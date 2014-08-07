package ch.defiant.purplesky.api.conversation.internal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessageHead;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.enums.MessageType;
import ch.defiant.purplesky.util.DateUtility;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class ConversationJSONTranslator {

    private static final String TAG = ConversationJSONTranslator.class.getSimpleName();

    public static UserMessageHistoryBean translateToUserMessageHistoryBean(JSONObject obj) {
        if (obj == null)
            return null;
        UserMessageHistoryBean bean = new UserMessageHistoryBean();

        String id = obj.optString(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID, null);
        bean.setProfileId(id);

        int unopend = obj.optInt(PurplemoonAPIConstantsV1.JSON_CHATLIST_UNOPENEDCOUNT, 0);
        bean.setUnopenedMessageCount(unopend);

        boolean hasMessages = obj.optBoolean(PurplemoonAPIConstantsV1.JSON_CHATLIST_MESSAGEEXIST_BOOL, false);
        bean.setHasMessages(hasMessages);

        long timestamp = obj.optLong(PurplemoonAPIConstantsV1.JSON_MESSAGE_USER_LASTCONTACT_TIMESTAMP, 0);
        bean.setLastContact(DateUtility.getFromUnixTime(timestamp));

        long lastread = obj.optLong(PurplemoonAPIConstantsV1.JSON_CHATLIST_OTHERUSER_LASTREAD, 0);
        if(lastread != 0){
            bean.setOtherUserLastRead(DateUtility.getFromUnixTime(lastread));
        }

        long lastSent = obj.optLong(PurplemoonAPIConstantsV1.JSON_CHATLIST_LASTSENT, 0);
        if(lastSent != 0){
            bean.setLastSent(DateUtility.getFromUnixTime(lastSent));
        }

        long lastReceived = obj.optLong(PurplemoonAPIConstantsV1.JSON_CHATLIST_LASTRECEIVED, 0);
        if(lastReceived != 0){
            bean.setLastReceived(DateUtility.getFromUnixTime(lastReceived));
        }

        bean.setLastMessageExcerpt(obj.optString(PurplemoonAPIConstantsV1.JSON_CHATLIST_EXCERPT, null));

        return bean;
    }

    public static MessageResult translateToMessageResult(JSONObject obj) {
        MessageResult res = new MessageResult();
        if (obj == null) {
            return res;
        }

        ArrayList<PrivateMessage> unread = new ArrayList<PrivateMessage>();
        JSONArray array = obj.optJSONArray(PurplemoonAPIConstantsV1.JSON_MESSAGE_SEND_UNREAD_MSGS);
        if (array != null) {
            // Oops unsaid!
            int size = array.length();
            for (int i = 0; i < size; i++) {
                JSONObject msg = array.optJSONObject(i);
                if (msg != null) {
                    unread.add(translateToPrivateMessage(msg));
                }
            }
            res.setUnreadMessages(unread);
        }

        JSONObject sent = obj.optJSONObject(PurplemoonAPIConstantsV1.JSON_MESSAGE_SEND_NEWMSG);
        res.setSentMessage(translateToPrivateMessage(sent));

        return res;
    }

    public static PrivateMessage translateToPrivateMessage(JSONObject obj) {
        if (obj == null)
            return null;
        PrivateMessage msg = new PrivateMessage();
        PrivateMessageHead head = new PrivateMessageHead();
        msg.setMessageHead(head);

        try {
            if (obj.has(PurplemoonAPIConstantsV1.JSON_MESSAGE_ID)) {
                long id = obj.getLong(PurplemoonAPIConstantsV1.JSON_MESSAGE_ID);
                head.setMessageId(id);
            }

            boolean unopened = obj.optBoolean("new"); // Officially not documented!
            head.setUnopened(unopened);

            if (obj.has(PurplemoonAPIConstantsV1.JSON_MESSAGE_TYPE)) {
                String type = obj.getString(PurplemoonAPIConstantsV1.JSON_MESSAGE_TYPE);
                head.setMessageType(MessageType.getStatusByAPIValue(type));
            }
            if (obj.has(PurplemoonAPIConstantsV1.JSON_MESSAGE_PROFILEID)) {
                String profileId = obj.getString(PurplemoonAPIConstantsV1.JSON_MESSAGE_PROFILEID);
                if (head.getMessageType() == null) {
                    Log.w(TAG, "Could not identify type of message (null), cannot properly assign user!");
                } else {
                    switch (head.getMessageType()) {
                        case RECEIVED: {
                            head.setAuthorProfileId(profileId);
                            head.setRecipientProfileId(PersistantModel.getInstance().getUserProfileId());
                            break;
                        }
                        case SENT: {
                            head.setRecipientProfileId(profileId);
                            head.setAuthorProfileId(PersistantModel.getInstance().getUserProfileId());
                            break;
                        }
                        default: {
                            Log.w(TAG, "Could not identify type of message, cannot properly assign user!");
                        }
                    }
                }
            }
            if (obj.has(PurplemoonAPIConstantsV1.JSON_MESSAGE_TEXT)) {
                String txt = obj.getString(PurplemoonAPIConstantsV1.JSON_MESSAGE_TEXT);
                msg.setMessageText(txt);
            }
            if (obj.has(PurplemoonAPIConstantsV1.JSON_MESSAGE_SENT_TIMESTAMP)) {
                long timestamp = obj.getLong(PurplemoonAPIConstantsV1.JSON_MESSAGE_SENT_TIMESTAMP);
                head.setTimeSent(DateUtility.getFromUnixTime(timestamp));
            }

        } catch (JSONException e) {
            Log.w(TAG, "Translation from JSON to usermessagehistorybean failed", e);
            return null;
        }
        return msg;
    }


}
