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
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.api.common.CommonJSONTranslator;
import ch.defiant.purplesky.beans.AlertBean;
import ch.defiant.purplesky.beans.DetailedUser;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.NotificationBean;
import ch.defiant.purplesky.beans.OnlineBean;
import ch.defiant.purplesky.beans.PreviewUser;
import ch.defiant.purplesky.beans.PurplemoonLocation;
import ch.defiant.purplesky.constants.SecureConstants;
import ch.defiant.purplesky.core.ErrorTranslator;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserSearchOptions;
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
        formBuilder.add(PurplemoonAPIConstantsV1.OAUTH_POSTPARAM_CLIENT_ID, SecureConstants.get(SecureConstants.API_CLIENT_ID));
        formBuilder.add(PurplemoonAPIConstantsV1.OAUTH_POSTPARAM_CLIENT_SECRET, SecureConstants.get(SecureConstants.API_SECRET));
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
            MinimalUser translatedUser = CommonJSONTranslator.translateToUser(object, MinimalUser.class);
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
            PreviewUser translatedUser = CommonJSONTranslator.translateToUser(object, PreviewUser.class);
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
        DetailedUser translatedUser = CommonJSONTranslator.translateToUser(jsonUser, DetailedUser.class);
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

        DetailedUser translatedUser = CommonJSONTranslator.translateToUser(user, DetailedUser.class);
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
    public boolean isLoggedIn() {
        PersistantModel model = PurpleSkyApplication.get().getPersistantModel();
        return StringUtility.isNotNullOrEmpty(model.getOAuthAccessToken());
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
                    MinimalUser user = CommonJSONTranslator.translateToUser(object, PreviewUser.class);
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
            PreviewUser translated = CommonJSONTranslator.translateToUser(array.optJSONObject(i), PreviewUser.class);
            if (translated == null) {
                continue;
            }
            result.add(translated);
        }

        return result;
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
