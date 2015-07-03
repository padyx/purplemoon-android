package ch.defiant.purplesky.db.internal;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Set;

import ch.defiant.purplesky.core.DBHelper;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.db.IDatabaseProvider;

/**
 * Implementation of provider that returns new instances of the database.
 * Ensure that you close every instance you obtain from this provider.
 * @author Patrick Bänziger
 * @since 1.0.1
 */
class DatabaseProvider implements IDatabaseProvider {

    private static String TAG = DatabaseProvider.class.getSimpleName();

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getDbHelper().getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getDbHelper().getReadableDatabase();
    }

    @NonNull
    private DBHelper getDbHelper() {
        return DBHelper.fromContext(PurpleSkyApplication.get());
    }

    public void truncateAllTables() {
        SQLiteDatabase db = getWritableDatabase();

        Set<String> tables = getDbHelper().getAllTables();
        for ( String table: tables ) {
            db.beginTransaction();
            try{
                db.delete(table,null,null);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        db.close();
    }

}
