package ch.defiant.purplesky.api.visit.internal;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.defiant.purplesky.api.common.APINetworkUtility;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.api.users.UserJSONTranslator;
import ch.defiant.purplesky.api.visit.IVisitAdapter;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.VisitsMadeBean;
import ch.defiant.purplesky.beans.VisitsReceivedBean;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.HTTPURLUtility;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class VisitAdapter implements IVisitAdapter {

    @Override
    public List<VisitsReceivedBean> getReceivedVists(AdapterOptions options, Date overrideLastDateCheck) throws IOException, PurpleSkyException {
        StringBuilder builder = new StringBuilder();

        ArrayList<Pair<String,String>> params = new ArrayList<>();
        int number = 20;
        if (options != null) {
            if (options.getStart() != null) {
                params.add(new Pair<>(PurplemoonAPIConstantsV1.START_PARAM, String.valueOf(options.getStart())));
            }
            if (options.getNumber() != null) {
                number = options.getNumber();
            }
            if (options.getSinceTimestamp() != null) {
                long s = DateUtility.getUnixTime(options.getSinceTimestamp());
                Pair<String,String> time = new Pair<>(PurplemoonAPIConstantsV1.SINCE_TIMESTAMP_PARAM, String.valueOf(s));
                params.add(time);
            }
        }
        params.add(new Pair<>(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(number)));
        params.add(new Pair<>(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_MINIMAL));
        params.add(new Pair<>(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(number)));

        builder.append(HTTPURLUtility.createGetQueryString(params));
        URL url = new URL(PurplemoonAPIConstantsV1.BASE_URL + VisitAPIConstants.VISITORS_URL + builder.toString());

        JSONObject obj = APINetworkUtility.performGETRequestForJSONObject(url);
        if (obj == null) {
            return null;
        }

        List<VisitsReceivedBean> result = VisitJSONTranslator.translateToVisitsReceivedList(obj, overrideLastDateCheck);
        if (result == null || result.isEmpty()) {
            return result;
        }

        JSONArray users = obj.optJSONArray(PurplemoonAPIConstantsV1.JSON_USER_ARRAY);
        Map<String, MinimalUser> userMap = UserJSONTranslator.translateToUsers(users, MinimalUser.class);
        if (userMap != null) { // Add to cache
            UserService service = PurpleSkyApplication.get().getUserService();
            for (MinimalUser u : userMap.values()) {
                service.addToCache(u);
            }
        }

        for (VisitsReceivedBean bean : result) {
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

        ArrayList<Pair<String,String>> params = new ArrayList<>();
        int number = 20;
        if (options != null) {
            if (options.getStart() != null) {
                params.add(new Pair<>(PurplemoonAPIConstantsV1.START_PARAM, String.valueOf(options.getStart())));
            }
            if (options.getNumber() != null) {
                number = options.getNumber();
            }
            if (options.getSinceTimestamp() != null) {
                long s = DateUtility.getUnixTime(options.getSinceTimestamp());
                Pair<String,String> time = new Pair<>(PurplemoonAPIConstantsV1.SINCE_TIMESTAMP_PARAM, String.valueOf(s));
                params.add(time);
            }
        }
        params.add(new Pair<>(PurplemoonAPIConstantsV1.NUMBER_PARAM, String.valueOf(number)));
        params.add(new Pair<>(PurplemoonAPIConstantsV1.USEROBJ_TYPE_PARAM, PurplemoonAPIConstantsV1.USEROBJ_TYPE_MINIMAL));
        params.add(new Pair<>(PurplemoonAPIConstantsV1.USEROBJ_NUMBER_PARAM, String.valueOf(number)));

        builder.append(HTTPURLUtility.createGetQueryString(params));
        URL url = new URL(PurplemoonAPIConstantsV1.BASE_URL + VisitAPIConstants.VISITS_MADE_URL + builder.toString());

        JSONObject obj = APINetworkUtility.performGETRequestForJSONObject(url);
        if (obj == null) {
            return null;
        }

        List<VisitsMadeBean> result = VisitJSONTranslator.translateToVisitsMadeList(obj);
        if (result == null || result.isEmpty()) {
            return result;
        }

        JSONArray users = obj.optJSONArray(PurplemoonAPIConstantsV1.JSON_USER_ARRAY);
        Map<String, MinimalUser> userMap = UserJSONTranslator.translateToUsers(users, MinimalUser.class);
        if (userMap != null) { // Add to cache
            UserService service = PurpleSkyApplication.get().getUserService();
            for (MinimalUser u : userMap.values()) {
                service.addToCache(u);
            }
        }

        for (VisitsMadeBean bean : result) {
            if (bean == null) {
                continue;
            }
            bean.setUser(userMap.get(bean.getProfileId()));
        }

        return result;
    }
}
