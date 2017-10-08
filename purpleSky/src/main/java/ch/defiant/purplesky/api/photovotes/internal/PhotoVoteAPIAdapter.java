package ch.defiant.purplesky.api.photovotes.internal;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.common.APINetworkUtility;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.api.photovotes.IPhotoVoteAdapter;
import ch.defiant.purplesky.api.users.UserJSONTranslator;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PhotoVoteBean;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.HTTPURLResponseHolder;
import ch.defiant.purplesky.util.HTTPURLUtility;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class PhotoVoteAPIAdapter implements IPhotoVoteAdapter{

    private static final String TAG = PhotoVoteAPIAdapter.class.getSimpleName();

    @Override
    public int getRemainingPhotoVotes() throws IOException, PurpleSkyException {
        JSONObject obj = APINetworkUtility.performGETRequestForJSONObject(new URL(PurplemoonAPIConstantsV1.BASE_URL + PhotoVoteAPIConstants.PHOTOVOTE_REMAINING_URL));
        return obj.optInt(PhotoVoteAPIConstants.JSON_PHOTOVOTES_REMAINING, 0);
    }

    @Override
    public PhotoVoteBean getNextPhotoVoteAndVote(PhotoVoteBean bean) throws IOException, PurpleSkyException {
        URL u = new URL(PurplemoonAPIConstantsV1.BASE_URL + PhotoVoteAPIConstants.PHOTOVOTE_VOTE_URL);
        if (bean == null) {
            JSONObject res = APINetworkUtility.performGETRequestForJSONObject(u);
            return PhotoVoteJSONTranslator.translateToPhotoVoteBean(res, MinimalUser.class);
        } else {
            ArrayList<Pair<String,String>> body = new ArrayList<>();
            body.add(new Pair<>(PhotoVoteAPIConstants.JSON_PHOTOVOTE_VOTEID, String.valueOf(bean.getVoteId())));
            body.add(new Pair<>(PhotoVoteAPIConstants.JSON_PHOTOVOTE_VERDICT, PhotoVoteAPIUtility.translatePhotoVoteVerdict(bean.getVerdict())));
            HTTPURLResponseHolder resp = APINetworkUtility.performPOSTRequestForResponseHolder(u, body, null);
            try {
                return PhotoVoteJSONTranslator.translateToPhotoVoteBean(new JSONObject(resp.getOutput()), MinimalUser.class);
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

    private List<PhotoVoteBean> getVotes(boolean given, AdapterOptions opts) throws IOException, PurpleSkyException {
        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        if (given) {
            sb.append(PhotoVoteAPIConstants.PHOTOVOTE_GIVEN_URL);
        } else {
            sb.append(PhotoVoteAPIConstants.PHOTOVOTE_RECEIVED_URL);
        }
        int number = 20;

        ArrayList<Pair<String,String>> params = new ArrayList<>();

        if (opts != null) {
            if (opts.getStart() != null) {
                params.add(new Pair<>(PurplemoonAPIConstantsV1.START_PARAM, String.valueOf(opts.getStart())));
            }
            if (opts.getNumber() != null) {
                number = opts.getNumber();
            }
            if (opts.getSinceTimestamp() != null) {
                long s = DateUtility.getUnixTime(opts.getSinceTimestamp());
                Pair<String,String> time = new Pair<>(PurplemoonAPIConstantsV1.SINCE_TIMESTAMP_PARAM, String.valueOf(s));
                params.add(time);
            }
        }
        // Total count same as user object count
        params.add(new Pair<>(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(number)));
        params.add(new Pair<>(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_MINIMAL));
        params.add(new Pair<>(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(number)));

        sb.append(HTTPURLUtility.createGetQueryString(params));

        URL url = new URL(sb.toString());
        JSONObject result = APINetworkUtility.performGETRequestForJSONObject(url);
        long check = result.optLong(PurplemoonAPIConstantsV1.JSON_LASTCHECK_TIMESTAMP, -1);
        JSONArray votes = result.optJSONArray(PhotoVoteAPIConstants.JSON_PHOTOVOTES_VOTES);
        JSONArray users = result.optJSONArray(PurplemoonAPIConstantsV1.JSON_USER_ARRAY);

        Map<String, MinimalUser> userMap = UserJSONTranslator.translateToUsers(users, MinimalUser.class);
        if (userMap != null) { // Add to cache
            UserService service = PurpleSkyApplication.get().getUserService();
            for (MinimalUser u : userMap.values()) {
                service.addToCache(u);
            }
        }

        if (votes == null) {
            return Collections.emptyList();
        }
        ArrayList<PhotoVoteBean> list = new ArrayList<>();
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
            PhotoVoteBean p = PhotoVoteJSONTranslator.translateToPhotoVoteBean(object, MinimalUser.class);
            if (p != null) {
                p.setUser(userMap.get(profileId));
                list.add(p);
            }
        }

        return list;
    }

}
