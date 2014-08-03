package ch.defiant.purplesky.api.internal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1.ProfileDetails;
import ch.defiant.purplesky.beans.AlertBean;
import ch.defiant.purplesky.beans.BasicUser;
import ch.defiant.purplesky.beans.LocationBean;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.NotificationBean;
import ch.defiant.purplesky.beans.OnlineBean;
import ch.defiant.purplesky.beans.PhotoVoteBean;
import ch.defiant.purplesky.beans.Picture;
import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.beans.PostIt;
import ch.defiant.purplesky.beans.PreviewUser;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessageHead;
import ch.defiant.purplesky.beans.ProfileTriplet;
import ch.defiant.purplesky.beans.PurplemoonLocation;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.beans.VisitsMadeBean;
import ch.defiant.purplesky.beans.VisitsReceivedBean;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.enums.Gender;
import ch.defiant.purplesky.enums.MessageType;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.enums.ProfileStatus;
import ch.defiant.purplesky.translators.ProfileTranslator;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.StringUtility;
import ch.defiant.purplesky.util.UserUtility;

public class JSONTranslator { // TODO Make package private

    private static final String TAG = "JSONTranslator";

    public static <T extends MinimalUser> Map<String, T> translateToUsers(JSONArray array, Class<T> clazz) {
        // Translate users
        HashMap<String, T> userMap = new HashMap<String, T>();
        if (array == null) {
            return userMap;
        }

        for (int i = 0, size = array.length(); i < size; i++) {
            T translatedUser = JSONTranslator.translateToUser(array.optJSONObject(i), clazz);
            if (translatedUser != null && StringUtility.isNotNullOrEmpty(translatedUser.getUserId())) {
                userMap.put(translatedUser.getUserId(), translatedUser);
            }
        }

        return userMap;
    }

    public static <T extends MinimalUser> T translateToUser(JSONObject jsonUserObject, Class<T> clazz) {
        if (jsonUserObject == null)
            return null;
        T user = UserUtility.instantiateUser(clazz);

        try {
            // 'status': string,
            if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_PROFILESTATUS)) {
                String string = jsonUserObject.getString(PurplemoonAPIConstantsV1.JSON_USER_PROFILESTATUS);
                ProfileStatus status = ProfileStatus.getStatusByAPIValue(string);
                if (status == ProfileStatus.NOTFOUND) {
                    // No valid user.
                    return null;
                }
                user.setProfileStatus(status);
            }

            // 'profile_id': integer,
            if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID)) {
                String string = jsonUserObject.getString(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID);
                user.setUserId(string);
            }
            // 'name': string,
            if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_NAME)) {
                String name = jsonUserObject.getString(PurplemoonAPIConstantsV1.JSON_USER_NAME);
                user.setUsername(name);
            }

            // 'gender': string,
            Gender gender = null;
            if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_GENDER)) {
                String string = jsonUserObject.getString(PurplemoonAPIConstantsV1.JSON_USER_GENDER);
                gender = APIUtility.toGender(string);
                user.setGender(gender);
            }

            // 'sexuality': string,
            if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY)) {
                String string = jsonUserObject.getString(PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY);
                user.setSexuality(APIUtility.toSexuality(string));
            }

            // 'age': integer,
            if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_AGE_INT)) {
                int age = jsonUserObject.getInt(PurplemoonAPIConstantsV1.JSON_USER_AGE_INT);
                user.setAge(age);
            }
            // 'verified': boolean,
            if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_VERIFIED_BOOL)) {
                boolean verified = jsonUserObject.getBoolean(PurplemoonAPIConstantsV1.JSON_USER_VERIFIED_BOOL);
                user.setAgeVerified(verified);
            }
            // 'picture': url,
            if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_PICTUREDIR_URL)) {
                String string = jsonUserObject.getString(PurplemoonAPIConstantsV1.JSON_USER_PICTUREDIR_URL);
                try {
                    user.setProfilePictureURLDirectory(new URL(string));
                } catch (MalformedURLException e) {
                    Log.d(TAG, "Invalid picture base URL: '" + string + "'");
                }
            }

            if (BasicUser.class.isAssignableFrom(clazz)) {
                BasicUser basicUser = (BasicUser) user;

                // 'noted': boolean,
                if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_HASNOTES_BOOL)) {
                    boolean hasNotes = jsonUserObject.getBoolean(PurplemoonAPIConstantsV1.JSON_USER_HASNOTES_BOOL);
                    basicUser.setHasNotes(hasNotes);
                }
                // 'friend': boolean,
                if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_ISFRIEND_BOOL)) {
                    boolean isFriend = jsonUserObject.getBoolean(PurplemoonAPIConstantsV1.JSON_USER_ISFRIEND_BOOL);
                    basicUser.setFriend(isFriend);
                }
                // 'known': boolean,
                if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_ISKNOWN)) {
                    boolean isKnown = jsonUserObject.getBoolean(PurplemoonAPIConstantsV1.JSON_USER_ISKNOWN);
                    basicUser.setKnown(isKnown);
                }
                // 'notes': string,
                if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_NOTES)) {
                    String notes = jsonUserObject.getString(PurplemoonAPIConstantsV1.JSON_USER_NOTES);
                    basicUser.setNotes(notes);
                }
                // 'online_status': string,
                if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUS)) {
                    String string = jsonUserObject.getString(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUS);
                    OnlineStatus status =  APIUtility.toOnlineStatus(string);
                    basicUser.setOnlineStatus(status);
                } else {
                    basicUser.setOnlineStatus(OnlineStatus.OFFLINE);
                }

                // 'online_status_text': string,
                if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUSTEXT)) {
                    String text = jsonUserObject.getString(PurplemoonAPIConstantsV1.JSON_USER_ONLINESTATUSTEXT);
                    basicUser.setOnlineStatusText(text);
                }
                // 'online_since': timestamp
                if (jsonUserObject.has(PurplemoonAPIConstantsV1.JSON_USER_ONLINESINCE_TIMESTAMP)) {
                    long timestamp = jsonUserObject.getLong(PurplemoonAPIConstantsV1.JSON_USER_ONLINESINCE_TIMESTAMP);
                    basicUser.setOnlineSince(DateUtility.getFromUnixTime(timestamp));
                }
            }

            if (PreviewUser.class.isAssignableFrom(clazz)) {
                PreviewUser previewUser = (PreviewUser) user;

                // TODO Preview user needs to be implemented here (Occupation!)
                Map<String, ProfileTriplet> details = translateToUserDetails(jsonUserObject);
                addUserLocation(jsonUserObject, previewUser);
                previewUser.setProfileDetails(details);
            }

        } catch (JSONException e) {
            Log.w(TAG, "Translation from JSON to user failed", e);
            return null;
        }
        return user;
    }

    private static void addUserLocation(JSONObject obj, PreviewUser user) {
        if (obj.has(ProfileDetails.LOCATION_CURRENT)) {
            user.setCurrentLocation(translateToLocationBean(obj.optJSONObject(ProfileDetails.LOCATION_CURRENT)));
        }
        if (obj.has(ProfileDetails.LOCATION_HOME)) {
            user.setHomeLocation(translateToLocationBean(obj.optJSONObject(ProfileDetails.LOCATION_HOME)));
        }
        if (obj.has(ProfileDetails.LOCATION_HOME2)) {
            user.setHome2Location(translateToLocationBean(obj.optJSONObject(ProfileDetails.LOCATION_HOME2)));
        }
    }

    private static LocationBean translateToLocationBean(JSONObject obj) {
        if (obj == null) {
            return null;
        }
        double latid = obj.optDouble(PurplemoonAPIConstantsV1.JSON_LOCATION_LAT);
        double longit = obj.optDouble(PurplemoonAPIConstantsV1.JSON_LOCATION_LONG);
        String country = obj.optString(PurplemoonAPIConstantsV1.JSON_LOCATION_COUNTRYID, null);
        String locationDesc = obj.optString(PurplemoonAPIConstantsV1.JSON_LOCATION_NAME, null);
        return new LocationBean(longit, latid, country, locationDesc);
    }

    /**
     * Translates (recursively) all the properties into profile triplets.
     * 
     * @param jsonUserObject
     *            The JSON object to translate
     * @return List of profile triplets, localized.
     */
    private static Map<String, ProfileTriplet> translateToUserDetails(JSONObject jsonUserObject) {
        if (jsonUserObject == null)
            return Collections.emptyMap();

        HashMap<String, ProfileTriplet> list = new HashMap<String, ProfileTriplet>();

        Iterator<?> keys = jsonUserObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();

            String translatedKey = ProfileTranslator.translateAPIKey(key);
            if (translatedKey == null) {
                // Cannot display this, skip it (If not listed as translatable, either unknown or handled otherwise).
                continue;
            }

            Object object = jsonUserObject.opt(key);
            if (object == null || JSONObject.NULL.equals(object)) {
                // Don't translate
            } else if (object instanceof JSONObject) {
                Map<String, ProfileTriplet> objectDetails = translateToUserDetails((JSONObject) object);
                list.put(key, new ProfileTriplet(key, translatedKey, objectDetails));
            } else if (object instanceof JSONArray) {
                ArrayList<Map<String, ProfileTriplet>> objectDetails = translateToUserDetails((JSONArray) object);
                list.put(key, new ProfileTriplet(key, objectDetails));
            } else {
                // Must be a simple one (String, Integer, Double, Long, Boolean)
                // We try to translate string values, but may fail (user specified). But that won't worry us.
                String translatedValue = null;
                Object rawValue = null;
                if (object instanceof String) {
                    translatedValue = ProfileTranslator.translateAPIValue(key, (String) object);
                } else {
                    rawValue = object;
                    assert (rawValue instanceof Serializable) : "Non-serializable object! Class was: " + rawValue.getClass();
                }

                list.put(key, new ProfileTriplet(key, translatedKey, translatedValue, (Serializable) rawValue));
            }
        }

        return list;
    }

    private static ArrayList<Map<String, ProfileTriplet>> translateToUserDetails(JSONArray jsonArray) {
        // These must be ordered
        ArrayList<Map<String, ProfileTriplet>> list = new ArrayList<Map<String, ProfileTriplet>>();
        for (int i = 0, count = jsonArray.length(); i < count; i++) {
            JSONObject obj = jsonArray.optJSONObject(i);
            if (obj == null) {
                continue;
            }

            Map<String, ProfileTriplet> details = translateToUserDetails(obj);
            list.add(details);
        }

        return list;
    }

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

    public static List<PictureFolder> translateToPictureFolders(JSONObject obj) {
        ArrayList<PictureFolder> list = new ArrayList<PictureFolder>();
        if (obj == null)
            return list;

        if (!obj.has(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID)) {
            Log.d(TAG, "Cannot translate: No profileId associated with pictureFolder");
            return list;
        }

        try {
            String profileId = obj.getString(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID);

            // Get the folders
            if (obj.has(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_FOLDERS)) {
                JSONArray folders = obj.getJSONArray(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_FOLDERS);

                list = translateToPictureFolders(profileId, folders);
            }
        } catch (JSONException e) {
            Log.w(TAG, "Translation from JSON to PictureFolders failed", e);
        }
        return list;
    }

    private static Picture translateToPicture(JSONObject obj) {
        if (obj == null)
            return null;
        int pictureID = obj.optInt(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_ID, -1);
        String url = obj.optString(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_URL, null);

        if (pictureID == -1 || url == null)
            return null;
        Picture pic = new Picture();
        pic.setUrl(url);
        pic.setPictureId(pictureID);

        pic.setMaxWidth(obj.optInt(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_MAX_WIDTH));
        pic.setMaxHeight(obj.optInt(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_MAX_HEIGHT));

        long timestamp = obj.optLong(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_DATE, -1);
        if (timestamp != -1) {
            pic.setDate(DateUtility.getFromUnixTime(timestamp));
        }

        return pic;
    }

    public static ArrayList<PictureFolder> translateToPictureFolders(String profileId, JSONArray folders) {
        ArrayList<PictureFolder> list = new ArrayList<PictureFolder>();
        for (int i = 0, size = folders.length(); i < size; i++) {
            JSONObject jsonFolder = folders.optJSONObject(i);
            if (jsonFolder == null)
                continue;

            String id = jsonFolder.optString(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_ID, null);
            if (id == null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Missing folder id, cannot translate!");
                }
                continue;
            }

            PictureFolder folder = new PictureFolder();
            folder.setFolderId(id);
            folder.setProfileId(profileId);

            String name = jsonFolder.optString(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_NAME, "");
            folder.setName(name);

            int pictureCount = jsonFolder.optInt(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_COUNT, 0);
            folder.setDeclaredPictureCount(pictureCount);

            boolean isProtected = false;
            isProtected = jsonFolder.optBoolean(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PASSPROTECTED_BOOL, false);
            folder.setPasswordProtected(isProtected);

            boolean hasAccess = false;
            hasAccess = jsonFolder.optBoolean(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_ACCESSGRANTED_BOOL, true);
            folder.setAccessGranted(hasAccess);

            JSONArray pictures = jsonFolder.optJSONArray(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_ARRAY);
            if (pictures != null) {
                ArrayList<Picture> pictureList = new ArrayList<Picture>();
                for (int p = 0, psize = pictures.length(); p < psize; p++) {
                    JSONObject picture = pictures.optJSONObject(p);
                    if (picture != null) {
                        Picture pic = translateToPicture(picture);
                        if (pic != null)
                            pictureList.add(pic);
                    }
                }
                folder.setPictures(pictureList);
            }

            list.add(folder);
        }
        return list;
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

    public static PostIt translateToPostIt(JSONObject obj, Date lastcheck) {
        if (obj == null) {
            return null;
        }
        PostIt postIt = new PostIt();

        int id = obj.optInt(PurplemoonAPIConstantsV1.JSON_POSTIT_ID, -1);
        if (id != -1) {
            postIt.setId(id);
        }
        String text = obj.optString(PurplemoonAPIConstantsV1.JSON_POSTIT_TEXT, null);
        if (text != null) {
            postIt.setText(text);
        }
        boolean custom = obj.optBoolean(PurplemoonAPIConstantsV1.JSON_POSTIT_CUSTOM_BOOLEAN);
        postIt.setCustom(custom);

        long timeCreated = obj.optLong(PurplemoonAPIConstantsV1.JSON_POSTIT_CREATE_TIMESTAMP, -1);
        if (timeCreated != -1) {
            postIt.setDate(DateUtility.getFromUnixTime(timeCreated));
        }

        boolean isNew = false;
        if (timeCreated != -1 && lastcheck != null) {
            isNew = DateUtility.getFromUnixTime(timeCreated).after(lastcheck);
        }
        postIt.setNew(isNew);

        return postIt;
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

        JSONArray visitors = obj.optJSONArray(PurplemoonAPIConstantsV1.JSON_VISITORS_ARRAY);
        if (visitors == null) {
            return Collections.emptyList();
        }

        ArrayList<VisitsReceivedBean> list = new ArrayList<VisitsReceivedBean>();
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

        JSONArray visitTimes = obj.optJSONArray(PurplemoonAPIConstantsV1.JSON_VISITS_OF_VISITORS);
        if (visitTimes == null || visitTimes.length() == 0) {
            return null; // No visit dates - useless
        }

        VisitsReceivedBean bean = new VisitsReceivedBean();
        bean.setProfileId(profileId);

        TreeMap<Date, Boolean> visits = new TreeMap<Date, Boolean>();
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

        JSONArray visits = obj.optJSONArray(PurplemoonAPIConstantsV1.JSON_VISITS_OF_VISITORS);
        if (visits == null) {
            return Collections.emptyList();
        }

        ArrayList<VisitsMadeBean> list = new ArrayList<VisitsMadeBean>();
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

        long timestamp = obj.optLong(PurplemoonAPIConstantsV1.JSON_VISITS_TIMESTAMP, -1);
        if (timestamp == -1) {
            return null; // No visit timestamp - useless
        }

        VisitsMadeBean bean = new VisitsMadeBean();
        bean.setProfileId(profileId);

        TreeMap<Date, Boolean> visits = new TreeMap<Date, Boolean>();
        bean.setVisits(visits);

        Date visitDate = DateUtility.getFromUnixTime(timestamp);
        visits.put(visitDate, false);

        return bean;
    }

    /**
     * Translates the JSON input to a photovote bean. The previous vote is set if present, as is the user bean unless <code>userclazz</code> is null
     * 
     * @param obj
     *            JSON input
     * @param userclazz
     *            The class that the user object shall be translated to
     * @return The translated photo vote beans
     */
    public static PhotoVoteBean translateToPhotoVoteBean(JSONObject obj, Class<? extends MinimalUser> userclazz) {
        if (obj == null) {
            return null;
        }

        PhotoVoteBean b = new PhotoVoteBean();
        b.setVoteId(obj.optLong(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VOTEID));
        b.setMaxHeight(obj.optInt(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_MAXHEIGHT));
        b.setMaxWidth(obj.optInt(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_MAXWIDTH));
        b.setPictureUrlPrefix(obj.optString(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_PICTUREURL, null));
        b.setPosX(obj.optDouble(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_XPOS_FLOAT));
        b.setPosY(obj.optDouble(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_YPOS_FLOAT));
        b.setTimestamp(DateUtility.getFromUnixTime(obj.optLong(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_CREATED)));
        b.setVerdict(APIUtility.toPhotoVoteVerdict(obj.optInt(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VERDICT)));
        if (userclazz != null && obj.has(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_USER)) {
            b.setUser(translateToUser(obj.optJSONObject(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_USER), userclazz));
        }
        if (obj.has(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_PREVIOUS)) {
            b.setPreviousVote(translateToPhotoVoteBean(obj.optJSONObject(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_PREVIOUS), userclazz));
        }
        return b;
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
