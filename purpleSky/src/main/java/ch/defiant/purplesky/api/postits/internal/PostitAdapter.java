package ch.defiant.purplesky.api.postits.internal;

import android.util.Log;
import android.util.Pair;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.api.common.APINetworkUtility;
import ch.defiant.purplesky.api.internal.JSONTranslator;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.api.postits.IPostitAdapter;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PostIt;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.exceptions.WrongCredentialsException;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.HTTPURLResponseHolder;
import ch.defiant.purplesky.util.HTTPURLUtility;
import ch.defiant.purplesky.util.StringUtility;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class PostitAdapter implements IPostitAdapter {

    private static final String TAG = PostitAdapter.class.getSimpleName();

    @Override
    public List<PostIt> getReceivedPostIts(AdapterOptions options) throws IOException, PurpleSkyException {
        ArrayList<PostIt> list = new ArrayList<PostIt>();

        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(PostitAPIConstants.POSTIT_RECEIVED_URL);

        return getPostits(options, list, sb);
    }

    private List<PostIt> getPostits(AdapterOptions options, ArrayList<PostIt> list, StringBuilder urlBuilder) throws IOException, PurpleSkyException {
        int number = 20;

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

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
        // Total count same as user object count
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(number)));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_MINIMAL));
        params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(number)));

        urlBuilder.append(HTTPURLUtility.createGetQueryString(params));

        URL url = new URL(urlBuilder.toString());
        JSONObject result = APINetworkUtility.performGETRequestForJSONObject(url);
        long check = result.optLong(PurplemoonAPIConstantsV1.JSON_LASTCHECK_TIMESTAMP, -1);
        JSONArray postits = result.optJSONArray(PurplemoonAPIConstantsV1.JSON_POSTIT_ARRAY);
        JSONArray users = result.optJSONArray(PurplemoonAPIConstantsV1.JSON_USER_ARRAY);

        Map<String, MinimalUser> userMap = JSONTranslator.translateToUsers(users, MinimalUser.class);

        if (userMap != null) { // Add to cache
            UserService service = PurpleSkyApplication.get().getUserService();
            for (MinimalUser u : userMap.values()) {
                service.addToCache(u);
            }
        }

        if (postits == null) {
            return list;
        }

        for (int i = 0, size = postits.length(); i < size; i++) {
            JSONObject object = postits.optJSONObject(i);
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
            PostIt p = JSONTranslator.translateToPostIt(object, fromUnixTime);
            if (p != null) {
                p.setSender(userMap.get(profileId));
                list.add(p);
            }
        }

        return list;
    }

    @Override
    public List<PostIt> getGivenPostIts(AdapterOptions options) throws IOException, PurpleSkyException {
        ArrayList<PostIt> list = new ArrayList<PostIt>();

        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(PostitAPIConstants.POSTIT_GIVEN_URL);

        return getPostits(options, list, sb);
    }

    @Override
    public List<Pair<Integer, String>> getPostitOptions(String profileId) throws IOException, PurpleSkyException {
        ArrayList<Pair<Integer, String>> list = new ArrayList<Pair<Integer, String>>();
        if (StringUtility.isNullOrEmpty(profileId)) {
            return list;
        }

        URL url = new URL(PurplemoonAPIConstantsV1.BASE_URL + PostitAPIConstants.POSTIT_GETOPTIONS_URL + profileId);
        JSONArray array = APINetworkUtility.performGETRequestForJSONArray(url);

        if (array != null) {
            for (int i = 0, size = array.length(); i < size; i++) {
                JSONObject object = array.optJSONObject(i);
                if (object == null) {
                    continue;
                }
                int value = object.optInt(PurplemoonAPIConstantsV1.JSON_POSTIT_ID, -1);
                String text = object.optString(PurplemoonAPIConstantsV1.JSON_POSTIT_TEXT);
                list.add(new Pair<Integer, String>(value, text));
            }
        }
        return list;
    }

    @Override
    public boolean createPostit(String profileId, Integer postitValue, String postitCustomText) throws IOException, WrongCredentialsException,
            PurpleSkyException {
        if (profileId == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "No profileId to send postit");
            }
            return false;
        }
        if (postitValue == null && StringUtility.isNullOrEmpty(postitCustomText)) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "No value to send postit");
            }
            return false;
        } else if (postitValue != null && StringUtility.hasText(postitCustomText)) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Both text and value when sending postit!");
            }
            return false;
        }

        URL url = new URL(PurplemoonAPIConstantsV1.BASE_URL + PostitAPIConstants.POSTIT_CREATE_URL);

        List<NameValuePair> body = new ArrayList<NameValuePair>();
        body.add(new BasicNameValuePair(PostitAPIConstants.POSTIT_CREATE_POSTIT_PROFILEID, profileId));

        if (postitValue != null) {
            body.add(new BasicNameValuePair(PostitAPIConstants.POSTIT_CREATE_POSTIT_VALUE, String.valueOf(postitValue)));
        } else {
            body.add(new BasicNameValuePair(PostitAPIConstants.POSTIT_CREATE_POSTIT_CUSTOMTEXT, postitCustomText));
        }

        HTTPURLResponseHolder result = APINetworkUtility.postForResponseHolderNoThrow(url, body, null);
        if (result.isSuccessful()) {
            return true;
        } else {
            // TODO Handle error better

            // Undiagnosed error
            return false;
        }
    }
}
