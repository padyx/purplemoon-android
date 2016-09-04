package ch.defiant.purplesky.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.constants.DatabaseConstants;
import ch.defiant.purplesky.core.DBHelper;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.SendOptions;
import ch.defiant.purplesky.dao.IMessageDao;
import ch.defiant.purplesky.enums.MessageStatus;
import ch.defiant.purplesky.events.MessageDeliveryFailedEvent;
import ch.defiant.purplesky.events.MessageSentEvent;
import ch.defiant.purplesky.exceptions.PurpleSkyException;


/**
 * @author Patrick Bänziger
 * @since 1.4
 */
public class MessageSendingService extends IntentService {

    @Inject
    protected IMessageDao m_pendingMessageDao;

    private final int MAX_RETRY = 2;

    private enum SendResultStatus {
        OK,
        NETWORK_ERROR,
        BUSINESS_ERROR
    }

    private class SendResult {

        @Nullable
        private final SendResultStatus m_status;
        @NonNull
        private final PrivateMessage m_message;

        public SendResult(@Nullable SendResultStatus status, @Nullable PrivateMessage message){
            m_status = status;
            m_message = message;
        }

        @Nullable
        public SendResultStatus getStatus() {
            return m_status;
        }

        @NonNull
        public PrivateMessage getMessage() {
            return m_message;
        }
    }

    private static String TAG = MessageSendingService.class.getSimpleName();

    @Inject
    protected IConversationAdapter m_conversationAdapter;

    public MessageSendingService() {
        super(MessageSendingService.class.getSimpleName());
        PurpleSkyApplication.get().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        IPrivateMessage pendingMessage = getNextPendingMessage();
        while (pendingMessage != null) {
            Log.d(TAG, "Found pending message to send");

            SendResult sendResult = send(pendingMessage);

            SendResultStatus status = sendResult.getStatus();
            if(status == SendResultStatus.OK){
                // TODO Proper states
                PrivateMessage sentMessage = sendResult.getMessage();
                sentMessage.setStatus(MessageStatus.SENT);
                m_pendingMessageDao.updateStatus(sentMessage);
                // Fire event
                long sentMessageId = sentMessage.getMessageHead().getMessageId();
                MessageSentEvent event = new MessageSentEvent(pendingMessage.getInternalId(), sentMessageId);
                fireEvent(event);
            }
            else if (status == SendResultStatus.BUSINESS_ERROR) {
                Log.i(TAG, "Sending message failed - business error. Schedule for retry");
                rescheduleMessage(pendingMessage, true);
            } else if (status == SendResultStatus.NETWORK_ERROR){
                Log.i(TAG, "Sending message failed - network error. Schedule for retry");
                rescheduleMessage(pendingMessage, false);
            }
            pendingMessage = getNextPendingMessage();
        }
    }

    private void fireEvent(Object event) {
        EventBus.getDefault().post(event);
    }

    private void rescheduleMessage(IPrivateMessage message, boolean businessError){
        message.setRetryCount(message.getRetryCount() + (businessError ? 1 : 0));

        long lastBackoff = Math.max(15000, message.getNextSendAttempt().getTime() - message.getTimeSent().getTime());
        if(lastBackoff > 64*60*1000){
            message.setStatus(MessageStatus.FAILED);
            m_pendingMessageDao.updateStatus(message);
            fireEvent(new MessageDeliveryFailedEvent(message.getInternalId()));
            return;
        }
        long nextBackoffInterval = lastBackoff;
        nextBackoffInterval *= 2;
        long nextAttempt = new Date().getTime() + nextBackoffInterval;

        // Update reschedule time
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();
        try {
            db.beginTransaction();

            // TODO Suboptimal backoff (only this recipient): May be inefficient if the network is unavailable

            // Update all messages for this recipient to try again at the same point in time
            // This way, attempts should be fairly distributed to all other recipients...
            ContentValues nextAttemptValues = new ContentValues();
            nextAttemptValues.put(DatabaseConstants.MessageTable.MESSAGES_NEXTATTEMPT, nextAttempt);
            db.update(
                    DatabaseConstants.MessageTable.TABLE_MESSAGES,
                    nextAttemptValues,
                    DatabaseConstants.MessageTable.MESSAGES_TOUSERID + " = ? "+ // FIXME Needs additional constraint
                    DatabaseConstants.MessageTable.MESSAGES_STATUS + " IN ( ? , ? ) ",
                    new String[]{
                            String.valueOf(message.getRecipientId()),
                            String.valueOf(MessageStatus.NEW),
                            String.valueOf(MessageStatus.RETRY_NEEDED),
                    }
            );

            // Update retry count of this message if it is a business error
            if(businessError) {
                ContentValues values = new ContentValues();
                values.put(DatabaseConstants.MessageTable.MESSAGES_NEXTATTEMPT, nextAttempt);
                values.put(DatabaseConstants.MessageTable.MESSAGES_ATTEMPT_COUNT, message.getRetryCount());
                db.update(
                        DatabaseConstants.MessageTable.TABLE_MESSAGES,
                        values,
                        DatabaseConstants.MessageTable.MESSAGES_PK + " = ?",
                        new String[] {String.valueOf(message.getInternalId())}
                );
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } finally {
            db.close();
        }

        scheduleRerun(nextBackoffInterval);
    }

    /**
     * Schedule an intent for this
     * @param nextBackoffInterval
     */
    private void scheduleRerun(long nextBackoffInterval) {
        AlarmManager service = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, MessageSendingService.class);

        PendingIntent pending = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        service.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextBackoffInterval+5000, pending);
    }

    @NonNull
    private SendResult send(@NonNull IPrivateMessage pendingMessage) {
        SendOptions sendOptions = new SendOptions();
        sendOptions.setUnreadHandling(SendOptions.UnreadHandling.SEND);
        try {
            MessageResult result = m_conversationAdapter.sendMessage(pendingMessage, sendOptions);
            PrivateMessage sentMessage = (PrivateMessage) result.getSentMessage();
            if (sentMessage != null) {
                return new SendResult(SendResultStatus.OK, sentMessage);
            } else {
                return new SendResult (SendResultStatus.BUSINESS_ERROR, null);
            }
        } catch (IOException e) {
            return new SendResult(SendResultStatus.NETWORK_ERROR, null);
        } catch (PurpleSkyException e){
            return new SendResult(SendResultStatus.BUSINESS_ERROR, null);
        }
    }

    @Nullable
    private IPrivateMessage getNextPendingMessage() {
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getReadableDatabase();

        try {
            // Find recipient with soonest pending message (that is not in the failed state)
            Cursor recipientIdQuery = db.query(false,
                    DatabaseConstants.MessageTable.TABLE_MESSAGES,
                    new String[] {DatabaseConstants.MessageTable.MESSAGES_TOUSERID},
                    DatabaseConstants.MessageTable.MESSAGES_STATUS + " NOT IN ( ?, ?)",
                    new String[]{String.valueOf(MessageStatus.FAILED), String.valueOf(MessageStatus.SENT)},
                    null,
                    null,
                    DatabaseConstants.MessageTable.MESSAGES_NEXTATTEMPT + " ASC ",
                    "1");
            boolean hasData = recipientIdQuery.moveToFirst();
            if(!hasData){
                return null;
            }

            long recipientId = recipientIdQuery.getLong(0);
            recipientIdQuery.close();

            // Now, get the next pending message for this recipient
            Cursor query = db.query(false,
                    DatabaseConstants.MessageTable.TABLE_MESSAGES,
                    new String[] {
                            DatabaseConstants.MessageTable.MESSAGES_PK,
                            DatabaseConstants.MessageTable.MESSAGES_MESSAGEID,
                            DatabaseConstants.MessageTable.MESSAGES_TOUSERID,
                            DatabaseConstants.MessageTable.MESSAGES_STATUS,
                            DatabaseConstants.MessageTable.MESSAGES_ATTEMPT_COUNT,
                            DatabaseConstants.MessageTable.MESSAGES_TIMESENT,
                            DatabaseConstants.MessageTable.MESSAGES_NEXTATTEMPT,
                            DatabaseConstants.MessageTable.MESSAGES_TEXT},
                    DatabaseConstants.MessageTable.MESSAGES_TOUSERID + " = ? AND "
                    + DatabaseConstants.MessageTable.MESSAGES_STATUS + " NOT IN (?, ?) AND "
                    + DatabaseConstants.MessageTable.MESSAGES_NEXTATTEMPT + " < ? ",
                    new String[] {
                            String.valueOf(recipientId),
                            String.valueOf(MessageStatus.FAILED.getId()),
                            String.valueOf(MessageStatus.SENT.getId()),
                            String.valueOf(System.currentTimeMillis())
                    },
                    null,
                    null,
                    DatabaseConstants.MessageTable.MESSAGES_TIMESENT + " ASC ",
                    "1");

            hasData = query.moveToFirst();
            if (!hasData) {
                query.close();
                return null;
            } else {
                PrivateMessage message = new PrivateMessage();
                message.setInternalId(query.getLong(0));
                message.setMessageId(query.getLong(1));
                message.setRecipientId(query.getString(2));
                message.setStatus(MessageStatus.getById(query.getInt(3)));
                message.setRetryCount(query.getInt(4));
                message.setTimeSent(new Date(query.getLong(5)));
                message.setNextSendAttempt(new Date(query.getLong(6)));
                message.setMessageText(query.getString(7));
                query.close();
                return message;
            }
        } finally {
            db.close();
        }
    }


}

