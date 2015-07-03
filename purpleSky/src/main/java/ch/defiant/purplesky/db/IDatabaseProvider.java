package ch.defiant.purplesky.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Provider to get database.
 * @author Patrick Bänziger
 * @since 1.0.1
 */
public interface IDatabaseProvider {

    SQLiteDatabase getWritableDatabase();

    SQLiteDatabase getReadableDatabase();

    /**
     * Deletes all records from all tables.
     */
    void truncateAllTables();
}
