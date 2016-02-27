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

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.PendingMessage;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.constants.DatabaseConstants;
import ch.defiant.purplesky.core.DBHelper;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.SendOptions;
import ch.defiant.purplesky.exceptions.PurpleSkyException;


/**
 * @author Patrick Bänziger
 * @since 1.4
 */
public class MessageSendingService extends IntentService {

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
        PendingMessage pendingMessage = getNextPendingMessage();
        while (pendingMessage != null) {
            Log.d(TAG, "Found pending message to send");

            SendResult sendResult = send(pendingMessage);

            SendResultStatus status = sendResult.getStatus();
            if(status == SendResultStatus.OK){
                deletePendingAndInsertFinalMessage(pendingMessage, (PrivateMessage) sendResult.getMessage());
                // FIMXE Notify UI
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

    public void rescheduleMessage(PendingMessage message, boolean businessError){
        message.setRetryCount(message.getRetryCount() + (businessError ? 1 : 0));

        long lastBackoff = Math.max(15000, message.getNextSendAttempt().getTime() - message.getTimeSent().getTime());
        if(lastBackoff > 64*60*1000){
            updateStatus(message, PendingMessage.Status.FAILED);
            // FIXME Notify UI
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
            nextAttemptValues.put(DatabaseConstants.PENDING_MESSAGES_NEXTATTEMPT, nextAttempt);
            db.update(
                    DatabaseConstants.TABLE_PENDING_MESSAGES,
                    nextAttemptValues,
                    DatabaseConstants.PENDING_MESSAGES_TOUSERID + " = ?",
                    new String[]{String.valueOf(message.getRecipientId())}
            );

            // Update retry count of this message if it is a business error
            if(businessError) {
                ContentValues values = new ContentValues();
                values.put(DatabaseConstants.PENDING_MESSAGES_NEXTATTEMPT, nextAttempt);
                values.put(DatabaseConstants.PENDING_ATTEMPT_COUNT, message.getRetryCount());
                db.update(
                        DatabaseConstants.TABLE_PENDING_MESSAGES,
                        values,
                        DatabaseConstants.PENDING_MESSAGES_ID + " = ?",
                        new String[] {String.valueOf(message.getId())}
                );
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } finally {
            db.close();
        }


        scheduleRerun(nextBackoffInterval);
    }

    private void updateStatus(@NonNull PendingMessage message, @NonNull PendingMessage.Status status){
        message.setStatus(status);
        // Update reschedule time
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();
        try {
            db.beginTransaction();

            ContentValues nextAttemptValues = new ContentValues();
            nextAttemptValues.put(DatabaseConstants.PENDING_MESSAGES_STATUS, status.getId());
            db.update(
                    DatabaseConstants.TABLE_PENDING_MESSAGES,
                    nextAttemptValues,
                    DatabaseConstants.PENDING_MESSAGES_ID + " = ?",
                    new String[] {String.valueOf(message.getId())}
            );
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
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

    private void deletePendingAndInsertFinalMessage(@NonNull PendingMessage message, @NonNull PrivateMessage sendMessage) {
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();

        try {
            db.beginTransaction();
            db.delete(
                    DatabaseConstants.TABLE_PENDING_MESSAGES,
                    DatabaseConstants.PENDING_MESSAGES_ID + " = ?",
                    new String[] {message.getId().toString()}
            );
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    @NonNull
    private SendResult send(@NonNull PendingMessage pendingMessage) {
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
    private PendingMessage getNextPendingMessage() {
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getReadableDatabase();

        try {
            // Find recipient with soonest pending message (that is not in the failed state)
            Cursor recipientIdQuery = db.query(false,
                    DatabaseConstants.TABLE_PENDING_MESSAGES,
                    new String[] {DatabaseConstants.PENDING_MESSAGES_TOUSERID},
                    DatabaseConstants.PENDING_MESSAGES_STATUS + " != ?",
                    new String[]{String.valueOf(DatabaseConstants.PENDING_MESSAGES_FAILED)},
                    null,
                    null,
                    DatabaseConstants.PENDING_MESSAGES_NEXTATTEMPT + " ASC ",
                    "1");
            boolean hasData = recipientIdQuery.moveToFirst();
            if(!hasData){
                return null;
            }

            long recipientId = recipientIdQuery.getLong(0);
            recipientIdQuery.close();

            // Now, get the next pending message for this recipient
            Cursor query = db.query(false,
                    DatabaseConstants.TABLE_PENDING_MESSAGES,
                    new String[] {
                            DatabaseConstants.PENDING_MESSAGES_ID,
                            DatabaseConstants.PENDING_MESSAGES_TOUSERID,
                            DatabaseConstants.PENDING_MESSAGES_STATUS,
                            DatabaseConstants.PENDING_ATTEMPT_COUNT,
                            DatabaseConstants.PENDING_MESSAGES_TIMESENT,
                            DatabaseConstants.PENDING_MESSAGES_NEXTATTEMPT,
                            DatabaseConstants.PENDING_MESSAGES_TEXT},
                    DatabaseConstants.PENDING_MESSAGES_TOUSERID + " = ? AND "
                    + DatabaseConstants.PENDING_MESSAGES_STATUS + " != ? AND "
                    + DatabaseConstants.PENDING_MESSAGES_NEXTATTEMPT + " < ? ",
                    new String[] {
                            String.valueOf(recipientId),
                            String.valueOf(DatabaseConstants.PENDING_MESSAGES_FAILED),
                            String.valueOf(System.currentTimeMillis())
                    },
                    null,
                    null,
                    DatabaseConstants.PENDING_MESSAGES_TIMESENT + " ASC ",
                    "1");

            hasData = query.moveToFirst();
            if (!hasData) {
                query.close();
                return null;
            } else {
                PendingMessage message = new PendingMessage();
                message.setId(query.getLong(0));
                message.setRecipientId(query.getLong(1));
                message.setStatus(PendingMessage.Status.getById(query.getInt(2)));
                message.setRetryCount(query.getInt(3));
                message.setTimeSent(new Date(query.getLong(4)));
                message.setNextSendAttempt(new Date(query.getLong(5)));
                message.setMessageText(query.getString(6));
                query.close();
                return message;
            }
        } finally {
            db.close();
        }
    }


}

