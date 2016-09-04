package ch.defiant.purplesky.api.conversation.internal;

import android.util.Log;
import android.util.Pair;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.common.APINetworkUtility;
import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.api.internal.APIUtility;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.api.users.UserJSONTranslator;
import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.SendOptions;
import ch.defiant.purplesky.enums.MessageRetrievalRestrictionType;
import ch.defiant.purplesky.enums.MessageStatus;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.HTTPURLResponseHolder;
import ch.defiant.purplesky.util.HTTPURLUtility;
import ch.defiant.purplesky.util.StringUtility;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class ConversationAdapter implements IConversationAdapter {

    private static final String TAG = ConversationAdapter.class.getSimpleName();

    @Override
    public MessageResult sendMessage(IPrivateMessage message, SendOptions opts) throws IOException, PurpleSkyException {
        if (message == null) {
            throw new IllegalArgumentException("Cannot send message with 'null' message.");
        }
        String userid = message.getRecipientId();

        if (userid == null) {
            throw new IllegalArgumentException("Cannot send message with without a receiver!");
        }

        if (StringUtility.isNullOrEmpty(message.getMessageText())) {
            throw new PurpleSkyException(PurpleSkyApplication.get().getString(R.string.SendFailNoText));
        }

        // Fix issue BB-41: Without an unread handling, the output format is different
        // This leads to parsing errors. Ensure it now.
        if (opts == null || opts.getUnreadHandling() == null) {
            throw new IllegalArgumentException("Need a unread handling set!");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(PurplemoonAPIConstantsV1.MESSAGE_SEND_URL);
        sb.append(userid);
        URL url = new URL(sb.toString());

        List<Pair<String,String>> body = new ArrayList<Pair<String,String>>();
        body.add(new Pair<>(PurplemoonAPIConstantsV1.JSON_MESSAGE_TEXT, message.getMessageText()));
        body.add(
                new Pair<>(
                        PurplemoonAPIConstantsV1.MESSAGE_SEND_UNREAD_HANDLING_PARAM,
                        APIUtility.translateUnreadHandling(opts.getUnreadHandling())));
        if (opts.getUnreadHandling() == SendOptions.UnreadHandling.ABORT && opts.getLatestRead() != null) {
            body.add(
                    new Pair<>(
                            PurplemoonAPIConstantsV1.MESSAGE_SEND_UNREAD_HANDLING_TIMESTAMPSINCE,
                            String.valueOf(DateUtility.getUnixTime(opts.getLatestRead()))
                    )
            );
        }

        HTTPURLResponseHolder result = APINetworkUtility.performPOSTRequestForResponseHolder(url, body, null);
        if (result == null) {
            throw new PurpleSkyException(PurpleSkyApplication.get().getString(R.string.UnknownErrorOccured));
        }

        // The default handling will have translated all default stuff (of the errors)
        // Now return the translated message
        try {
            return ConversationJSONTranslator.translateToMessageResult(new JSONObject(result.getOutput()));
        } catch (JSONException jsonE) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Could not translate the message returned from sending", jsonE);
            }
            throw new PurpleSkyException(PurpleSkyApplication.get().getString(R.string.UnknownErrorOccured));
        }

    }

    @Override
    public List<UserMessageHistoryBean> getRecentContacts(Integer resultCount, Integer startAt,
                                                          MessageRetrievalRestrictionType restrict) throws IOException, PurpleSkyException {
        if (restrict == null) {
            restrict = MessageRetrievalRestrictionType.LAST_CONTACT;
        }

        ArrayList<UserMessageHistoryBean> resultList = new ArrayList<>();

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(PurplemoonAPIConstantsV1.BASE_URL);
        urlBuilder.append(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_URL);

        ArrayList<Pair<String,String>> params = new ArrayList<Pair<String,String>>();
        if (startAt != null && startAt >= 0) {
            params.add(new Pair<>(PurplemoonAPIConstantsV1.START_PARAM, String.valueOf(startAt)));
        }
        if (resultCount != null && resultCount >= 0) {
            params.add(new Pair<>(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(resultCount)));
            params.add(new Pair<>(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(resultCount)));
        } else {
            // TODO Remove the bean retrieval when beans are cached
            params.add(new Pair<>(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(15)));
            params.add(new Pair<>(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(15)));
        }
        params.add(new Pair<>(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_INCLUDEEXCERPT, "true"));
        params.add(new Pair<>(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_INCLUDEEXCERPT, String.valueOf(100)));
        params.add(new Pair<>(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_MINIMAL));

        params.add(new Pair<>(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_PARAM, APIUtility.translateMessageRetrievalRestrictionType(restrict)));
        // End of parameters
        urlBuilder.append(HTTPURLUtility.createGetQueryString(params));

        JSONObject res = APINetworkUtility.performGETRequestForJSONObject(new URL(urlBuilder.toString()));
        if (res == null)
            return resultList; // Empty

        JSONArray beans = res.optJSONArray(PurplemoonAPIConstantsV1.JSON_CHATLIST_CHATS);
        JSONArray users = res.optJSONArray(PurplemoonAPIConstantsV1.JSON_USER_ARRAY);

        // Parse Users first to associate them with messages
        Map<String, MinimalUser> map = null;
        if (users != null) {
            map = UserJSONTranslator.translateToUsers(users, MinimalUser.class);
        }

        if (beans != null) {
            // Get chats
            int size = beans.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject = beans.optJSONObject(i);
                if (jsonObject != null) {
                    UserMessageHistoryBean bean = ConversationJSONTranslator.translateToUserMessageHistoryBean(jsonObject);
                    if (map != null && map.containsKey(bean.getProfileId())) {
                        bean.setUserBean(map.get(bean.getProfileId()));
                    }
                    resultList.add(bean);
                }
            }
        }

        return resultList;
    }

    @Override
    public UserMessageHistoryBean getConversationStatus(String profileId) throws IOException, PurpleSkyException {
        if (profileId == null) {
            throw new IllegalArgumentException("Missing profileId of other user");
        }

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(PurplemoonAPIConstantsV1.BASE_URL);
        urlBuilder.append(PurplemoonAPIConstantsV1.MESSAGE_CHATSTATUS_URL);
        urlBuilder.append(profileId);


        JSONArray res = APINetworkUtility.performGETRequestForJSONArray(new URL(urlBuilder.toString()));
        if (res == null ){
            return null;
        }
        JSONObject object = res.optJSONObject(0);
        if(object == null){
            return null;
        }
        return ConversationJSONTranslator.translateToUserMessageHistoryBean(object);
    }

    @Override
    public int getUnopenedMessagesCount() throws IOException, PurpleSkyException {
        int count = 0;
        int nextStartIdx = 0;

        while (true) {
            List<UserMessageHistoryBean> contacts = getRecentContacts(25, nextStartIdx, MessageRetrievalRestrictionType.UNOPENED_ONLY);
            if (contacts == null) {
                // Error
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Get message count: Got null list, aborting.");
                }
                return 0;
            }
            if (contacts.isEmpty())
                break; // Empty list? Exit loop!
            nextStartIdx += contacts.size(); // Otherwise calculate next start index.

            for (UserMessageHistoryBean bean : contacts) {
                if (bean == null)
                    continue;

                int unopened = bean.getUnopenedMessageCount();
                if (unopened > 0) {
                    count += unopened;
                }
            }
        }

        return count;
    }

    @Override
    public List<IPrivateMessage> getRecentMessagesByUser(String profileId, AdapterOptions options) throws IOException, PurpleSkyException {

        ArrayList<IPrivateMessage> list = new ArrayList<>();
        if (profileId == null)
            return list;

        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(PurplemoonAPIConstantsV1.MESSAGE_CHATSHOW_URL);
        sb.append(profileId);

        ArrayList<Pair<String,String>> params = new ArrayList<>();

        if (options != null) {
            if (options.getNumber() != null) {
                params.add(new Pair<>(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(options.getNumber())));
            }
            if (options.getSinceTimestamp() != null) {
                long s = DateUtility.getUnixTime(options.getSinceTimestamp());
                Pair<String,String> time = new Pair<>(PurplemoonAPIConstantsV1.SINCE_TIMESTAMP_PARAM, String.valueOf(s));
                params.add(time);
            }
            if (options.getUptoTimestamp() != null) {
                long u = DateUtility.getUnixTime(options.getUptoTimestamp());
                Pair<String,String> time = new Pair<>(PurplemoonAPIConstantsV1.UPTO_TIMESTAMP_PARAM, String.valueOf(u));
                params.add(time);
            }
            if (options.getSinceId() != null) {
                params.add(new Pair<>(PurplemoonAPIConstantsV1.MESSAGE_CHATSHOW_SINCE_MESSAGEID, String.valueOf(options.getSinceId())));
            }
            if (options.getUptoId() != null) {
                params.add(new Pair<>(PurplemoonAPIConstantsV1.MESSAGE_CHATSHOW_UPTO_MESSAGEID, String.valueOf(options.getUptoId())));
            }
            if (options.getOrder() != null) {
                params.add(new Pair<>(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_PARAM, options.getOrder()));
            }
        }
        sb.append(HTTPURLUtility.createGetQueryString(params));

        URL url = new URL(sb.toString());
        JSONArray array = APINetworkUtility.performGETRequestForJSONArray(url);
        if (array == null) {
            return list;
        }

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                PrivateMessage message = ConversationJSONTranslator.translateToPrivateMessage(obj);
                if (message != null) {
                    message.setStatus(MessageStatus.SENT);
                    list.add(message);
                }
            } catch (JSONException e) {
                Log.d(TAG, "Message Array did contain non-object entities.");
            }
        }
        return list;
    }

}
