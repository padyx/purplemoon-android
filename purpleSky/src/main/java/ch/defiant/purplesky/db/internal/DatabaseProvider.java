package ch.defiant.purplesky.db.internal;

import android.database.sqlite.SQLiteDatabase;

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

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return DBHelper.fromContext(PurpleSkyApplication.get()).getReadableDatabase();
    }
}
