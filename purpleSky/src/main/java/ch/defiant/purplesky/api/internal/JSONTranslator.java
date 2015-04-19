package ch.defiant.purplesky.api.internal;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ch.defiant.purplesky.beans.AlertBean;
import ch.defiant.purplesky.beans.NotificationBean;
import ch.defiant.purplesky.beans.OnlineBean;
import ch.defiant.purplesky.beans.PurplemoonLocation;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.util.DateUtility;

class JSONTranslator { // TODO Make package private

    private static final String TAG = JSONTranslator.class.getSimpleName();

    public static OnlineBean translateToOnlineBean(JSONObject obj) {
        if (obj == null)
            return null;
        OnlineBean bean = new OnlineBean();
        try {
            if (obj.has(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID)) {
                String string;
                string = obj.getString(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID);
                bean.setProfileId(string);
            }
            if (obj.has(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUS)) {
                String string = obj.getString(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUS);
                OnlineStatus status =  APIUtility.toOnlineStatus(string);
                bean.setOnlineStatus(status);
            }
            if (obj.has(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUSTEXT)) {
                String string;
                string = obj.getString(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUSTEXT);
                bean.setOnlineStatusText(string);
            }
            if (obj.has(PurplemoonAPIConstantsV1.JSON_USER_ONLINESINCE_TIMESTAMP)) {
                long timestamp = obj.getLong(PurplemoonAPIConstantsV1.JSON_USER_ONLINESINCE_TIMESTAMP);
                bean.setOnlineSince(DateUtility.getFromUnixTime(timestamp));
            }

        } catch (JSONException e) {
            Log.w(TAG, "Translation from JSON to onlinebean failed", e);
            return null;
        }

        return bean;
    }

    public static NotificationBean translateToNotificationBean(JSONObject obj) {
        if (obj == null)
            return null;

        NotificationBean bean = new NotificationBean();
        long timestamp = obj.optLong(PurplemoonAPIConstantsV1.NOTIFICATIONS_MESSAGES_LASTUPDATE_TIMESTAMP, -1);
        if (timestamp != -1) {
            bean.setLastMessagesUpdate(DateUtility.getFromUnixTime(timestamp));
        }
        long timestampStat = obj.optLong(PurplemoonAPIConstantsV1.NOTIFICATIONS_STATUS_LASTUPDATE_TIMESTAMP, -1);
        if (timestampStat != -1) {
            bean.setLastStatusUpdate(DateUtility.getFromUnixTime(timestampStat));
        }
        long timestampFavorites = obj.optLong(PurplemoonAPIConstantsV1.NOTIFICATIONS_FAVORITES_LASTUPDATE_TIMESTAMP, -1);
        if (timestampFavorites != -1) {
            bean.setLastFavoritesUpdate(DateUtility.getFromUnixTime(timestampFavorites));
        }

        long timestampMsgsRcvd = obj.optLong(PurplemoonAPIConstantsV1.NOTIFICATIONS_LASTNEW_MESSAGE_RECEIVED_TIMESTAMP, -1);
        if (timestampMsgsRcvd != -1) {
            bean.setLastMessageReceived(DateUtility.getFromUnixTime(timestampMsgsRcvd));
        }
        long timestampGeneralNews = obj.optLong(PurplemoonAPIConstantsV1.NOTIFICATIONS_ALERTS_LASTUPDATE_TIMESTAMP, -1);
        if (timestampGeneralNews != -1) {
            bean.setLastGeneralNewsUpdate(DateUtility.getFromUnixTime(timestampGeneralNews));
        }

        return bean;
    }

    public static AlertBean translateToAlertBean(JSONObject obj) {
        if (obj == null) {
            return null;
        }

        final int NULL = -1;
        AlertBean bean = new AlertBean();

        int unseenVisits = obj.optInt(PurplemoonAPIConstantsV1.ALERTS_VISITS_UNSEEN, NULL);
        if (unseenVisits != NULL) {
            bean.setUnseenVisits(unseenVisits);
        }
        int unseenPostits = obj.optInt(PurplemoonAPIConstantsV1.ALERTS_POSTITS_UNSEEN, NULL);
        if (unseenPostits != NULL) {
            bean.setUnseenPostits(unseenPostits);
        }
        int unseenGroupInvites = obj.optInt(PurplemoonAPIConstantsV1.ALERTS_GROUPINVITES_UNSEEN, NULL);
        if (unseenGroupInvites != NULL) {
            bean.setUnseenGroupInvites(unseenGroupInvites);
        }
        int unseenActivitiesOwn = obj.optInt(PurplemoonAPIConstantsV1.ALERTS_ACTIVITIES_OWN_UNSEEN, NULL);
        if (unseenActivitiesOwn != NULL) {
            bean.setUnseenActivitiesOwn(unseenActivitiesOwn);
        }
        int unseenActivitiesAffected = obj.optInt(PurplemoonAPIConstantsV1.ALERTS_ACTIVITIES_AFFECTED_UNSEEN, NULL);
        if (unseenActivitiesAffected != NULL) {
            bean.setUnseenActivitiesAffected(unseenActivitiesAffected);
        }
        int unseenForumThreads = obj.optInt(PurplemoonAPIConstantsV1.ALERTS_FORUMTHREADS_UNSEEN, NULL);
        if (unseenForumThreads != NULL) {
            bean.setUnseenForumThreads(unseenForumThreads);
        }
        int unconfirmedFriends = obj.optInt(PurplemoonAPIConstantsV1.ALERTS_FRIENDS_UNCONFIRMED, NULL);
        if (unconfirmedFriends != NULL) {
            bean.setUnconfirmedFriends(unconfirmedFriends);
        }
        int unconfirmedKnown = obj.optInt(PurplemoonAPIConstantsV1.ALERTS_PERSONALLYKNOWN_UNCONFIRMED, NULL);
        if (unconfirmedKnown != NULL) {
            bean.setUnconfirmedPersonallyKnown(unconfirmedKnown);
        }
        int unconfirmedPicMarks = obj.optInt(PurplemoonAPIConstantsV1.ALERTS_PICTUREMARKS_UNCONFIRMED, NULL);
        if (unconfirmedPicMarks != NULL) {
            bean.setUnconfirmedPictureMarks(unconfirmedPicMarks);
        }

        return bean;
    }

    public static PurplemoonLocation toPurplemoonLocation(JSONObject obj){
        String type = obj.optString(PurplemoonAPIConstantsV1.LOCATIONS_TYPE);

        double latitude = obj.optDouble(PurplemoonAPIConstantsV1.LOCATIONS_LATITUDE);
        double longitude = obj.optDouble(PurplemoonAPIConstantsV1.LOCATIONS_LONGITUE);
        String locationName = obj.optString(PurplemoonAPIConstantsV1.LOCATIONS_NAME);
        String countryCode = obj.optString(PurplemoonAPIConstantsV1.LOCATIONS_COUNTRYCODE);
        String address = obj.optString(PurplemoonAPIConstantsV1.LOCATIONS_ADDRESS);

        PurplemoonLocation location = new PurplemoonLocation(APIUtility.toLocationType(type),
                locationName, countryCode, address, longitude, latitude);
        return location;
    }

}
