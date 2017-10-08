package ch.defiant.purplesky.api.visit.internal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.beans.VisitsMadeBean;
import ch.defiant.purplesky.beans.VisitsReceivedBean;
import ch.defiant.purplesky.util.DateUtility;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class VisitJSONTranslator {
    private static final String TAG = VisitJSONTranslator.class.getSimpleName();

    /**
     *
     * @param obj
     * @param overrideLastCheck
     * @return
     */
    public static List<VisitsReceivedBean> translateToVisitsReceivedList(JSONObject obj, Date overrideLastCheck) {
        if (obj == null) {
            return Collections.emptyList();
        }

        long lastcheck = obj.optLong(PurplemoonAPIConstantsV1.JSON_LASTCHECK_TIMESTAMP, System.currentTimeMillis());
        if (overrideLastCheck != null) {
            lastcheck = overrideLastCheck.getTime();
        }

        JSONArray visitors = obj.optJSONArray(VisitAPIConstants.JSON_VISITORS_ARRAY);
        if (visitors == null) {
            return Collections.emptyList();
        }

        ArrayList<VisitsReceivedBean> list = new ArrayList<>();
        for (int i = 0, size = visitors.length(); i < size; i++) {
            VisitsReceivedBean bean = translateToVisitReceivedBean(visitors.optJSONObject(i), lastcheck);
            if (bean != null) {
                list.add(bean);
            }
        }
        return list;
    }

    private static VisitsReceivedBean translateToVisitReceivedBean(JSONObject obj, long lastCheckDate) {
        if (obj == null) {
            return null;
        }

        String profileId = obj.optString(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID, null);
        if (profileId == null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "No profile id in visit. Skipping");
            }
            return null;
        }

        JSONArray visitTimes = obj.optJSONArray(VisitAPIConstants.JSON_VISITS_OF_VISITORS);
        if (visitTimes == null || visitTimes.length() == 0) {
            return null; // No visit dates - useless
        }

        VisitsReceivedBean bean = new VisitsReceivedBean();
        bean.setProfileId(profileId);

        TreeMap<Date, Boolean> visits = new TreeMap<>();
        bean.setVisits(visits);

        for (int i = 0, size = visitTimes.length(); i < size; i++) {
            long visitLong = visitTimes.optLong(i, -1);
            if (visitLong == -1) {
                continue;
            }

            Date visitDate = DateUtility.getFromUnixTime(visitLong);
            boolean isNew = visitDate.getTime() >= lastCheckDate;
            visits.put(visitDate, isNew);
        }
        return bean;
    }

    public static List<VisitsMadeBean> translateToVisitsMadeList(JSONObject obj) {
        if (obj == null) {
            return Collections.emptyList();
        }

        JSONArray visits = obj.optJSONArray(VisitAPIConstants.JSON_VISITS_OF_VISITORS);
        if (visits == null) {
            return Collections.emptyList();
        }

        ArrayList<VisitsMadeBean> list = new ArrayList<>();
        for (int i = 0, size = visits.length(); i < size; i++) {
            VisitsMadeBean bean = translateToVisitMadeBean(visits.optJSONObject(i));
            if (bean != null) {
                list.add(bean);
            }
        }
        return list;
    }

    private static VisitsMadeBean translateToVisitMadeBean(JSONObject obj) {
        if (obj == null) {
            return null;
        }

        String profileId = obj.optString(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID, null);
        if (profileId == null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "No profile id in visit. Skipping");
            }
            return null;
        }

        long timestamp = obj.optLong(VisitAPIConstants.JSON_VISITS_TIMESTAMP, -1);
        if (timestamp == -1) {
            return null; // No visit timestamp - useless
        }

        VisitsMadeBean bean = new VisitsMadeBean();
        bean.setProfileId(profileId);

        TreeMap<Date, Boolean> visits = new TreeMap<>();
        bean.setVisits(visits);

        Date visitDate = DateUtility.getFromUnixTime(timestamp);
        visits.put(visitDate, false);

        return bean;
    }

}
