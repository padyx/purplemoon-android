package ch.defiant.purplesky.core.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessageHead;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.beans.util.UserMessageHistoryBeanLastContactComparator;
import ch.defiant.purplesky.constants.DatabaseConstants;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.DBHelper;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.enums.MessageRetrievalRestrictionType;
import ch.defiant.purplesky.enums.MessageType;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.CollectionUtil;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.NVLUtility;

// TODO Rename
class MessageService implements IMessageService {

    private final IConversationAdapter apiAdapter;

    public MessageService(IConversationAdapter apiAdapter){
        this.apiAdapter = apiAdapter;
    }

    private final int BATCH_MOREMESSAGES = 100;

    private final long THREE_MONTHS = 3*30*24*60*60*1000L; // The "L" here is important, otherwise hello integer overflow!
    private final long YEAR = 12*30*24*60*60*1000L;

    private final String TAG = MessageService.class.getSimpleName();

    private final String FROM_TO_STRING = "( " + DatabaseConstants.MESSAGES_FROMUSERID
            + " = ? OR " + DatabaseConstants.MESSAGES_TOUSERID + " = ? )";

    private final String UPDATE_LAST_CONTACT = 
            " ? = " + DatabaseConstants.CONVERSATIONS_OTHERUSERID + 
            " AND " + DatabaseConstants.CONVERSATIONS_LASTCONTACT + " <= ?";

    private final int MAX_CACHED_CONVERSATIONS = 100;

    /**
     * Returns up tp {@link #BATCH} messages that directly preceeded the messages indicated by <code>messageId</code>. This function issues an online
     * call. The returned order is oldest first.
     * 
     * @param profileId
     *            The profile id of the other user
     * @param messageId
     *            Message id being the upper bound of the messages to fetch.
     * @return
     */
    @Override
    public Holder<List<PrivateMessage>> getPreviousMessagesOnline(String profileId, long messageId) {
        AdapterOptions opts = new AdapterOptions();
        opts.setNumber(BATCH_MOREMESSAGES);
        opts.setOrder(PurplemoonAPIConstantsV1.MESSAGE_CHATSHOW_ORDER_NEWESTFIRST);
        opts.setUptoId(messageId);
        try {
            List<PrivateMessage> list = apiAdapter.getRecentMessagesByUser(profileId, opts);
            if (list != null && !list.isEmpty()) {
                insertMessages(list);
            }
            Collections.reverse(list); // We need oldest first
            return new Holder<>(list);
        } catch (Exception e) {
            return new Holder<>(e);
        }
    }

    /**
     * Returns the newest messages with the specified user, sorts the result in oldest first order.
     * @param profileId
     * @return messages
     */
    @Override
    public List<PrivateMessage> getNewestCachedMessagesWithUser(String profileId){
        return getCachedMessagesWithUser(profileId, null);
    }

    /**
     * Get a batch of previous message that went directly before the message having the <code>messageId</code>. <br/>
     * Order is oldest first
     * 
     * @param profileId
     *            The profile id of the other user
     * @param messageId
     *            Message id being the upper bound of the messages to fetch.
     */
    @Override
    public List<PrivateMessage> getPreviousCachedMessagesWithUser(String profileId, long messageId) {
        return getCachedMessagesWithUser(profileId, messageId);
    }

    /**
     * Get all messages that came after the message having the <code>lastMessageId</code>. This will result in an online call in any case. If
     * <code>lastMessageId</code> is null, then the most recent messages will be returned. Otherwise the messages that will be returned are those that
     * directly follow the specified id.
     * 
     * @param profileId
     *            The profile id of the other user
     * @param lastMessageId
     *            Message id being the lower bound of the messages to fetch.
     */
    @Override
    public Holder<List<PrivateMessage>> getNewMessagesFromUser(String profileId, Long lastMessageId) {
        final List<PrivateMessage> res = new ArrayList<>();

        boolean hasPossiblyMore = lastMessageId != null;

        // Fetch until the first time we get an empty list
        AdapterOptions opts = new AdapterOptions();
        opts.setNumber(BATCH);
        while (true) {
            // In case that we have NULL, we don't have anything yet. Get newest
            if (lastMessageId != null) {
                opts.setOrder(PurplemoonAPIConstantsV1.MESSAGE_CHATSHOW_ORDER_OLDESTFIRST);
                opts.setSinceId(lastMessageId);
            } else {
                opts.setOrder(PurplemoonAPIConstantsV1.MESSAGE_CHATSHOW_ORDER_NEWESTFIRST);
            }

            try {
                List<PrivateMessage> list = apiAdapter.getRecentMessagesByUser(profileId, opts);
                res.addAll(list);
                if (list.isEmpty()) {
                    hasPossiblyMore = false;
                } else {
                    if (lastMessageId != null) { // Only if we had sth before! Not for initial
                        // Last one newest!
                        lastMessageId = list.get(list.size() - 1).getMessageHead().getMessageId();
                    }
                }
            } catch (Exception e) {
                return new Holder<>(e);
            }
            if (!hasPossiblyMore) {
                break;
            }
        }

        if (lastMessageId == null) {
            // We have requested it in reverse order! Make sure that newest now first again
            Collections.reverse(res);
        }
        if (!res.isEmpty()) {
            insertMessages(res);
        }
        return new Holder<>(res);

    }

    @Override
    public void insertMessage(PrivateMessage m) {
        insertMessages(Collections.singletonList(m));
    }

    /**
     * Inserts messages into database.
     * 
     * @param list
     */
    @Override
    public void insertMessages(Collection<PrivateMessage> list) {
        if (list.isEmpty()) {
            return;
        }
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();
        try {
            db.beginTransaction();
            for (PrivateMessage m : list) {
                ContentValues cVal = new ContentValues();
                cVal.put(DatabaseConstants.MESSAGES_MESSAGEID, Long.valueOf(m.getMessageHead().getMessageId()));
                cVal.put(DatabaseConstants.MESSAGES_FROMUSERID, Long.valueOf(m.getMessageHead().getAuthorProfileId()));
                cVal.put(DatabaseConstants.MESSAGES_TOUSERID, Long.valueOf(m.getMessageHead().getRecipientProfileId()));
                cVal.put(DatabaseConstants.MESSAGES_TIMESENT, m.getMessageHead().getTimeSent().getTime());
                cVal.put(DatabaseConstants.MESSAGES_PENDING, false);
                cVal.put(DatabaseConstants.MESSAGES_TEXT, m.getMessageText());
                db.insert(DatabaseConstants.TABLE_MESSAGES, null, cVal);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * Returns the timestamp of the latest message received from the specified user.
     * 
     * @param profileId
     *            Profile Id of sending user.
     * @return Timestamp, or <tt>null</tt> if the specified user send no message.
     */
    @Override
    public Long getLatestReceivedMessageTimestamp(String profileId) {
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getReadableDatabase();

        try {
            db.beginTransaction();
            Cursor query = db.query(false,
                    DatabaseConstants.TABLE_MESSAGES,
                    new String[] {
                    DatabaseConstants.MESSAGES_TIMESENT
            },
            DatabaseConstants.MESSAGES_FROMUSERID + " = ? ",
            new String[] { profileId },
            null,
            null,
            DatabaseConstants.MESSAGES_MESSAGEID + " DESC ",
                    "1");

            boolean hasData = query.moveToFirst();
            if (!hasData) {
                query.close();
                return null;
            } else {
                long res = query.getLong(0);
                query.close();
                return res;
            }

        } finally {
            db.endTransaction();
            db.close();
        }

    }

    /**
     * Returns the id of the latest message transmitted between the users
     * 
     * @param profileId
     *            Profile Id of sending user.
     * @return Id, or <tt>null</tt> if the specified user send no message.
     */
    @Override
    public Long getLatestMessageId(String profileId) {
        if(profileId == null){
            return null;
        }
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getReadableDatabase();

        try {
            Cursor query = db.query(false,
                    DatabaseConstants.TABLE_MESSAGES,
                    new String[] {
                    DatabaseConstants.MESSAGES_MESSAGEID
            },
            FROM_TO_STRING,
            new String[] { profileId, profileId },
            null,
            null,
            DatabaseConstants.MESSAGES_MESSAGEID + " DESC ",
                    "1");

            boolean hasData = query.moveToFirst();
            if (!hasData) {
                query.close();
                return null;
            } else {
                long res = query.getLong(0);
                query.close();
                return res;
            }
        } finally {
            db.close();
        }

    }

    /**
     * Updates the last contact date for all conversation to the maximum of the stored value and the beans' last contact dates.
     * @param conversations List of conversations to update
     */
    @Override
    public void updateLastContact(Collection<UserMessageHistoryBean> conversations){
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();

        try{
            for (UserMessageHistoryBean bean : conversations) {
                    String profileId = NVLUtility.nvl(bean.getProfileId(), bean.getUserBean().getUserId());

                    ContentValues values = new ContentValues();
                    long timeMillis = bean.getLastContact().getTime();
                    values.put(DatabaseConstants.CONVERSATIONS_LASTCONTACT, timeMillis);
                    values.put(DatabaseConstants.CONVERSATIONS_EXCERPT, bean.getLastMessageExcerpt());
                    values.put(DatabaseConstants.CONVERSATIONS_UNREADCOUNT, bean.getUnopenedMessageCount());
                    values.put(DatabaseConstants.CONVERSATIONS_OTHERUSERID, profileId);

                    // Check if insert or update
                    Cursor cursor = db.query(DatabaseConstants.TABLE_CONVERSATIONS,
                            new String[]{DatabaseConstants.CONVERSATIONS_OTHERUSERID},
                            DatabaseConstants.CONVERSATIONS_OTHERUSERID + "= ?" ,
                            new String[]{profileId}, null, null, null, "1");
                    boolean exists = cursor.moveToFirst();
                    cursor.close();

                    try{
                        db.beginTransaction();
                        if(exists){
                            db.update(DatabaseConstants.TABLE_CONVERSATIONS, 
                                    values, UPDATE_LAST_CONTACT, 
                                    new String[]{
                                        profileId, 
                                        String.valueOf(timeMillis)
                                    }
                            );
                        } else {
                            db.insert(DatabaseConstants.TABLE_CONVERSATIONS, null, values);
                        }

                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
            }
        } finally {
            db.close();
        }
    }

    /**
     * Updates the mapping from userId to the username
     * @param user
     */
    @Override
    public void updateUserNameMapping(MinimalUser user){
        updateUserNameMapping(Collections.singletonList(user));
    }
    
    @Override
    public void updateUserNameMapping(Collection<MinimalUser> users){
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();
        try{
            db.beginTransaction();
            for (MinimalUser e : users) {
                ContentValues values = new ContentValues();
                values.put(DatabaseConstants.USERMAPPING_USERID, e.getUserId());
                values.put(DatabaseConstants.USERMAPPING_USERNAME, e.getUsername());
                if(e.getProfilePictureURLDirectory() != null){
                    values.put(DatabaseConstants.USERMAPPING_PROFILEPICTURE_URL, e.getProfilePictureURLDirectory().toString());
                }
                values.put(DatabaseConstants.USERMAPPING_INSERTED, new Date().getTime());
                db.insertWithOnConflict(DatabaseConstants.TABLE_USERMAPPING, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    
    @Override
    public String getUserNameForId(String userId){
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(DatabaseConstants.TABLE_USERMAPPING, new String[] { DatabaseConstants.USERMAPPING_USERNAME },
                    "? = " + DatabaseConstants.USERMAPPING_USERID, new String[] { userId }, null, null, null);
            if(cursor.isAfterLast()){
                return null;
            } else {
                cursor.moveToFirst();
                return cursor.getString(0);
            }
        } finally {
            if(cursor != null){
                cursor.close();
            }
            db.close();
        }
    }
    
    /**
     * Retrieve the profile picture URL prefix.
     * @param userId
     * @return
     */
    @Override
    public String getProfilePictureUrlForId(String userId){
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(DatabaseConstants.TABLE_USERMAPPING, new String[] { DatabaseConstants.USERMAPPING_PROFILEPICTURE_URL },
                    "? = " + DatabaseConstants.USERMAPPING_USERID, new String[] { userId }, null, null, null);
            if(cursor.isAfterLast()){
                return null;
            } else {
                cursor.moveToFirst();
                return cursor.getString(0);
            }
        } finally {
            if(cursor != null){
                cursor.close();
            }
            db.close();
        }
    }
    
    /**
     * Retrieve the profile picture URL prefix.
     * @param users
     * @return
     */
    @Override
    public void injectProfilePictureUrlForId(Collection<UserMessageHistoryBean> users){
        if(users == null || users.isEmpty()) {
            return;
        }
        
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getReadableDatabase();
        Cursor cursor = null;
        try{
            for (UserMessageHistoryBean b : users) {
                String userId;
                if(b.getUserBean() != null){
                    userId = b.getUserBean().getUserId();
                } else {
                    userId = b.getProfileId();
                }
                cursor = db.query(DatabaseConstants.TABLE_USERMAPPING, new String[] { DatabaseConstants.USERMAPPING_PROFILEPICTURE_URL },
                        "? = " + DatabaseConstants.USERMAPPING_USERID, new String[] { userId }, null, null, null);
                if(!cursor.isAfterLast()){
                    cursor.moveToFirst();
                    b.setCachedProfilePictureUrl(cursor.getString(0));
                }
                cursor.close();
                cursor = null;
            }
        } finally {
            if(cursor != null){
                cursor.close();
            }
            db.close();
        }
    }

    /**
     * Will delete old records from the cache tables in the DB.
     */
    @Override
    public void cleanupDB(){
        final SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();
        final long timeMillis = new Date().getTime();
        try{
            db.beginTransaction();
            // USER MAPPINGS
            // Delete old user id mappings ones (>3 months)
            int deleted = db.delete(
                    DatabaseConstants.TABLE_USERMAPPING,
                    DatabaseConstants.USERMAPPING_INSERTED + " < "+ (timeMillis - THREE_MONTHS),
                    new String[]{});
            if(deleted > 0){
                Log.d(TAG, "User mapping: Deleted "+deleted+" records");
            }
            if(deleted > 0){
                Log.d(TAG, "User mapping: Deleted "+deleted+" records");
            }

            // CONVERSATIONS
            // Delete those older than a year
            deleted = db.delete(
                    DatabaseConstants.TABLE_CONVERSATIONS,
                    DatabaseConstants.CONVERSATIONS_LASTCONTACT + " < "+ (timeMillis - YEAR),
                    new String[]{});
            if(deleted > 0){
                Log.d(TAG, "Conversation cache: Deleted "+deleted+" records");
            }

            // MESSAGES
            // Delete those older than a year
            deleted = db.delete(
                    DatabaseConstants.TABLE_MESSAGES,
                    DatabaseConstants.MESSAGES_TIMESENT + " < "+ (timeMillis - YEAR),
                    new String[]{});
            if(deleted > 0){
                Log.d(TAG, "Message cache: Deleted "+deleted+" records");
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * @return List of cached conversations, in descending time order of last contact. 
     */
    @Override
    public List<UserMessageHistoryBean> getCachedConversations(){
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getReadableDatabase();
        List<UserMessageHistoryBean> list = new ArrayList<>();
        try{
            Cursor cursor = db.rawQuery(
                    " SELECT " + 
                            " c."+ DatabaseConstants.CONVERSATIONS_OTHERUSERID + ", " +
                            " m."+ DatabaseConstants.USERMAPPING_USERNAME + ", "+
                            " c."+ DatabaseConstants.CONVERSATIONS_LASTCONTACT + ", " +
                            " c."+ DatabaseConstants.CONVERSATIONS_EXCERPT + ", " +
                            " c."+ DatabaseConstants.CONVERSATIONS_UNREADCOUNT + " " +
                            " FROM " + DatabaseConstants.TABLE_CONVERSATIONS + " c " +
                            " LEFT OUTER JOIN " + DatabaseConstants.TABLE_USERMAPPING + " m " +
                            " ON c." + DatabaseConstants.CONVERSATIONS_OTHERUSERID + " = m."+ DatabaseConstants.USERMAPPING_USERID + 
                            " ORDER BY c." + DatabaseConstants.CONVERSATIONS_LASTCONTACT + " DESC " +
                            " LIMIT ?"
                            , new String[]{String.valueOf(MAX_CACHED_CONVERSATIONS)});

            while(cursor.moveToNext()){
                UserMessageHistoryBean bean = new UserMessageHistoryBean();
                bean.setProfileId(cursor.getString(0));
                bean.setCachedUsername(cursor.getString(1));
                bean.setLastContact(new Date(cursor.getLong(2)));
                bean.setLastMessageExcerpt(cursor.getString(3));
                bean.setUnopenedMessageCount(cursor.getInt(4));
                list.add(bean);
            }
            cursor.close();
        } finally {
            db.close();
        }
        return list;
    }

    @Override
    public Date getNewestConversationTimestamp(){
        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getReadableDatabase();
        try{
            Cursor cursor = db.query(DatabaseConstants.TABLE_CONVERSATIONS, 
                    new String[]{DatabaseConstants.CONVERSATIONS_LASTCONTACT},
                    null, 
                    new String[]{},
                    null, null, DatabaseConstants.CONVERSATIONS_LASTCONTACT + " DESC", "1");
            if(!cursor.isAfterLast()){
                cursor.moveToNext();
                Date d = new Date(cursor.getLong(0));
                cursor.close();
                return d;
            } else {
                cursor.close();
                return null;
            }
        } finally {
            db.close();
        }
    }

    /**
     * Retrieves list of conversations with a last contact date newer than that have not been cached/updated yet.
     * The cached conversations are updated.
     * @return list of conversations
     * @throws PurpleSkyException 
     * @throws IOException 
     */
    @Override
    public List<UserMessageHistoryBean> getOnlineConversations() throws IOException, PurpleSkyException{
        final Date newestCached = getNewestConversationTimestamp();
        List<UserMessageHistoryBean> conversations = new ArrayList<>();

        boolean loadmore = true;
        int currentIdx = 0;
        while(loadmore){
            loadmore = false;

            List<UserMessageHistoryBean> lastcontacts = new ArrayList<>(
                        apiAdapter.getRecentContacts(BATCH, currentIdx, MessageRetrievalRestrictionType.UNREAD_FIRST));
            Collections.sort(lastcontacts, new UserMessageHistoryBeanLastContactComparator());
            
            UserMessageHistoryBean last = CollectionUtil.lastElement(lastcontacts);
            // Load more if we have loaded sth
            if(last != null){
                Date oldestReadInBatch = last.getLastContact();
                // All are unread, or the latest one is newer (or equally old) as our newest cached one.
                // Then there may be more new ones...
                boolean hasUnread = last.getUnopenedMessageCount() > 0;
                boolean cacheIsNotUptoDate  = CompareUtility.compare(oldestReadInBatch, newestCached) >= 0;
                if(hasUnread || (newestCached != null && cacheIsNotUptoDate) ){
                    loadmore = true;
                    currentIdx += lastcontacts.size();
                }
            }
            conversations.addAll(lastcontacts);
        }
        
        updateLastContact(conversations);
        List<MinimalUser> list = new ArrayList<>();
        for (UserMessageHistoryBean b : conversations) {
            if(b != null && b.getUserBean() != null){
                MinimalUser user = b.getUserBean();
                list.add(user);
            }
        }
        updateUserNameMapping(list);

        return conversations;
    }

    /**
     * Gets the cached messages with the specified user.
     * @param profileId
     * @param upToMessageId Restrict to messages with a smaller id than this parameter
     * @return List of messages
     */
    private List<PrivateMessage> getCachedMessagesWithUser(String profileId, Long upToMessageId) {
        StringBuilder where = new StringBuilder();
        where.append(FROM_TO_STRING);
        where.append(" AND ");
        where.append(DatabaseConstants.MESSAGES_MESSAGEID);
        where.append(" < ? ");

        List<String> selectArgs = new ArrayList<>();
        selectArgs.add(profileId);
        selectArgs.add(profileId);
        selectArgs.add(String.valueOf(upToMessageId));

        SQLiteDatabase db = DBHelper.fromContext(PurpleSkyApplication.get()).getReadableDatabase();
        Cursor curs = db.query(false,
                DatabaseConstants.TABLE_MESSAGES,
                new String[] {
                DatabaseConstants.MESSAGES_MESSAGEID,
                DatabaseConstants.MESSAGES_FROMUSERID,
                DatabaseConstants.MESSAGES_TOUSERID,
                DatabaseConstants.MESSAGES_TIMESENT,
                DatabaseConstants.MESSAGES_TEXT,
                DatabaseConstants.MESSAGES_PENDING },
                where.toString(),
                selectArgs.toArray(new String[selectArgs.size()]),
                null,
                null,
                DatabaseConstants.MESSAGES_MESSAGEID + " DESC ",
                String.valueOf(BATCH));
        try {
            curs.moveToFirst();
            if (curs.isAfterLast()) {
                return  Collections.emptyList();
            } else {
                List<PrivateMessage> messages = translateCursorToMessages(curs);
                Collections.reverse(messages);
                return messages;
            }
        } finally {
            if (curs != null) {
                curs.close();
            }
            db.close();
        }
    }

    private List<PrivateMessage> translateCursorToMessages(Cursor curs) {
        if (curs == null) {
            return Collections.emptyList();
        }

        final String myUserId = PersistantModel.getInstance().getUserProfileId();

        ArrayList<PrivateMessage> list = new ArrayList<>();
        while (!curs.isAfterLast()) {
            PrivateMessage message = new PrivateMessage();
            PrivateMessageHead head = new PrivateMessageHead();
            head.setMessageId(curs.getInt(curs.getColumnIndexOrThrow(DatabaseConstants.MESSAGES_MESSAGEID)));
            head.setRecipientProfileId(String.valueOf(curs.getInt(curs.getColumnIndexOrThrow(DatabaseConstants.MESSAGES_TOUSERID))));
            head.setAuthorProfileId(String.valueOf(curs.getInt(curs.getColumnIndexOrThrow(DatabaseConstants.MESSAGES_FROMUSERID))));
            head.setTimeSent(new Date(curs.getLong(curs.getColumnIndexOrThrow(DatabaseConstants.MESSAGES_TIMESENT))));
            head.setMessageType(myUserId.equals(head.getAuthorProfileId()) ? MessageType.SENT
                    : MessageType.RECEIVED);
            message.setMessageHead(head);
            message.setMessageText(curs.getString(curs.getColumnIndexOrThrow(DatabaseConstants.MESSAGES_TEXT)));

            list.add(message);

            curs.moveToNext();
        }

        return list;
    }
}
