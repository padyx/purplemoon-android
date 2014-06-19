package ch.defiant.purplesky.constants;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PurpleSkyApplication;

public class PurplemoonAPIConstantsV1 {

    public static final String BASE_URL = "https://api.ppmoon.com/v1";
    public static final char REQUEST_DIVIDER = ',';

    public static final char BASE64_DIVIDER = ':';
    public static final String AUTH_HEADER_NAME = "Authorization";
    public static final String AUTH_HEADER_VALUEPREFIX = "Bearer ";

    /*
     * OAuth related
     */
    public static final String OAUTH_TOKENREQUEST_URL = "/oauth/token";
    public static final String JSON_OAUTH_ACCESSTOKEN = "access_token";

    public static final String OAUTH_POSTPARAM_CLIENT_ID = "client_id";
    public static final String OAUTH_POSTPARAM_CLIENT_SECRET = "client_secret";
    public static final String OAUTH_POSTPARAM_GRANTTYPE = "grant_type";
    public static final String OAUTH_POSTPARAM_GRANTTYPE_PASSWORD = "password";
    public static final String OAUTH_POSTPARAM_USERNAME = "username";
    public static final String OAUTH_POSTPARAM_PASSWORD = "password";

    /*
     * General parameters for use in the url
     */
    public static final String SINCE_TIMESTAMP_PARAM = "since_timestamp";
    public static final String NUMBER_PARAM = "number";
    /**
     * @deprecated Use {@link #UPTO_TIMESTAMP_PARAM} or similar.
     */
    @Deprecated
    public static final String START_PARAM = "start";
    public static final String UPTO_TIMESTAMP_PARAM = "upto_timestamp";
    public static final String USEROBJ_NUMBER_PARAM = "user_obj_num";

    /**
     * Results number. Special case for {@link IPurplemoonAPIAdapter#searchUserByName(String, UserSearchOptions)}
     * and {@link }IPurplemoonAPIAdapter#searchUser(UserSearchOptions}
     */
    public static final String RESULTSNUMBER_PARAM = "results_number";

    /*
     * General return values
     */
    public static final String JSON_LASTCHECK_TIMESTAMP = "last_check";
    public static final String JSON_USER_ARRAY = "users";

    /*
     * User related
     */
    public static final String USER_MINIMALDATA_URL = "/users/minimal/";
    public static final String USER_MINIMALDATA_WITHSTATUS_URL = "/users/minimal_online/";
    public static final String USER_PREVIEWDATA_URL = "/users/preview/";
    public static final String USER_PREVIEWDATA_WITHSTATUS_URL = "/users/preview_online/";
    public static final String USER_DETAILEDDATA_URL = "/users/details/";
    public static final String USER_DETAILEDDATA_WITHSTATUS_URL = "/users/details_online/";

    public static final String USER_BASICDATA_ME_URL = "/users/me/";
    public static final String USER_DETAILEDDATA_ME_URL = "/me/details";

    /*
     * Search related
     */
    public static final String USER_SEARCH_BYNAME = "/search/profile_name/";
    public static final String USEROBJ_TYPE_PARAM = "user_obj";
    public static final String USEROBJ_TYPE_MINIMAL = "minimal";
    public static final String USEROBJ_TYPE_BASIC = "basic";
    public static final String USEROBJ_TYPE_PREVIEW = "preview";
    public static final String USEROBJ_TYPE_MINIMAL_WITHSTATUS = "minimal_online";
    public static final String USEROBJ_TYPE_BASIC_WITHSTATUS = "basic_online";
    public static final String USEROBJ_TYPE_PREVIEW_WITHSTATUS = "preview_online";

    // Basic attributes
    public static final String JSON_USER_PROFILE_ID = "profile_id";
    public static final String JSON_USER_NAME = "name";
    public static final String JSON_USER_GENDER = "gender";
    public static final String JSON_USER_SEXUALITY = "sexuality";
    public static final String JSON_USER_AGE_INT = "age";
    public static final String JSON_USER_VERIFIED_BOOL = "verified";
    public static final String JSON_USER_PICTUREDIR_URL = "picture";
    public static final String JSON_USER_PROFILESTATUS = "status";
    // Extended Attributes
    public static final String JSON_USER_HASNOTES_BOOL = "noted";
    public static final String JSON_USER_ISFRIEND_BOOL = "friend";
    public static final String JSON_USER_ISKNOWN = "known";
    public static final String JSON_USER_NOTES = "notes";
    public static final String JSON_USER_ONLINESTATUS = "online_status";
    public static final String JSON_USER_ONLINESTATUSTEXT = "online_status_text";
    public static final String JSON_USER_ONLINESINCE_TIMESTAMP = "online_since";

    public static final String JSON_USER_SEXUALITY_HOMOSEXUAL_VALUE = "homo";
    public static final String JSON_USER_SEXUALITY_HETEROSEXUAL_VALUE = "straight";
    public static final String JSON_USER_SEXUALITY_BISEXUAL_VALUE = "bi";

    /*
     * Favorite related
     */
    public static final String FAVORITES_ONLINE_URL = "/favorites/online";
    public static final String FAVORITES_ONLINECOUNT_URL = "/favorites/online_count";

    public static final String JSON_FAVORITES_ONLINECOUNT = "users";

    /*
     * Picture Folder related
     */
    public static final String PICTUREFOLDER_FOLDERSONLY_URL = "/users/folders/";
    public static final String PICTUREFOLDER_FOLDERSONLY_ME_URL = "/me/folders";

    public static final String PICTUREFOLDER_WITHPICTURES_URL = "/users/pictures/";

    public static final String JSON_PICTUREFOLDER_FOLDERS = "folders";
    public static final String JSON_PICTUREFOLDER_ID = "folder_id";
    public static final String JSON_PICTUREFOLDER_IDS = "folder_ids";
    public static final String JSON_PICTUREFOLDER_NAME = "name";
    public static final String JSON_PICTUREFOLDER_PICTURE_COUNT = "number";
    public static final String JSON_PICTUREFOLDER_PASSPROTECTED_BOOL = "password_protected";
    public static final String JSON_PICTUREFOLDER_ACCESSGRANTED_BOOL = "access_granted";
    public static final int PICTUREFOLDER_PICTURELIMIT = 100;

    public static final String JSON_PICTUREFOLDER_PICTURE_ARRAY = "pictures";
    public static final String JSON_PICTUREFOLDER_PICTURE_ID = "pic_id";
    public static final String JSON_PICTUREFOLDER_PICTURE_DATE = "date";
    public static final String JSON_PICTUREFOLDER_PICTURE_URL = "url";
    public static final String JSON_PICTUREFOLDER_PICTURE_MAX_WIDTH = "max_width";
    public static final String JSON_PICTUREFOLDER_PICTURE_MAX_HEIGHT = "max_height";

    public static final String USERPICTURE_URLPOSTFIX_MINUSCULE = "50";
    public static final String USERPICTURE_URLPOSTFIX_TINY = "75";
    public static final String USERPICTURE_URLPOSTFIX_SMALL = "100";
    public static final String USERPICTURE_URLPOSTFIX_MEDIUM = "500";
    public static final String USERPICTURE_URLPOSTFIX_LARGE = "700";
    public static final String USERPICTURE_URLPOSTFIX_LARGER = "1000";
    public static final String USERPICTURE_URLPOSTFIX_VERYLARGE = "1500";
    public static final String USERPICTURE_URLPOSTFIX_XLARGE = "2000";

    /*
     * Picture Upload related
     */
    public static final String PICTURE_UPLOAD_URL = "/me/picture_add";
    public static final String PICTURE_POST_PICTURE = "picture";
    public static final String PICTURE_POST_PICTUREURI = "pictureUri";
    public static final String PICTURE_POST_FOLDER = "folder_id";
    public static final String PICTURE_POST_DESCRIPTION = "description";

    /*
     * Message related
     */
    public static final String MESSAGE_SEND_URL = "/chats/send/";
    public static final String MESSAGE_SEND_UNREAD_HANDLING_PARAM = "unread_handling";
    public static final String MESSAGE_SEND_UNREAD_HANDLING_SEND = "send";
    public static final String MESSAGE_SEND_UNREAD_HANDLING_ABORT = "abort";
    public static final String MESSAGE_SEND_UNREAD_HANDLING_TIMESTAMPSINCE = "unread_known_ts";

    public static final String MESSAGE_CHATLIST_URL = "/chats/list/";
    public static final String MESSAGE_CHATSHOW_URL = "/chats/show/";
    public static final String MESSAGE_CHATSTATUS_URL = "/chats/status/";

    public static final String MESSAGE_CHATLIST_ORDER_PARAM = "order";
    public static final String MESSAGE_CHATLIST_ORDER_LASTCONTACT = "last_contact";
    public static final String MESSAGE_CHATLIST_ORDER_UNREADONLY = "unread_only";
    public static final String MESSAGE_CHATLIST_ORDER_UNREADFIRST = "unread_first";
    public static final String MESSAGE_CHATLIST_INCLUDEEXCERPT = "include_excerpt";
    public static final String MESSAGE_CHATLIST_EXCERPTLENGTH = "excerpt_length";

    public static final String MESSAGE_CHATSHOW_ORDER_PARAM = "order";
    public static final String MESSAGE_CHATSHOW_ORDER_NEWESTFIRST = "newest_first";
    public static final String MESSAGE_CHATSHOW_ORDER_OLDESTFIRST = "oldest_first";
    public static final String MESSAGE_CHATSHOW_SINCE_MESSAGEID = "since_msg_id";
    public static final String MESSAGE_CHATSHOW_UPTO_MESSAGEID = "upto_msg_id";

    public static final String JSON_CHATLIST_CHATS = "chats";
    public static final String JSON_CHATLIST_UNOPENEDCOUNT = "unopened";
    public static final String JSON_CHATLIST_LASTRECEIVED = "last_received";
    public static final String JSON_CHATLIST_LASTSENT = "last_sent";
    public static final String JSON_CHATLIST_EXCERPT = "excerpt";
    public static final String JSON_CHATLIST_MESSAGEEXIST_BOOL = "msgs_exist";
    public static final String JSON_CHATLIST_OTHERUSER_LASTREAD = "user_last_read";

    public static final String JSON_MESSAGE_MESSAGECOUNT = "msgs";
    public static final String JSON_MESSAGE_USER_LASTCONTACT_TIMESTAMP = "last_contact";

    public static final String JSON_MESSAGE_ID = "msg_id";
    public static final String JSON_MESSAGE_TEXT = "msg";
    public static final String JSON_MESSAGE_SENT_TIMESTAMP = "sent";
    public static final String JSON_MESSAGE_TYPE = "type";
    public static final String JSON_MESSAGE_PROFILEID = "profile_id";

    public static final String JSON_MESSAGE_TYPE_ALL = "all";
    public static final String JSON_MESSAGE_TYPE_UNOPENED = "unopened";
    public static final String JSON_MESSAGE_TYPE_RECEIVED = "received";
    public static final String JSON_MESSAGE_TYPE_SENTUNOPENED = "sent_unopened";
    public static final String JSON_MESSAGE_TYPE_SENT = "sent";

    public static final String JSON_MESSAGE_SEND_UNREAD_MSGS = "unread_msgs";
    public static final String JSON_MESSAGE_SEND_NEWMSG = "new_msg";

    /**
     * Restriction enum for restricting or reordering the retrieved chats.
     * 
     * @see IPurplemoonAPIAdapter#getRecentContacts(Integer, Integer, MessageRetrievalRestrictionType)
     */
    public enum MessageRetrievalRestrictionType {
        /**
		 * 
		 */
        UNREAD_FIRST(MESSAGE_CHATLIST_ORDER_UNREADFIRST),
        /**
		 * 
		 */
        UNOPENED_ONLY(MESSAGE_CHATLIST_ORDER_UNREADONLY),
        /**
		 * 
		 */
        LAST_CONTACT(MESSAGE_CHATLIST_ORDER_LASTCONTACT);

        private final String m_apiValue;

        private MessageRetrievalRestrictionType(String apiValue) {
            m_apiValue = apiValue;
        }

        public String getApiValue() {
            return m_apiValue;
        }
    }

    /*
     * Image related
     */
    public static final String PREVIEWIMAGE_URLPOSTFIX_TINY = "35";
    public static final String PREVIEWIMAGE_URLPOSTFIX_SMALL = "50";
    public static final String PREVIEWIMAGE_URLPOSTFIX_MEDIUM = "100";
    public static final String PREVIEWIMAGE_URLPOSTFIX_LARGE = "200";

    /*
     * Notification related
     */
    public static final String NOTIFICATIONS_URL = "/notifications";
    public static final String NOTIFICATIONS_MESSAGES_LASTUPDATE_TIMESTAMP = "msgs_ts";
    public static final String NOTIFICATIONS_LASTNEW_MESSAGE_RECEIVED_TIMESTAMP = "msgs_received_ts";
    public static final String NOTIFICATIONS_STATUS_LASTUPDATE_TIMESTAMP = "status_ts";
    public static final String NOTIFICATIONS_FAVORITES_LASTUPDATE_TIMESTAMP = "favs_ts";
    public static final String NOTIFICATIONS_ALERTS_LASTUPDATE_TIMESTAMP = "news_ts";

    public static final String ALERTS_URL = "/notifications/alerts";
    public static final String ALERTS_VISITS_UNSEEN = "visits_unseen";
    public static final String ALERTS_POSTITS_UNSEEN = "postits_unseen";
    public static final String ALERTS_PHOTOVOTES_UNSEEN = "photo_votes_unseen";
    public static final String ALERTS_GROUPINVITES_UNSEEN = "group_invitations_unseen";
    public static final String ALERTS_ACTIVITIES_OWN_UNSEEN = "activities_own_unseen";
    public static final String ALERTS_ACTIVITIES_AFFECTED_UNSEEN = "activities_affected_unseen";
    public static final String ALERTS_FORUMTHREADS_UNSEEN = "forum_threads_unseen";
    public static final String ALERTS_FRIENDS_UNCONFIRMED = "friends_unconfirmed";
    public static final String ALERTS_PERSONALLYKNOWN_UNCONFIRMED = "known_unconfirmed";
    public static final String ALERTS_PICTUREMARKS_UNCONFIRMED = "pic_marks_unconfirmed";

    /*
     * Post-it related
     */
    public static final String POSTIT_RECEIVED_URL = "/postits/received";
    public static final String POSTIT_GIVEN_URL = "/postits/sent";

    public static final String JSON_POSTIT_ARRAY = "postits";
    public static final String JSON_POSTIT_ID = "value";
    public static final String JSON_POSTIT_TEXT = "text";
    public static final String JSON_POSTIT_CUSTOM_BOOLEAN = "custom";
    public static final String JSON_POSTIT_CREATE_TIMESTAMP = "created";

    public static final String POSTIT_CREATE_URL = "/postits/create";
    public static final String POSTIT_GETOPTIONS_URL = "/postits/available_options/";

    public static final String POSTIT_CREATE_POSTIT_PROFILEID = "profile_id";
    public static final String POSTIT_CREATE_POSTIT_VALUE = "postit_value";
    public static final String POSTIT_CREATE_POSTIT_CUSTOMTEXT = "postit_text";
    public static final int POSTIT_CUSTOM_MAXLENGTH = 50;

    /*
     * Online Status related
     */
    public static final String MY_ONLINESTATUS_URL = "/me/online_status";
    public static final String MY_ONLINESTATUS_STATUS_PARAM = "status";
    public static final String MY_ONLINESTATUS_CUSTOMTEXT_PARAM = "text";
    public static final int MY_ONLINESTATUS_CUSTOM_MAXLENGTH = 20;

    public static final String ONLINESTATUS_ONLINE = "online";
    public static final String ONLINESTATUS_BUSY = "busy";
    public static final String ONLINESTATUS_AWAY = "away";
    public static final String ONLINESTATUS_INVISIBLE = "invisible";

    public static final String ONLINESTATUS_URL = "/users/online_status/";

    /*
     * Visitors-related
     */
    public static final String VISITORS_URL = "/me/visitors";

    public static final String JSON_VISITORS_LASTCHECK = "last_check";
    public static final String JSON_VISITORS_ARRAY = "visitors";

    public static final String JSON_VISITS_OF_VISITORS = "visits";
    public static final String JSON_VISITS_TIMESTAMP = "timestamp";

    public static final String VISITS_MADE_URL = "/me/visits_made";
    public static final String VISIT_MADE_VISIBLE = "visible";

    /*
     * User search related
     */
    public static final String USERSEARCH_URL = "/search/users";

    public static final String USERSEARCH_CURRPOS_LATITUDE_PARAM = "lat";
    public static final String USERSEARCH_CURRPOS_LONGITUDE_PARAM = "lng";

    public static final String USERSEARCH_TYPE_PARAM = "type";
    public static final String USERSEARCH_TYPE_FRIENDS = "friends";
    public static final String USERSEARCH_TYPE_PARTNER = "partner";

    public static final String USERSEARCH_CRITERIA_JSON_PARAM = "criteria";

    public static final String JSON_USERSEARCH_AGEMIN = "age_min";
    public static final String JSON_USERSEARCH_AGEMAX = "age_max";
    public static final String JSON_USERSEARCH_COUNTRY = "country";
    public static final String JSON_USERSEARCH_GENDER_SEXUALITY = "gender_sexuality";
    public static final char JSON_USERSEARCH_GENDER_SEXUALITY_SEPARATOR = '_';
    public static final String JSON_USERSEARCH_DISTANCE_KM = "distance";

    /**
     * JSON value type: String
     */
    public static final String JSON_USERSEARCH_ONLINE_ONLY = "is_online";
    public static final String JSON_USERSEARCH_ONLINE_PARAM_NOW = "now";
    public static final String JSON_USERSEARCH_ONLINE_PARAM_PAST_HOUR = "recently";
    public static final String JSON_USERSEARCH_ONLINE_PARAM_PAST_DAY = "24hours";
    public static final String JSON_USERSEARCH_ONLINE_PARAM_PAST_WEEK = "7days";
    public static final String JSON_USERSEARCH_ONLINE_PARAM_PAST_MONTH= "30days";


    public static final String USERSEARCH_ORDER_PARAM = "order";
    public static final String USERSEARCH_ORDER_LAST_UPDATED = "last_updated";
    public static final String USERSEARCH_ORDER_LAST_ONLINE = "last_online";
    public static final String USERSEARCH_ORDER_CREATED = "created";
    public static final String USERSEARCH_ORDER_DISTANCE = "distance";

    public static final int USERSEARCH_MAXDIST_NONPOWERUSER = 29;

    public static enum UserSearchOrder {
        LAST_UPDATED("last_updated", PurpleSkyApplication.get().getString(R.string.LastUpdated)),
        LAST_ONLINE("last_online", PurpleSkyApplication.get().getString(R.string.LastOnline)),
        CREATED("created", PurpleSkyApplication.get().getString(R.string.Created)),
        DISTANCE("distance", PurpleSkyApplication.get().getString(R.string.Distance));

        private String m_apiValue;
        private String m_localizedString;

        private UserSearchOrder(String apiValue, String localizedString) {
            setApiValue(apiValue);
            setLocalizedString(localizedString);
        }

        public String getLocalizedString() {
            return m_localizedString;
        }

        private void setLocalizedString(String localizedString) {
            m_localizedString = localizedString;
        }

        public String getApiValue() {
            return m_apiValue;
        }

        private void setApiValue(String apiValue) {
            m_apiValue = apiValue;
        }

    }

    public static class ProfileDetails {
        public static final String BIRTHDATE = "birthdate";
        public static final String FIRST_NAME = "first_name";
        public static final String NICKNAMES = "nicknames";
        public static final String LAST_NAME = "last_name";
        public static final String EMAIL_ADDRESS = "email_address";

        public static final String HEIGHT = "height";
        public static final String WEIGHT = "weight";
        public static final String PHYSIQUE = "physique";
        public static final String EYE_COLOR = "eye_color";
        public static final String HAIR_COLOR = "hair_color";
        public static final String HAIR_LENGTH = "hair_length";
        public static final String FACIAL_HAIR = "facial_hair";

        public static final String OCCUPATION_LIST = "occupations";

        public static final String RELIGION = "religion";
        public static final String POLITICS = "politics";
        public static final String DRINKER = "drinker";
        public static final String SMOKER = "smoker";
        public static final String VEGETARIAN = "vegeterian";
        public static final String WANTS_KIDS = "kidswant";
        public static final String HAS_KIDS = "kidshave";

        public static final String CHATS_FREQUENCY = "chat_frequency";
        public static final String CHATS_WHICH = "which_chats";
        public static final String CHATS_NAMES = "chat_names";
        public static final String HOMEPAGE = "homepage";

        public static final String TARGET_PARTNER = "target_partner";
        public static final String TARGET_PARTNER_RELATIONSHIPSTATUS = "status";
        public static final String TARGET_FRIENDS = "target_friends";

        public static final String PROFILE_CREATION_DATE = "create_date";
        public static final String PROFILE_LASTUPDATE_DATE = "last_update";
        public static final String PROFILE_LASTONLINE_DATE = "last_online";

        public static final String LOCATION_CURRENT = "current_location";
        public static final String LOCATION_HOME = "home_location";
        public static final String LOCATION_HOME2 = "home2_location";

        public static final String PROFILE_DATE_LAST24h = "last24h";
    }

    public static final String PHOTOVOTE_VOTE_URL = "/photovotes/vote";
    public static final String PHOTOVOTE_REMAINING_URL = "/photovotes/remaining";
    public static final String PHOTOVOTE_RECEIVED_URL = "/photovotes/received";
    public static final String PHOTOVOTE_GIVEN_URL = "/photovotes/sent";

    public static final String JSON_PHOTOVOTE_VOTEID = "vote_id";
    public static final String JSON_PHOTOVOTE_PROFILEID = "profile_id";
    public static final String JSON_PHOTOVOTE_MAXWIDTH = "max_width";
    public static final String JSON_PHOTOVOTE_MAXHEIGHT = "max_height";
    public static final String JSON_PHOTOVOTE_XPOS_FLOAT = "xpos";
    public static final String JSON_PHOTOVOTE_YPOS_FLOAT = "ypos";
    public static final String JSON_PHOTOVOTE_PICTUREURL = "picture";
    public static final String JSON_PHOTOVOTE_PREVIOUS = "previous";
    public static final String JSON_PHOTOVOTE_USER = "user";

    public static final String JSON_PHOTOVOTES_REMAINING = "photovotes";
    public static final String JSON_PHOTOVOTES_LASTCHECK = "last_check";
    /**
     * JSONArray of Votes
     */
    public static final String JSON_PHOTOVOTES_VOTES = "photovotes";
    public static final String JSON_PHOTOVOTES_USERS = "users";

    public static final String JSON_PHOTOVOTE_TYPE = "type";
    public static final String JSON_PHOTOVOTE_VERDICT = "verdict";
    // Verdicts
    public static final int JSON_PHOTOVOTE_VERDICT_NEUTRAL_NEGATIVE = 0;
    public static final int JSON_PHOTOVOTE_VERDICT_CUTE_ATTRACTIVE = 1;
    public static final int JSON_PHOTOVOTE_VERDICT_VERY_ATTRACTIVE = 2;
    public static final int JSON_PHOTOVOTE_VERDICT_STUNNING = 3;

    public static final String JSON_PHOTOVOTE_CREATED = "created";
    public static final String JSON_LOCATION_LAT = "lat";
    public static final String JSON_LOCATION_LONG = "lng";
    public static final String JSON_LOCATION_COUNTRYID = "country";

    public static final String JSON_LOCATION_NAME = "name";

    public static final String POWERUSER_STATUS_URL = "/me/supporter";
    public static final String JSON_POWERUSER_EXPIRY = "power_user";

    public static class Errors {
        public static final String JSON_ERROR_TYPE = "error";

        /**
         * Indicates that the application user is blocking the user that he is trying to contact.
         */
        public static final String JSON_ERROR_TYPE_BLOCKINGUSER = "blockinguser";
        /**
         * Indicates that the application user is being blocked by the user that he is trying to contact.
         */
        public static final String JSON_ERROR_TYPE_BLOCKEDBYOTHER = "blockedbyuser";

        public static final String JSON_ERROR_TYPE_NOTEXT = "notext";
        public static final String JSON_ERROR_TYPE_TEXTTOOLONG = "texttoolong";
        public static final String JSON_ERROR_TYPE_USERNOTFOUND = "usernotfound";
        public static final String JSON_ERROR_TYPE_TOOMANYPOSTITSWITHUSER = "toomanywithuser";
        public static final String JSON_ERROR_TYPE_TOOMANYPOSTITSGENERAL = "toomanyingeneral";
    }

    /*
     * Push Notification related
     */
    public static final String PUSH_NOTIFICATION_URL = "/push";

    public static final String PUSH_NOTIFICATION_ACTION_ARG = "action";
    public static final String PUSH_NOTIFICATION_ACTION_REGISTER = "register";
    public static final String PUSH_NOTIFICATION_ACTION_UNREGISTER = "unregister";

    public static final String PUSH_NOTIFICATION_DEVICETOKEN = "device_token";

    public static final String JSON_PUSH_TYPE = "type";
    public static final String JSON_PUSH_TYPE_GCM = "gcm";
    public static final String JSON_PUSH_ACTIVE = "active";
    public static final String JSON_PUSH_LASTPUSH_TIMESTAMP = "last_push_ts";
    public static final String JSON_PUSH_DEVICETOKEN = "device_token";

    public static final String GCM_EXTRA_TYPE = "type";
    public static final String GCM_EXTRA_TYPE_NEWS = "news";
    public static final String GCM_EXTRA_TYPE_CHATS = "chats";
    public static final String GCM_EXTRA_USERS = "users";
    public static final String GCM_EXTRA_NEWEST = "newest";
    public static final String GCM_EXTRA_CHATS_LASTRECEIVED = "last_received";
    public static final String GCM_EXTRA_CHATS_LASTSENT = "last_sent";
    public static final String GCM_EXTRA_CHATS_LASTVIEWED = "last_viewed";
    public static final String GCM_EXTRA_CHATS_EXCERPT = "excerpt";
    /**
     * Overall unread
     */
    public static final String GCM_EXTRA_CHATS_UNREAD = "unread";
    /**
     * Within a conversation
     */
    public static final String GCM_EXTRA_CHATS_UNOPENED = "unopened";

    public static final String GCM_EXTRA_VOTESUNSEEN = "photo_votes_unseen";
    public static final String GCM_EXTRA_VISITSUNSEEN = "visits_unseen";
    public static final String GCM_EXTRA_POSTITSUNSEEN = "postits_unseen";
    public static final String GCM_EXTRA_VOTES_LASTTIMESTAMP = "photo_votes_ts";
    public static final String GCM_EXTRA_VISITS_LASTTIMESTAMP = "visits_ts";
    public static final String GCM_EXTRA_POSTITS_LASTTIMESTAMP = "postits_ts";

}
