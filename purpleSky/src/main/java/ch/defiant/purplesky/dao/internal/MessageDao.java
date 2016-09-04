package ch.defiant.purplesky.dao.internal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;

import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.core.DBHelper;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.dao.IMessageDao;
import static ch.defiant.purplesky.constants.DatabaseConstants.MessageTable;

/**
 * @author Patrick Bänziger
 * @since 1.4
 */
class MessageDao implements IMessageDao {

    @Override
    public void create(@NonNull PrivateMessage message) {
       create(Collections.singleton(message));
    }

    @Override
    public void create(@NonNull Collection<PrivateMessage> messages) {
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();

        try {
            db.beginTransaction();

            for(PrivateMessage m: messages) {

                // FIXME pbn This should be in the same table
                ContentValues cVal = new ContentValues();
                // PK inserted automatically
                cVal.put(MessageTable.MESSAGES_MESSAGEID, m. getMessageHead().getMessageId());
                cVal.put(MessageTable.MESSAGES_FROMUSERID, Long.valueOf(m.getMessageHead().getAuthorProfileId()));
                cVal.put(MessageTable.MESSAGES_TOUSERID, Long.valueOf(m.getMessageHead().getRecipientProfileId()));
                if(m.getTimeSent() != null) {
                    cVal.put(MessageTable.MESSAGES_TIMESENT, m.getTimeSent().getTime());
                } else {
                    cVal.putNull(MessageTable.MESSAGES_TIMESENT);
                }
                if(m.getNextSendAttempt() != null) {
                    cVal.put(MessageTable.MESSAGES_NEXTATTEMPT, m.getNextSendAttempt().getTime());
                } else {
                    cVal.putNull(MessageTable.MESSAGES_NEXTATTEMPT);
                }
                cVal.put(MessageTable.MESSAGES_STATUS, m.getStatus().getId());
                cVal.put(MessageTable.MESSAGES_ATTEMPT_COUNT, m.getRetryCount());
                cVal.put(MessageTable.MESSAGES_TEXT, m.getMessageText());
                db.insert(MessageTable.TABLE_MESSAGES, null, cVal);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void updateStatus(@NonNull IPrivateMessage message){
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();

        try {
            db.beginTransaction();
            ContentValues cVal = new ContentValues();
            // PK inserted automatically
            cVal.put(MessageTable.MESSAGES_MESSAGEID, message.getMessageId());
            cVal.put(MessageTable.MESSAGES_NEXTATTEMPT, message.getNextSendAttempt().getTime());
            cVal.put(MessageTable.MESSAGES_ATTEMPT_COUNT, message.getRetryCount());
            // cVal.put(MessageTable.MESSAGES_LASTATTEMPT, message.getMessageHead().getTimeSent().getTime());
            cVal.put(MessageTable.MESSAGES_TIMESENT, message.getTimeSent().getTime());
            cVal.put(MessageTable.MESSAGES_STATUS, message.getStatus().getId());

            db.update(MessageTable.TABLE_MESSAGES, cVal, "WHERE "+MessageTable.MESSAGES_PK+" = ?",
                    new String[]{String.valueOf(message.getInternalId())});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }

    }
}
