package ch.defiant.purplesky.dao.internal;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.Date;

import ch.defiant.purplesky.beans.PendingMessage;
import ch.defiant.purplesky.constants.DatabaseConstants;
import ch.defiant.purplesky.core.DBHelper;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.dao.IPendingMessageDao;

/**
 * @author Patrick Bänziger
 */
class PendingMessageDao implements IPendingMessageDao {

    @NonNull
    @Override
    public PendingMessage create(@NonNull PendingMessage message) {
        // Insert into database
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues cVal = new ContentValues();
            // ID: Automatically set
            cVal.put(DatabaseConstants.PENDING_MESSAGES_STATUS, DatabaseConstants.PENDING_MESSAGES_UNSENT);
            long currentTime = new Date().getTime();
            cVal.put(DatabaseConstants.PENDING_MESSAGES_TIMESENT, currentTime);
            cVal.put(DatabaseConstants.PENDING_MESSAGES_NEXTATTEMPT, currentTime);
            cVal.put(DatabaseConstants.PENDING_ATTEMPT_COUNT, 0);
            cVal.put(DatabaseConstants.PENDING_MESSAGES_TOUSERID, message.getRecipientId());
            cVal.put(DatabaseConstants.PENDING_MESSAGES_TEXT, message.getMessageText());

            message.setId(db.insertOrThrow(DatabaseConstants.TABLE_PENDING_MESSAGES, null, cVal));
            db.setTransactionSuccessful();
            db.endTransaction();

            return message;
        } finally {
            db.close();
        }
    }
}
