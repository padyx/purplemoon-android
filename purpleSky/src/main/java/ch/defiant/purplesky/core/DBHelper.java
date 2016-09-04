package ch.defiant.purplesky.core;

import static ch.defiant.purplesky.constants.DatabaseConstants.MessageTable;
import static ch.defiant.purplesky.constants.DatabaseConstants.ConversationTable;
import static ch.defiant.purplesky.constants.DatabaseConstants.BundleStoreTable;
import static ch.defiant.purplesky.constants.DatabaseConstants.UserMappingTable;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

import ch.defiant.purplesky.constants.DatabaseConstants;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "purplesky.db";

    /**
     * The version of the database. For every release, this field must be increased if the schema was changed.
     *
     * Version 1&2: pre-release versions
     * Version 3: App version 29 (1.0.0)
     * Version 4: App version 30 (1.0.1)
     * Version 5: App version 1600000135 (1.3.5) - c
     */
    private static final int SCHEMA_VERSION = 5;

    public static DBHelper fromContext(Context c){
        return new DBHelper(c);
    }
    
    /**
     * Internal constructor.
     * @deprecated Use {@link #fromContext(Context)} instead
     * @param c
     */
    @Deprecated
    public DBHelper(Context c) {
        super(c, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createMessageTable(db);
        createConversationTable(db);
        createUserNameMappingTable(db);
        createBundleStoreTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion){
            case 4:
                // Delete Message Table, recreate
                dropTable(db, MessageTable.TABLE_MESSAGES);
                createMessageTable(db);
            case 3:
                createBundleStoreTable(db);
            case 2:
                dropTable(db, UserMappingTable.TABLE_USERMAPPING);
                createConversationTable(db);
            case 1:
                createConversationTable(db);
                createUserNameMappingTable(db);
            default:
                // No operation needed.
        }
    }

    private void dropTable(SQLiteDatabase db, String table) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE "+table);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public @NonNull Set<String> getAllTables(){
        Set<String> s = new HashSet<>();
        s.add(DatabaseConstants.BundleStoreTable.TABLE_BUNDLESTORE);
        s.add(DatabaseConstants.ConversationTable.TABLE_CONVERSATIONS);
        s.add(DatabaseConstants.MessageTable.TABLE_MESSAGES);
        s.add(DatabaseConstants.UserMappingTable.TABLE_USERMAPPING);
        return s;
    }

    /**
     * Creates the message table and associated indices.
     * @param db
     * @since Database version 1
     */
    private void createMessageTable(SQLiteDatabase db){
        db.beginTransaction();
        try {
            db.execSQL("    CREATE TABLE IF NOT EXISTS " + DatabaseConstants.MessageTable.TABLE_MESSAGES + " (" +
                    "         " + MessageTable.MESSAGES_PK + " INTEGER PRIMARY KEY," +
                    "         " + MessageTable.MESSAGES_MESSAGEID + " INTEGER," +
                    "         " + MessageTable.MESSAGES_FROMUSERID + " INTEGER NOT NULL," +
                    "         " + MessageTable.MESSAGES_TOUSERID + " INTEGER NOT NULL," +
                    "         " + MessageTable.MESSAGES_TIMESENT + " INTEGER NOT NULL," +
                    "         " + MessageTable.MESSAGES_LASTATTEMPT + " INTEGER, " +
                    "         " + MessageTable.MESSAGES_NEXTATTEMPT + " INTEGER, " +
                    "         " + MessageTable.MESSAGES_STATUS + " INTEGER, " + // FIXME Changed
                    "         " + MessageTable.MESSAGES_ATTEMPT_COUNT + " INTEGER, " +
                    "         " + MessageTable.MESSAGES_TEXT + " TEXT ); ");
            db.execSQL("    CREATE INDEX IF NOT EXISTS " + MessageTable.MESSAGES_INDEX_FROMUSERID + " ON " + MessageTable.TABLE_MESSAGES + " (" + MessageTable.MESSAGES_FROMUSERID + ");");
            db.execSQL("    CREATE INDEX IF NOT EXISTS " + MessageTable.MESSAGES_INDEX_TOUSERID + " ON " + MessageTable.TABLE_MESSAGES + " (" + MessageTable.MESSAGES_TOUSERID + ");");
            db.execSQL("    CREATE INDEX IF NOT EXISTS " + MessageTable.MESSAGES_INDEX_SENT + " ON " + MessageTable.TABLE_MESSAGES + " (" + MessageTable.MESSAGES_TIMESENT + " DESC);");

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Creates the conversation table and associated indices.
     * @param db
     * @since Database version 2
     */
    private void createConversationTable(SQLiteDatabase db){
        db.beginTransaction();
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + ConversationTable.TABLE_CONVERSATIONS + " (" +
                    "         " + ConversationTable.CONVERSATIONS_OTHERUSERID + " INTEGER PRIMARY KEY," +
                    "         " + ConversationTable.CONVERSATIONS_LASTCONTACT + " INTEGER NOT NULL, " +
                    "         " + ConversationTable.CONVERSATIONS_UNREADCOUNT + " INTEGER NOT NULL, " +
                    "         " + ConversationTable.CONVERSATIONS_EXCERPT + " TEXT )");
            db.execSQL("CREATE INDEX IF NOT EXISTS " + ConversationTable.CONVERSATIONS_INDEX_LASTCONTACT + " ON " + ConversationTable.TABLE_CONVERSATIONS + " (" + ConversationTable.CONVERSATIONS_LASTCONTACT + " DESC);");
            
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Creates the mapping table between userId and cached properties
     */
    private void createUserNameMappingTable(SQLiteDatabase db){
        db.beginTransaction();
        try{
            db.execSQL("CREATE TABLE IF NOT EXISTS " + UserMappingTable.TABLE_USERMAPPING + " (" +
                    "        " + UserMappingTable.USERMAPPING_USERID + " INTEGER PRIMARY KEY, " +
                    "        " + UserMappingTable.USERMAPPING_USERNAME + " TEXT, " +
                    "        " + UserMappingTable.USERMAPPING_PROFILEPICTURE_URL + " TEXT, " +
                    "        " + UserMappingTable.USERMAPPING_INSERTED + " INTEGER NOT NULL )");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        
    }

    /**
     * Creates the bundle store table
     * @param db
     * @since  Database version 3
     */
    private void createBundleStoreTable(SQLiteDatabase db){
        db.beginTransaction();
        try{
            db.execSQL("    CREATE TABLE IF NOT EXISTS "+BundleStoreTable.TABLE_BUNDLESTORE +
                    " ( " +
                    "       " + BundleStoreTable.BUNDLESTORE_OWNER + " TEXT, " +
                    "       " + BundleStoreTable.BUNDLESTORE_KEY + " TEXT, " +
                    "       " + BundleStoreTable.BUNDLESTORE_TYPE + " TEXT NOT NULL, " +
                    "       " + BundleStoreTable.BUNDLESTORE_CVALUE + " TEXT, " +
                    "       " + BundleStoreTable.BUNDLESTORE_NVALUE + " INTEGER, " +
                    "       " + BundleStoreTable.BUNDLESTORE_FVALUE + " REAL " +
                    " " +
                    ", CONSTRAINT PK_BUNDLESTORE PRIMARY KEY ("+
                        BundleStoreTable.BUNDLESTORE_OWNER +", " +
                        BundleStoreTable.BUNDLESTORE_KEY+") " +
                    ")"
            );
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void delete(@NonNull Context c) {
        close();
        c.deleteDatabase(DATABASE_NAME);
    }
}
