package ch.defiant.purplesky.core;

import static ch.defiant.purplesky.constants.DatabaseConstants.CONVERSATIONS_EXCERPT;
import static ch.defiant.purplesky.constants.DatabaseConstants.CONVERSATIONS_INDEX_LASTCONTACT;
import static ch.defiant.purplesky.constants.DatabaseConstants.CONVERSATIONS_LASTCONTACT;
import static ch.defiant.purplesky.constants.DatabaseConstants.CONVERSATIONS_OTHERUSERID;
import static ch.defiant.purplesky.constants.DatabaseConstants.MESSAGES_FROMUSERID;
import static ch.defiant.purplesky.constants.DatabaseConstants.MESSAGES_INDEX_FROMUSERID;
import static ch.defiant.purplesky.constants.DatabaseConstants.MESSAGES_INDEX_SENT;
import static ch.defiant.purplesky.constants.DatabaseConstants.MESSAGES_INDEX_TOUSERID;
import static ch.defiant.purplesky.constants.DatabaseConstants.MESSAGES_MESSAGEID;
import static ch.defiant.purplesky.constants.DatabaseConstants.MESSAGES_PENDING;
import static ch.defiant.purplesky.constants.DatabaseConstants.MESSAGES_TEXT;
import static ch.defiant.purplesky.constants.DatabaseConstants.MESSAGES_TIMESENT;
import static ch.defiant.purplesky.constants.DatabaseConstants.MESSAGES_TOUSERID;
import static ch.defiant.purplesky.constants.DatabaseConstants.TABLE_CONVERSATIONS;
import static ch.defiant.purplesky.constants.DatabaseConstants.TABLE_MESSAGES;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ch.defiant.purplesky.constants.DatabaseConstants;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "purplesky.db";

    /**
     * The version of the database. For every release, this field must be increased if the schema was changed.
     */
    private static final int SCHEMA_VERSION = 3;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion){
            case 2:
                dropTable(db, DatabaseConstants.TABLE_USERMAPPING);
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

    /**
     * Creates the message table and associated indices.
     * @param db
     * @since Database version 1
     */
    private void createMessageTable(SQLiteDatabase db){
        db.beginTransaction();
        try {
            db.execSQL("    CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGES + " (" +
                    "         " + MESSAGES_MESSAGEID + " INTEGER PRIMARY KEY," +
                    "         " + MESSAGES_FROMUSERID + " INTEGER NOT NULL," +
                    "         " + MESSAGES_TOUSERID + " INTEGER NOT NULL," +
                    "         " + MESSAGES_TIMESENT + " INTEGER NOT NULL," +
                    "         " + MESSAGES_PENDING + " INTEGER NOT NULL," +
                    "         " + MESSAGES_TEXT + " TEXT ); ");
            db.execSQL("    CREATE INDEX IF NOT EXISTS " + MESSAGES_INDEX_FROMUSERID + " ON " + TABLE_MESSAGES + " (" + MESSAGES_FROMUSERID + ");");
            db.execSQL("    CREATE INDEX IF NOT EXISTS " + MESSAGES_INDEX_TOUSERID + " ON " + TABLE_MESSAGES + " (" + MESSAGES_TOUSERID + ");");
            db.execSQL("    CREATE INDEX IF NOT EXISTS " + MESSAGES_INDEX_SENT + " ON " + TABLE_MESSAGES + " (" + MESSAGES_TIMESENT + " DESC);");

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
            db.execSQL("    CREATE TABLE IF NOT EXISTS " + TABLE_CONVERSATIONS + " (" +
                    "         " + CONVERSATIONS_OTHERUSERID + " INTEGER PRIMARY KEY," +
                    "         " + CONVERSATIONS_LASTCONTACT + " INTEGER NOT NULL, " +
                    "         " + DatabaseConstants.CONVERSATIONS_UNREADCOUNT + " INTEGER NOT NULL, " +
                    "         " + CONVERSATIONS_EXCERPT + " TEXT )");
            db.execSQL("    CREATE INDEX IF NOT EXISTS " + CONVERSATIONS_INDEX_LASTCONTACT + " ON " + TABLE_CONVERSATIONS + " (" + CONVERSATIONS_LASTCONTACT + " DESC);");
            
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
            db.execSQL("    CREATE TABLE IF NOT EXISTS " + DatabaseConstants.TABLE_USERMAPPING + " (" +
                    "        " + DatabaseConstants.USERMAPPING_USERID + " INTEGER PRIMARY KEY, " +
                    "        " + DatabaseConstants.USERMAPPING_USERNAME + " TEXT, " +
                    "        " + DatabaseConstants.USERMAPPING_PROFILEPICTURE_URL + " TEXT, " +
                    "        " + DatabaseConstants.USERMAPPING_INSERTED + " INTEGER NOT NULL )");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        
    }
}
