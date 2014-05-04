package ch.defiant.purplesky.constants;

public class DatabaseConstants {
    public static final String TABLE_MESSAGES = "messages";
    public static final String MESSAGES_MESSAGEID = "message_id";
    public static final String MESSAGES_FROMUSERID = "from_user_id";
    public static final String MESSAGES_TOUSERID = "to_user_id";
    public static final String MESSAGES_TIMESENT = "time_sent";
    public static final String MESSAGES_PENDING = "pending";
    public static final String MESSAGES_TEXT = "message_text";

    public static final String MESSAGES_INDEX_FROMUSERID = "messages_from_idx";
    public static final String MESSAGES_INDEX_TOUSERID = "messages_to_idx";
    public static final String MESSAGES_INDEX_SENT = "messages_time_sent_idx";

    public static final int MESSAGES_PENDING_NONE = 0;
    public static final int MESSAGES_PENDING_UNSENT = 1;
    /**
     * Indicates that this message was sent, but there may be
     */
    public static final int MESSAGES_PENDING_SENT_UNCONFIRMED = 2;
    /**
     * Indicates that this message was sent, and the directly preceding messages have
     */
    public static final int MESSAGES_PENDING_SENT = 3;

    
    public static final String TABLE_CONVERSATIONS = "conversations";
    public static final String CONVERSATIONS_OTHERUSERID = "otheruser_id";
    public static final String CONVERSATIONS_LASTCONTACT = "last_contact";
    public static final String CONVERSATIONS_UNREADCOUNT = "unread_count";
    public static final String CONVERSATIONS_EXCERPT = "excerpt";

    public static final String CONVERSATIONS_INDEX_LASTCONTACT = "conversations_last_contact_idx";
    
    public static final String TABLE_USERMAPPING = "usermapping";
    public static final String USERMAPPING_USERID = "user_id";
    public static final String USERMAPPING_USERNAME = "user_name";
    public static final String USERMAPPING_PROFILEPICTURE_URL = "profilepicture_url";
    public static final String USERMAPPING_INSERTED = "inserted";

}
