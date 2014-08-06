package ch.defiant.purplesky.api.internal;

import android.util.Log;
import android.util.Pair;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.beans.AlertBean;
import ch.defiant.purplesky.beans.DetailedUser;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.NotificationBean;
import ch.defiant.purplesky.beans.OnlineBean;
import ch.defiant.purplesky.beans.PhotoVoteBean;
import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.beans.PreviewUser;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.PurplemoonLocation;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.beans.VisitsMadeBean;
import ch.defiant.purplesky.beans.VisitsReceivedBean;
import ch.defiant.purplesky.constants.SecureConstants;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.ErrorTranslator;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.SendOptions;
import ch.defiant.purplesky.core.SendOptions.UnreadHandling;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.enums.MessageRetrievalRestrictionType;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.exceptions.WrongCredentialsException;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.HTTPURLResponseHolder;
import ch.defiant.purplesky.util.HTTPURLUtility;
import ch.defiant.purplesky.util.NVLUtility;
import ch.defiant.purplesky.util.StringUtility;

import static ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1.REQUEST_DIVIDER;

/**
 * Implementation of the API Adapter for Purplemoon
 * @author Chakotay
 *
 */
class PurplemoonAPIAdapter implements IPurplemoonAPIAdapter {

    private static final String TAG = PurplemoonAPIAdapter.class.getSimpleName();
    private static PurplemoonAPIAdapter m_instance;

    @Override
    public MinimalUser getMinimalUserData(String userid, boolean withOnlineStatus) throws IOException, PurpleSkyException {
        if (userid == null)
            return null;
        Map<String, MinimalUser> minimalUserData = getMinimalUserData(Collections.singletonList(userid), withOnlineStatus);
        if (minimalUserData != null)
            return minimalUserData.get(userid);
        else
            return null;
    }

    private String getOAuthToken(String username, String password) throws IOException, WrongCredentialsException {
        Request.Builder builder = new Request.Builder();

        URL requestUrl = new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.OAUTH_TOKENREQUEST_URL);

        builder.url(requestUrl);

        FormEncodingBuilder formBuilder = new FormEncodingBuilder();
        formBuilder.add(PurplemoonAPIConstantsV1.OAUTH_POSTPARAM_CLIENT_ID, SecureConstants.get("api.id"));
        formBuilder.add(PurplemoonAPIConstantsV1.OAUTH_POSTPARAM_CLIENT_SECRET, SecureConstants.get("api.sec"));
        formBuilder.add(PurplemoonAPIConstantsV1.OAUTH_POSTPARAM_GRANTTYPE,
                PurplemoonAPIConstantsV1.OAUTH_POSTPARAM_GRANTTYPE_PASSWORD);
        formBuilder.add(PurplemoonAPIConstantsV1.OAUTH_POSTPARAM_USERNAME, username);
        formBuilder.add(PurplemoonAPIConstantsV1.OAUTH_POSTPARAM_PASSWORD, password);

        builder.post(formBuilder.build());
        Response response = new OkHttpClient().newCall(builder.build()).execute();

        int code = response.code();
        if(response.isSuccessful()){
            try {
                JSONObject jsonObject = new JSONObject(response.body().string());
                return jsonObject.optString(PurplemoonAPIConstantsV1.JSON_OAUTH_ACCESSTOKEN, null);
            } catch (JSONException e) {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "Parsing OAUTH Token - JSONException ");
                }
                return null;
            }
        } else {
            switch (code) {
                case HttpURLConnection.HTTP_UNAUTHORIZED: {
                    throw new WrongCredentialsException();
                }

                default: {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "Requesting OAUTH Token - got HTTP Response code: " + code);
                    }
                    return null;
                }
            }
        }
    }

    @Override
    public boolean doLogin(String username, String password) throws IOException, PurpleSkyException {
        PersistantModel model = PurpleSkyApplication.get().getPersistantModel();
        try {
            String token = getOAuthToken(username, password);
            if (StringUtility.isNullOrEmpty(token)) {
                return false;
            }
            model.setUserCredentials(null, token); // Store token first.

            DetailedUser user = getMyDetailedUserData(); // FIXME Only the userid is needed.
            if (user == null || StringUtility.isNullOrEmpty(user.getUserId())) {
                model.setUserCredentials(null, null);
                return false;
            } else {
                model.setUserCredentials(user.getUserId(), token);
            }
        } catch (WrongCredentialsException e) {
            return false;
        }

        return true;
    }

    @Override
    public Map<String, MinimalUser> getMinimalUserData(List<String> userids, boolean withOnlineStatus) throws IOException, PurpleSkyException {
        HashMap<String, MinimalUser> result = new HashMap<String, MinimalUser>();
        if (userids == null)
            return result;

        // Build the comma-separated URL
        StringBuilder urlString = new StringBuilder();
        urlString.append(PurplemoonAPIConstantsV1.BASE_URL);
        if (withOnlineStatus) {
            urlString.append(PurplemoonAPIConstantsV1.USER_MINIMALDATA_URL);
        } else {
            urlString.append(PurplemoonAPIConstantsV1.USER_MINIMALDATA_URL);
        }

        buildProfileListURL(userids, urlString);

        URL url = new URL(urlString.toString());

        JSONArray users = performGETRequestForJSONArray(url);
        if (users == null) {
            // Oops, return empty list.
            return result;
        }

        int size = users.length();
        for (int i = 0; i < size; i++) {
            // Convert all users into beans
            JSONObject object = users.optJSONObject(i);
            if (object == null) {
                continue;
            }
            MinimalUser translatedUser = JSONTranslator.translateToUser(object, MinimalUser.class);
            if (translatedUser != null && StringUtility.isNotNullOrEmpty(translatedUser.getUserId())) {
                result.put(translatedUser.getUserId(), translatedUser);
            }
        }

        return result;
    }

    @Override
    public PreviewUser getPreviewUserData(String userid, boolean withOnlineStatus) throws IOException, PurpleSkyException {
        if (userid == null)
            return null;
        Map<String, PreviewUser> previewUserData = getPreviewUserData(Collections.singletonList(userid), withOnlineStatus);
        if (previewUserData != null) {
            return previewUserData.get(userid);
        } else {
            return null;
        }
    }

    @Override
    public Map<String, PreviewUser> getPreviewUserData(List<String> userids, boolean withOnlineStatus) throws IOException, PurpleSkyException {
        HashMap<String, PreviewUser> result = new HashMap<String, PreviewUser>();
        if (userids == null)
            return result;

        // Build the comma-separated URL
        StringBuilder urlString = new StringBuilder();
        urlString.append(PurplemoonAPIConstantsV1.BASE_URL);
        if (withOnlineStatus) {
            urlString.append(PurplemoonAPIConstantsV1.USER_PREVIEWDATA_WITHSTATUS_URL);
        } else {
            urlString.append(PurplemoonAPIConstantsV1.USER_PREVIEWDATA_URL);
        }

        buildProfileListURL(userids, urlString);

        URL url = new URL(urlString.toString());
        JSONArray users = performGETRequestForJSONArray(url);
        if (users == null) {
            // Oops, return empty list.
            return result;
        }

        int size = users.length();
        for (int i = 0; i < size; i++) {
            // Convert all users into beans
            JSONObject object = users.optJSONObject(i);
            if (object == null) {
                continue;
            }
            PreviewUser translatedUser = JSONTranslator.translateToUser(object, PreviewUser.class);
            if (translatedUser != null && StringUtility.isNotNullOrEmpty(translatedUser.getUserId())) {
                result.put(translatedUser.getUserId(), translatedUser);
            }
        }

        return result;
    }

    @Override
    public DetailedUser getDetailedUserData(String userid) throws IOException, PurpleSkyException {
        if (userid == null) {
            return null;
        }

        // Build the comma-separated URL
        StringBuilder urlString = new StringBuilder();
        urlString.append(PurplemoonAPIConstantsV1.BASE_URL);
        urlString.append(PurplemoonAPIConstantsV1.USER_DETAILEDDATA_WITHSTATUS_URL);
        urlString.append(userid);

        URL url = new URL(urlString.toString());
        JSONObject jsonUser = performGETRequestForJSONObject(url);
        if (jsonUser == null) {
            return null;
        }

        // Convert user into bean
        DetailedUser translatedUser = JSONTranslator.translateToUser(jsonUser, DetailedUser.class);
        if (translatedUser != null && StringUtility.isNotNullOrEmpty(translatedUser.getUserId())) {
            return translatedUser;
        } else {
            return null;
        }
    }

    private void buildProfileListURL(List<String> userids, StringBuilder urlString) {
        final int listSize = userids.size();
        for (int i = 0; i < listSize; i++) {
            urlString.append(userids.get(i));
            // If not the last, add a comma.
            if (i != listSize - 1) {
                urlString.append(REQUEST_DIVIDER);
            }
        }
    }

    @Override
    public DetailedUser getMyDetailedUserData() throws IOException, PurpleSkyException {
        // Build the comma-separated URL
        StringBuilder urlString = new StringBuilder();
        urlString.append(PurplemoonAPIConstantsV1.BASE_URL);
        urlString.append(PurplemoonAPIConstantsV1.USER_DETAILEDDATA_ME_URL);

        URL url = new URL(urlString.toString());

        JSONObject user = performGETRequestForJSONObject(url);
        if (user == null) {
            Log.w(TAG, "Could not identify current user! No user returned!");
            return null;
        }

        DetailedUser translatedUser = JSONTranslator.translateToUser(user, DetailedUser.class);
        return translatedUser;
    }

    @Override
    public List<OnlineBean> getOnlineFavorites() throws IOException, PurpleSkyException {
        URL resource = new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.FAVORITES_ONLINE_URL);
        JSONArray response = performGETRequestForJSONArray(resource);
        ArrayList<OnlineBean> result = new ArrayList<OnlineBean>();

        if (response != null) {
            final int size = response.length();
            HashMap<String, Integer> idToPosMap = new HashMap<String, Integer>();
            for (int i = 0; i < size; i++) {
                // Translate to beans
                JSONObject jsonObject = response.optJSONObject(i);
                if (jsonObject == null) {
                    continue;
                }
                OnlineBean bean = JSONTranslator.translateToOnlineBean(jsonObject);
                if (bean == null)
                    continue;
                if (StringUtility.isNotNullOrEmpty(bean.getProfileId())) {
                    idToPosMap.put(bean.getProfileId(), i);
                }

                result.add(bean);
            }
            Map<String, MinimalUser> minimalUser = PurpleSkyApplication.get().getUserService()
                    .getMinimalUsers(new ArrayList<String>(idToPosMap.keySet()), false);
            for (Entry<String, MinimalUser> entry : minimalUser.entrySet()) {
                if (idToPosMap.get(entry.getKey()) != null) {
                    OnlineBean onlineBean = result.get(idToPosMap.get(entry.getKey()));
                    onlineBean.setUserBean(entry.getValue());
                }
            }
        }

        return result;
    }

    @Override
    public MessageResult sendMessage(PrivateMessage message, SendOptions opts) throws IOException, PurpleSkyException {
        if (message == null) {
            throw new IllegalArgumentException("Cannot send message with 'null' message.");
        }
        String userid = null;
        if (message.getRecipient() != null) {
            userid = message.getRecipient().getUserId();
        } else {
            userid = message.getMessageHead().getRecipientProfileId();
        }

        if (StringUtility.isNullOrEmpty(userid)) {
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

        List<NameValuePair> body = new ArrayList<NameValuePair>();
        body.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.JSON_MESSAGE_TEXT, message.getMessageText()));
        body.add(
                new BasicNameValuePair(
                        PurplemoonAPIConstantsV1.MESSAGE_SEND_UNREAD_HANDLING_PARAM,
                        APIUtility.translateUnreadHandling(opts.getUnreadHandling())));
        if (opts.getUnreadHandling() == UnreadHandling.ABORT && opts.getLatestRead() != null) {
            body.add(
                    new BasicNameValuePair(
                            PurplemoonAPIConstantsV1.MESSAGE_SEND_UNREAD_HANDLING_TIMESTAMPSINCE,
                            String.valueOf(DateUtility.getUnixTime(opts.getLatestRead()))
                            )
                    );
        }

        HTTPURLResponseHolder result = performPOSTRequestForResponseHolder(url, body, null);
        if (result == null) {
            throw new PurpleSkyException(PurpleSkyApplication.get().getString(R.string.UnknownErrorOccured));
        }

        // The default handling will have translated all default stuff (of the errors)
        // Now return the translated message
        try {
            return JSONTranslator.translateToMessageResult(new JSONObject(result.getOutput()));
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

        ArrayList<UserMessageHistoryBean> resultList = new ArrayList<UserMessageHistoryBean>();

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(PurplemoonAPIConstantsV1.BASE_URL);
        urlBuilder.append(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_URL);

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        if (startAt != null && startAt >= 0) {
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.START_PARAM, String.valueOf(startAt)));
        }
        if (resultCount != null && resultCount >= 0) {
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(resultCount)));
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(resultCount)));
        } else {
            // TODO Remove the bean retrieval when beans are cached
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(15)));
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(15)));
        }
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_INCLUDEEXCERPT, "true"));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_INCLUDEEXCERPT, String.valueOf(100)));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_MINIMAL));

        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_PARAM, APIUtility.translateMessageRetrievalRestrictionType(restrict)));
        // End of parameters
        urlBuilder.append(HTTPURLUtility.createGetQueryString(params));

        JSONObject res = performGETRequestForJSONObject(new URL(urlBuilder.toString()));
        if (res == null)
            return resultList; // Empty

        JSONArray beans = res.optJSONArray(PurplemoonAPIConstantsV1.JSON_CHATLIST_CHATS);
        JSONArray users = res.optJSONArray(PurplemoonAPIConstantsV1.JSON_USER_ARRAY);

        // Parse Users first to associate them with messages
        Map<String, MinimalUser> map = null;
        if (users != null) {
            map = JSONTranslator.translateToUsers(users, MinimalUser.class);
        }

        if (beans != null) {
            // Get chats
            int size = beans.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject = beans.optJSONObject(i);
                if (jsonObject != null) {
                    UserMessageHistoryBean bean = JSONTranslator.translateToUserMessageHistoryBean(jsonObject);
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


        JSONArray res = performGETRequestForJSONArray(new URL(urlBuilder.toString()));
        if (res == null ){
            return null;
        }
        JSONObject object = res.optJSONObject(0);
        if(object == null){
            return null;
        }
        return JSONTranslator.translateToUserMessageHistoryBean(object);
    }

    @Override
    public int getOnlineFavoritesCount() throws IOException, PurpleSkyException {
        URL url = new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.FAVORITES_ONLINECOUNT_URL);
        JSONObject result = performGETRequestForJSONObject(url);
        if (result == null) {
            return 0;
        }

        int count = result.optInt(PurplemoonAPIConstantsV1.JSON_FAVORITES_ONLINECOUNT);
        if (count >= 0) {
            return count;
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Invalid online count value from API: " + count);
            }
            return 0;
        }
    }

    @Override
    public int getUnopenedMessagesCount() throws IOException, PurpleSkyException {
        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_URL);

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_PARAM,
                PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_UNREADONLY));

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

                Integer unopened = bean.getUnopenedMessageCount();
                if (unopened != null && unopened > 0) {
                    count += unopened;
                }
            }
        }

        return count;
    }

    @Override
    public List<PrivateMessage> getRecentMessagesByUser(String profileId, AdapterOptions options) throws IOException, PurpleSkyException {

        ArrayList<PrivateMessage> list = new ArrayList<PrivateMessage>();
        if (profileId == null)
            return list;

        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(PurplemoonAPIConstantsV1.MESSAGE_CHATSHOW_URL);
        sb.append(profileId);

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        if (options != null) {
            if (options.getNumber() != null) {
                params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(options.getNumber())));
            }
            if (options.getSinceTimestamp() != null) {
                long s = DateUtility.getUnixTime(options.getSinceTimestamp());
                BasicNameValuePair time = new BasicNameValuePair(PurplemoonAPIConstantsV1.SINCE_TIMESTAMP_PARAM, String.valueOf(s));
                params.add(time);
            }
            if (options.getUptoTimestamp() != null) {
                long u = DateUtility.getUnixTime(options.getUptoTimestamp());
                BasicNameValuePair time = new BasicNameValuePair(PurplemoonAPIConstantsV1.UPTO_TIMESTAMP_PARAM, String.valueOf(u));
                params.add(time);
            }
            if (options.getSinceId() != null) {
                params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.MESSAGE_CHATSHOW_SINCE_MESSAGEID, String.valueOf(options.getSinceId())));
            }
            if (options.getUptoId() != null) {
                params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.MESSAGE_CHATSHOW_UPTO_MESSAGEID, String.valueOf(options.getUptoId())));
            }
            if (options.getOrder() != null) {
                params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_PARAM, options.getOrder()));
            }
        }
        sb.append(HTTPURLUtility.createGetQueryString(params));

        URL url = new URL(sb.toString());
        JSONArray array = performGETRequestForJSONArray(url);
        if (array == null) {
            return list;
        }

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                PrivateMessage message = JSONTranslator.translateToPrivateMessage(obj);
                if (message != null) {
                    list.add(message);
                }
            } catch (JSONException e) {
                Log.d(TAG, "Message Array did contain non-object entities.");
            }
        }
        return list;
    }

    @Override
    public boolean isLoggedIn() {
        PersistantModel model = PurpleSkyApplication.get().getPersistantModel();
        return StringUtility.isNotNullOrEmpty(model.getOAuthAccessToken());
    }

    @Override
    public List<PictureFolder> getPictureFolders(String profileId) throws IOException, PurpleSkyException {

        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(PurplemoonAPIConstantsV1.PICTUREFOLDER_FOLDERSONLY_URL);
        sb.append(profileId);

        JSONObject jsonObject = performGETRequestForJSONObject(new URL(sb.toString()));
        return JSONTranslator.translateToPictureFolders(jsonObject);
    }

    @Override
    public List<PictureFolder> getMyPictureFolders() throws IOException, PurpleSkyException {
        PersistantModel model = PurpleSkyApplication.get().getPersistantModel();
        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(PurplemoonAPIConstantsV1.PICTUREFOLDER_FOLDERSONLY_ME_URL);

        JSONArray jsonArray = performGETRequestForJSONArray(new URL(sb.toString()));
        return JSONTranslator.translateToPictureFolders(model.getUserProfileId(), jsonArray);
    }

    @Override
    public Map<String, PictureFolder> getFoldersWithPictures(String profileId, List<String> folders) throws IOException, PurpleSkyException {
        HashMap<String, PictureFolder> map = new HashMap<String, PictureFolder>();

        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(PurplemoonAPIConstantsV1.PICTUREFOLDER_WITHPICTURES_URL);
        sb.append(profileId);
        if (folders != null && folders.size() > 0) {
            sb.append("?");
            sb.append(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_IDS);
            sb.append("=");
            for (int i = 0, size = folders.size(); i < size; i++) {
                sb.append(folders.get(i));
                if (i != size - 1) {
                    sb.append(",");
                }
            }
        }

        JSONObject obj = performGETRequestForJSONObject(new URL(sb.toString()));
        if (obj == null) {
            return map;
        }
        List<PictureFolder> translatedFolders = JSONTranslator.translateToPictureFolders(obj);
        if (translatedFolders != null) {
            for (PictureFolder f : translatedFolders) {
                if (f == null)
                    continue;
                map.put(f.getFolderId(), f);
            }
        }
        return map;
    }

    @Override
    public NotificationBean getNotificationBean() throws IOException, PurpleSkyException {
        String url = PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.NOTIFICATIONS_URL;
        JSONObject jsonObject = performGETRequestForJSONObject(new URL(url));
        if (jsonObject != null) {
            return JSONTranslator.translateToNotificationBean(jsonObject);
        } else {
            return null;
        }
    }

    @Override
    public List<MinimalUser> searchUserByName(String text, UserSearchOptions options) throws IOException, PurpleSkyException {
        if (options == null) {
            throw new IllegalArgumentException("No options provided");
        }

        ArrayList<MinimalUser> list = new ArrayList<MinimalUser>();

        StringBuilder builder = new StringBuilder();
        builder.append(PurplemoonAPIConstantsV1.BASE_URL);
        builder.append(PurplemoonAPIConstantsV1.USER_SEARCH_BYNAME);

        // Must manually encode space by "%20" (encoder encodes as a plus sign)
        builder.append(URLEncoder.encode(text, HTTP.UTF_8).replace("+", "%20"));

        // NICE Maybe make type switchable?

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_PREVIEW_WITHSTATUS));

        Integer resultsNum = NVLUtility.nvl(options.getNumber(), 20);
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.RESULTSNUMBER_PARAM, String.valueOf(resultsNum)));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(resultsNum)));

        builder.append(HTTPURLUtility.createGetQueryString(params));

        JSONArray array = performGETRequestForJSONArray(new URL(builder.toString()));
        if (array != null && array.length() != 0) {
            for (int i = 0, size = array.length(); i < size; i++) {
                JSONObject object = array.optJSONObject(i);
                if (object != null) {
                    MinimalUser user = JSONTranslator.translateToUser(object, PreviewUser.class);
                    if (user != null) {
                        list.add(user);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public boolean setOnlineStatus(OnlineStatus status, String custom) throws IOException, PurpleSkyException {
        if (status == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Cannot set online status: No status");
            }
            return false;
        }

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.MY_ONLINESTATUS_STATUS_PARAM, APIUtility.translateOnlineStatus(status)));

        if (StringUtility.isNotNullOrEmpty(custom)) {
            // Make sure to truncate
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.MY_ONLINESTATUS_CUSTOMTEXT_PARAM, StringUtility.truncate(custom,
                    PurplemoonAPIConstantsV1.MY_ONLINESTATUS_CUSTOM_MAXLENGTH)));
        }

        HTTPURLResponseHolder result;
        result = performPOSTRequestForResponseHolder(new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.MY_ONLINESTATUS_URL),
                params, Collections.<NameValuePair> emptyList());

        switch (result.getResponseCode()) {
            case HttpURLConnection.HTTP_OK: {
                return true;
            }
            default: {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Unhandled http return code when setting online status: " + result.getResponseCode());
                }
                return false;
            }
        }
    }

    @Override
    public Pair<OnlineStatus, String> getOwnOnlineStatus() throws IOException, PurpleSkyException {
        Pair<OnlineStatus, String> result = new Pair<OnlineStatus, String>(OnlineStatus.UNKNOWN, null);

        JSONObject object = performGETRequestForJSONObject(new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.MY_ONLINESTATUS_URL));
        if (object == null) {
            return result;
        }

        String predefined = object.optString(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUS, null);
        String customText = object.optString(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUSTEXT, null);

        if (predefined != null) {
            OnlineStatus status = APIUtility.toOnlineStatus(predefined);
            return new Pair<OnlineStatus, String>(status, customText);
        } else {
            // Unknown case...
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "No online status returned - setting 'UNKNOWN'");
            }
            return result;
        }
    }

    @Override
    public Map<String, Pair<OnlineStatus, String>> getOnlineStatus(List<String> profileIds) throws IOException, PurpleSkyException {
        if (profileIds == null) {
            return new HashMap<String, Pair<OnlineStatus, String>>();
        }

        StringBuilder url = new StringBuilder();
        url.append(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.ONLINESTATUS_URL);

        if (profileIds.size() > 100) {
            Log.w(TAG, "Requested more online statuses than available from API. Will truncate");
        }

        final int size = profileIds.size();
        for (int i = 0; i < size; i++) {
            url.append(profileIds.get(i));
            if (i < (size - 1)) {
                url.append(",");
            }
        }

        JSONArray res = performGETRequestForJSONArray(new URL(url.toString()));
        if (res == null) {
            return new HashMap<String, Pair<OnlineStatus, String>>();
        }

        HashMap<String, Pair<OnlineStatus, String>> map = new HashMap<String, Pair<OnlineStatus, String>>();
        final int arraySize = res.length();
        for (int i = 0; i < arraySize; i++) {
            JSONObject object = res.optJSONObject(i);
            if (object == null) {
                continue;
            }

            String profileId = object.optString(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID, null);
            String predefined = object.optString(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUS, null);
            String customText = object.optString(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUSTEXT, null);

            if (profileId == null) {
                continue;
            }

            OnlineStatus status = APIUtility.toOnlineStatus(predefined);
            if (predefined != null) {
                map.put(profileId, new Pair<OnlineStatus, String>(status, customText));
            } else {
                map.put(profileId, new Pair<OnlineStatus, String>(status, null));
            }
        }
        return map;
    }

    @Override
    public AlertBean getAlertBean() throws IOException, PurpleSkyException {
        JSONObject obj = performGETRequestForJSONObject(new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.ALERTS_URL));
        if (obj == null) {
            return null;
        }
        return JSONTranslator.translateToAlertBean(obj);
    }

    @Override
    public List<VisitsReceivedBean> getReceivedVists(AdapterOptions options, Date overrideLastDateCheck) throws IOException, PurpleSkyException {
        StringBuilder builder = new StringBuilder();

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        int number = 20;
        if (options != null) {
            if (options.getStart() != null) {
                params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.START_PARAM, String.valueOf(options.getStart())));
            }
            if (options.getNumber() != null) {
                number = options.getNumber();
            }
            if (options.getSinceTimestamp() != null) {
                long s = DateUtility.getUnixTime(options.getSinceTimestamp());
                BasicNameValuePair time = new BasicNameValuePair(PurplemoonAPIConstantsV1.SINCE_TIMESTAMP_PARAM, String.valueOf(s));
                params.add(time);
            }
        }
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(number)));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_MINIMAL));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(number)));

        builder.append(HTTPURLUtility.createGetQueryString(params));
        URL url = new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.VISITORS_URL + builder.toString());

        JSONObject obj = performGETRequestForJSONObject(url);
        if (obj == null) {
            return null;
        }

        List<VisitsReceivedBean> result = JSONTranslator.translateToVisitsReceivedList(obj, overrideLastDateCheck);
        if (result == null || result.isEmpty()) {
            return result;
        }

        JSONArray users = obj.optJSONArray(PurplemoonAPIConstantsV1.JSON_USER_ARRAY);
        Map<String, MinimalUser> userMap = JSONTranslator.translateToUsers(users, MinimalUser.class);
        if (userMap != null) { // Add to cache
            UserService service = PurpleSkyApplication.get().getUserService();
            for (MinimalUser u : userMap.values()) {
                service.addToCache(u);
            }
        }

        for (int i = 0, size = result.size(); i < size; i++) {
            VisitsReceivedBean bean = result.get(i);
            if (bean == null) {
                continue;
            }
            bean.setUser(userMap.get(bean.getProfileId()));
        }

        return result;
    }

    @Override
    public List<VisitsMadeBean> getOwnVists(AdapterOptions options) throws IOException, PurpleSkyException {
        StringBuilder builder = new StringBuilder();

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        int number = 20;
        if (options != null) {
            if (options.getStart() != null) {
                params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.START_PARAM, String.valueOf(options.getStart())));
            }
            if (options.getNumber() != null) {
                number = options.getNumber();
            }
            if (options.getSinceTimestamp() != null) {
                long s = DateUtility.getUnixTime(options.getSinceTimestamp());
                BasicNameValuePair time = new BasicNameValuePair(PurplemoonAPIConstantsV1.SINCE_TIMESTAMP_PARAM, String.valueOf(s));
                params.add(time);
            }
        }
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(number)));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_MINIMAL));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(number)));

        builder.append(HTTPURLUtility.createGetQueryString(params));
        URL url = new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.VISITS_MADE_URL + builder.toString());

        JSONObject obj = performGETRequestForJSONObject(url);
        if (obj == null) {
            return null;
        }

        List<VisitsMadeBean> result = JSONTranslator.translateToVisitsMadeList(obj);
        if (result == null || result.isEmpty()) {
            return result;
        }

        JSONArray users = obj.optJSONArray(PurplemoonAPIConstantsV1.JSON_USER_ARRAY);
        Map<String, MinimalUser> userMap = JSONTranslator.translateToUsers(users, MinimalUser.class);
        if (userMap != null) { // Add to cache
            UserService service = PurpleSkyApplication.get().getUserService();
            for (MinimalUser u : userMap.values()) {
                service.addToCache(u);
            }
        }

        for (int i = 0, size = result.size(); i < size; i++) {
            VisitsMadeBean bean = result.get(i);
            if (bean == null) {
                continue;
            }
            bean.setUser(userMap.get(bean.getProfileId()));
        }

        return result;
    }

    @Override
    public List<MinimalUser> searchUser(UserSearchOptions options) throws IOException, PurpleSkyException {
        if (options == null) {
            return Collections.emptyList();
        }
        final int number = NVLUtility.nvl(options.getNumber(), 100);

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        if (options.getUserClass() == null || options.getUserClass() == MinimalUser.class) {
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_MINIMAL_WITHSTATUS));
        } else if (options.getUserClass() == PreviewUser.class) {
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_PREVIEW_WITHSTATUS));
        }
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(number)));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.RESULTSNUMBER_PARAM, String.valueOf(number)));
        if (options.getSearchType() != null) {
            // FIXME PBN Search type
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USERSEARCH_TYPE_PARAM, PurplemoonAPIConstantsV1.USERSEARCH_TYPE_FRIENDS));
        }
        // Add location
        if (options.getLocation() != null) {
            ch.defiant.purplesky.beans.util.Pair<Double, Double> location = options.getLocation();
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USERSEARCH_CURRPOS_LATITUDE_PARAM, String.valueOf(location.getFirst())));
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USERSEARCH_CURRPOS_LONGITUDE_PARAM, String.valueOf(location.getSecond())));
        }

        // Search order
        if (options.getSearchOrder() != null) {
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USERSEARCH_ORDER_PARAM, options.getSearchOrder().getApiValue()));
        }

        JSONObject jsonObj = APIUtility.getJSONUserSearchObject(options);
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USERSEARCH_CRITERIA_JSON_PARAM, jsonObj.toString()));


        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(PurplemoonAPIConstantsV1.USERSEARCH_URL);
        sb.append(HTTPURLUtility.createGetQueryString(params));

        JSONArray array = performGETRequestForJSONArray(new URL(sb.toString()));
        if (array == null) {
            return Collections.emptyList();
        }

        ArrayList<MinimalUser> result = new ArrayList<MinimalUser>();
        for (int i = 0, size = array.length(); i < size; i++) {
            PreviewUser translated = JSONTranslator.translateToUser(array.optJSONObject(i), PreviewUser.class);
            if (translated == null) {
                continue;
            }
            result.add(translated);
        }

        return result;
    }

    @Override
    public int getRemainingPhotoVotes() throws IOException, PurpleSkyException {
        JSONObject obj = performGETRequestForJSONObject(new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.PHOTOVOTE_REMAINING_URL));
        return obj.optInt(PurplemoonAPIConstantsV1.JSON_PHOTOVOTES_REMAINING, 0);
    }

    @Override
    public PhotoVoteBean getNextPhotoVoteAndVote(PhotoVoteBean bean) throws IOException, PurpleSkyException {
        URL u = new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.PHOTOVOTE_VOTE_URL);
        if (bean == null) {
            JSONObject res = performGETRequestForJSONObject(u);
            return JSONTranslator.translateToPhotoVoteBean(res, MinimalUser.class);
        } else {
            ArrayList<NameValuePair> body = new ArrayList<NameValuePair>();
            body.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VOTEID, String.valueOf(bean.getVoteId())));
            body.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VERDICT, APIUtility.translatePhotoVoteVerdict(bean.getVerdict())));
            HTTPURLResponseHolder resp = performPOSTRequestForResponseHolder(u, body, null);
            try {
                return JSONTranslator.translateToPhotoVoteBean(new JSONObject(resp.getOutput()), MinimalUser.class);
            } catch (JSONException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Could not translate photovote output from POST request to JSON!", e);
                }
                throw new PurpleSkyException(PurpleSkyApplication.get().getString(R.string.UnknownErrorOccured));
            }
        }
    }

    @Override
    public List<PhotoVoteBean> getReceivedVotes(AdapterOptions opts) throws IOException, PurpleSkyException {
        return getVotes(false, opts);
    }

    @Override
    public List<PhotoVoteBean> getGivenVotes(AdapterOptions opts) throws IOException, PurpleSkyException {
        return getVotes(true, opts);
    }

    @Override
    public Date getPowerUserExpiry() throws IOException, PurpleSkyException {
        URL u = new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.POWERUSER_STATUS_URL);
        JSONObject jsonObject = performGETRequestForJSONObject(u);

        if (jsonObject.has(PurplemoonAPIConstantsV1.JSON_POWERUSER_EXPIRY)) {
            Object opt = jsonObject.opt(PurplemoonAPIConstantsV1.JSON_POWERUSER_EXPIRY);
            if (opt instanceof String) {
                return DateUtility.parseJSONDate((String) opt);
            }
        }
        return null;
    }

    @Override
    public boolean registerPush(String gcmRegId) throws IOException, PurpleSkyException {
        URL u = new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.PUSH_NOTIFICATION_URL);
        ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.PUSH_NOTIFICATION_ACTION_ARG,
                PurplemoonAPIConstantsV1.PUSH_NOTIFICATION_ACTION_REGISTER));
        list.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.PUSH_NOTIFICATION_DEVICETOKEN, gcmRegId));

        HTTPURLResponseHolder resp = performPOSTRequestForResponseHolder(u, list, null);
        if (resp.getOutput() != null) {
            JSONObject object = null;
            try {
                object = new JSONObject(resp.getOutput());
            } catch (JSONException e) {
                Log.d(TAG, "Could not parse result for push registering!", e);
            }
            if (object == null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "");
                }
                return false;
            }

            return object.optBoolean(PurplemoonAPIConstantsV1.JSON_PUSH_ACTIVE);
        } else {
            return false;
        }
    }

    @Override
    public boolean unregisterPush(String gcmRegId) throws IOException, PurpleSkyException {
        if (gcmRegId == null) {
            return false;
        }

        URL u = new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.PUSH_NOTIFICATION_URL);
        ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.PUSH_NOTIFICATION_ACTION_ARG,
                PurplemoonAPIConstantsV1.PUSH_NOTIFICATION_ACTION_UNREGISTER));
        list.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.PUSH_NOTIFICATION_DEVICETOKEN, gcmRegId));

        HTTPURLResponseHolder resp = performPOSTRequestForResponseHolder(u, list, null);
        if (resp.getOutput() != null) {
            JSONObject object = null;
            try {
                object = new JSONObject(resp.getOutput());
            } catch (JSONException e) {
                Log.d(TAG, "Could not parse result for push registering!", e);
            }
            if (object == null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "");
                }
                return false;
            }

            boolean wasUnregisterSuccessful = !object.optBoolean(PurplemoonAPIConstantsV1.JSON_PUSH_ACTIVE, true);

            return wasUnregisterSuccessful;
        } else {
            return false;
        }
    }

    @Override
    public Collection<PurplemoonLocation> getOwnLocations() throws IOException, PurpleSkyException {
        String url = PurplemoonAPIConstantsV1.BASE_URL+PurplemoonAPIConstantsV1.LOCATIONS_URL;

        JSONArray array = performGETRequestForJSONArray(new URL(url));
        if(array == null){
            Log.w(TAG, "No locations returned from API");
            return Collections.emptyList();
        }
        List<PurplemoonLocation> result = new ArrayList<PurplemoonLocation>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add(JSONTranslator.toPurplemoonLocation(array.optJSONObject(i)));
        }

        return result;
    }

    @Override
    public void setOwnLocation(PurplemoonLocation location) throws IOException, PurpleSkyException {
        String url = PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.LOCATIONS_URL;

        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.LOCATIONS_TYPE, APIUtility.translateLocationType(location.getLocationType())));
        postData.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.LOCATIONS_COUNTRYCODE, location.getCountryCode()));
        postData.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.LOCATIONS_NAME, location.getLocationName()));
        postData.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.LOCATIONS_ADDRESS, location.getStreetAddress()));
        postData.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.LOCATIONS_LATITUDE, String.valueOf(location.getLatitude())));
        postData.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.LOCATIONS_LONGITUE, String.valueOf(location.getLongitude())));

       performPOSTRequestForResponseHolder(new URL(url), postData, null);
    }

    private List<PhotoVoteBean> getVotes(boolean given, AdapterOptions opts) throws IOException, PurpleSkyException {
        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        if (given) {
            sb.append(PurplemoonAPIConstantsV1.PHOTOVOTE_GIVEN_URL);
        } else {
            sb.append(PurplemoonAPIConstantsV1.PHOTOVOTE_RECEIVED_URL);
        }
        int number = 20;

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        if (opts != null) {
            if (opts.getStart() != null) {
                params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.START_PARAM, String.valueOf(opts.getStart())));
            }
            if (opts.getNumber() != null) {
                number = opts.getNumber();
            }
            if (opts.getSinceTimestamp() != null) {
                long s = DateUtility.getUnixTime(opts.getSinceTimestamp());
                BasicNameValuePair time = new BasicNameValuePair(PurplemoonAPIConstantsV1.SINCE_TIMESTAMP_PARAM, String.valueOf(s));
                params.add(time);
            }
        }
        // Total count same as user object count
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(number)));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_MINIMAL));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(number)));

        sb.append(HTTPURLUtility.createGetQueryString(params));

        URL url = new URL(sb.toString());
        JSONObject result = performGETRequestForJSONObject(url);
        long check = result.optLong(PurplemoonAPIConstantsV1.JSON_LASTCHECK_TIMESTAMP, -1);
        JSONArray votes = result.optJSONArray(PurplemoonAPIConstantsV1.JSON_PHOTOVOTES_VOTES);
        JSONArray users = result.optJSONArray(PurplemoonAPIConstantsV1.JSON_USER_ARRAY);

        Map<String, MinimalUser> userMap = JSONTranslator.translateToUsers(users, MinimalUser.class);
        if (userMap != null) { // Add to cache
            UserService service = PurpleSkyApplication.get().getUserService();
            for (MinimalUser u : userMap.values()) {
                service.addToCache(u);
            }
        }

        if (votes == null) {
            return Collections.emptyList();
        }
        ArrayList<PhotoVoteBean> list = new ArrayList<PhotoVoteBean>();
        for (int i = 0, size = votes.length(); i < size; i++) {
            JSONObject object = votes.optJSONObject(i);
            if (object == null) {
                continue;
            }

            String profileId = object.optString(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID, null);

            Date fromUnixTime;
            if (check != -1) {
                fromUnixTime = DateUtility.getFromUnixTime(check);
            } else {
                fromUnixTime = new Date();
            }
            PhotoVoteBean p = JSONTranslator.translateToPhotoVoteBean(object, MinimalUser.class);
            if (p != null) {
                p.setUser(userMap.get(profileId));
                list.add(p);
            }
        }

        return list;
    }

    private JSONObject performGETRequestForJSONObject(URL resource) throws IOException, PurpleSkyException {
        String response = performGETRequestForString(resource);
        if (response == null)
            return null;
        try {
            return new JSONObject(response);
        } catch (JSONException e) {
            // Oops, we can't parse that
            Log.w(TAG, "Response from server is not a JSON parsable object.");
            return null;
        }
    }

    private JSONArray performGETRequestForJSONArray(URL resource) throws IOException, PurpleSkyException {
        String response = performGETRequestForString(resource);
        if (response == null)
            return null;
        try {
            return new JSONArray(response);
        } catch (JSONException e) {
            // Oops, we can't parse that
            Log.w(TAG, "Response from server is not a JSON parsable array.");
            return null;
        }
    }

    /**
     * Will perform a request for the given resource and return the output from the server. Will use HTTP Basic authentication if one of the
     * parameters (username, password) is non-empty
     * 
     * @param resource
     *            The URL to query
     * @return String if successful. null otherwise.
     * @throws IOException
     *             If the connection failed.
     * @throws WrongCredentialsException
     *             If the users credentials were rejected.
     */
    private String performGETRequestForString(URL resource) throws IOException, PurpleSkyException {
        ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();

        OkHttpClient httpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();

        addLanguageHeader(builder);
        addAuthenticationHeader(builder, resource);

        Request request = builder.url(resource).build();
        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful()){
            return response.body().string();
        } else {
            ErrorTranslator.translateHttpError(PurpleSkyApplication.get(), response.code(), response.body().string(), resource.toString());
        }
        // We should never get here - translator will throw above
        return StringUtility.EMPTY_STRING;
    }

    private void addAuthenticationHeader(Request.Builder builder, URL resource) {
        if (resource.toString().startsWith(PurplemoonAPIConstantsV1.BASE_URL)) { // TODO Rewrite this
            String token = PersistantModel.getInstance().getOAuthAccessToken();
            if (StringUtility.isNullOrEmpty(token)) {
                if (BuildConfig.DEBUG)
                    Log.w(TAG, "Requesting URL " + resource + " without token");
            } else {
                builder.addHeader(PurplemoonAPIConstantsV1.AUTH_HEADER_NAME, PurplemoonAPIConstantsV1.AUTH_HEADER_VALUEPREFIX + token);
            }
        }
    }

    private void addLanguageHeader(Request.Builder builder) {
        String language = Locale.getDefault().getLanguage();
        if (StringUtility.isNullOrEmpty(language)) {
            language = Locale.US.getLanguage();
        }
        builder.addHeader("Accept-Language", language);
    }

    // TODO Move to network utility
    private HTTPURLResponseHolder performPOSTRequestForResponseHolder(URL resource, List<NameValuePair> postBody, List<NameValuePair> headrs)
            throws IOException, PurpleSkyException {
        Request.Builder builder = new Request.Builder();
        builder.url(resource);
        if(headrs != null){
            for(NameValuePair pair : headrs){
                builder.addHeader(pair.getName(), pair.getValue());
            }
        }

        addLanguageHeader(builder);
        addAuthenticationHeader(builder, resource);

        FormEncodingBuilder formBuilder = new FormEncodingBuilder();
        if(postBody != null){
            for(NameValuePair pair : postBody){
                formBuilder.add(pair.getName(), pair.getValue());
            }
        }
        builder.post(formBuilder.build());
        Response response = new OkHttpClient().newCall(builder.build()).execute();

        HTTPURLResponseHolder holder = new HTTPURLResponseHolder();
        holder.setResponseCode(response.code());
        holder.setSuccessful(response.isSuccessful());
        if(response.isSuccessful()){
            holder.setOutput(response.body().string());
        } else {
            holder.setError(response.body().string());
            ErrorTranslator.translateHttpError(PurpleSkyApplication.get(), response.code(), response.body().string(), resource.toString());
        }

        return holder;
    }



}
