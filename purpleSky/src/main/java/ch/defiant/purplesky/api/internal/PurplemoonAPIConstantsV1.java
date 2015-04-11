package ch.defiant.purplesky.api.internal;

// TODO Make this package-private
public class PurplemoonAPIConstantsV1 {

    public static final String HOST = "api.ppmoon.com";
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
    public static final String JSON_USER_GENDER_MALE = "male";
    public static final String JSON_USER_GENDER_FEMALE = "female";

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

    //Detailed Attributes

    // Relationship status
    public static final String JSON_USER_RELATIONSHIP_STATUS = "status";
    public static final String JSON_USER_RELATIONSHIP_STATUS_SINGLE = "single";
    public static final String JSON_USER_RELATIONSHIP_STATUS_LONGTERM = "long-term";
    public static final String JSON_USER_RELATIONSHIP_STATUS_ENGANGED = "engaged";
    public static final String JSON_USER_RELATIONSHIP_STATUS_MARRIED = "married";
    public static final String JSON_USER_RELATIONSHIP_STATUS_OPEN = "open";

    public static final String JSON_USER_RELATIONSHIP_MAXDISTANCE = "distance";
    public static final String JSON_USER_RELATIONSHIP_AGEFROM = "age_from";
    public static final String JSON_USER_RELATIONSHIP_AGETO = "age_till";
    public static final String JSON_USER_RELATIONSHIP_TEXT = "text";

    /*
     * Favorite related
     */
    public static final String FAVORITES_ONLINE_URL = "/favorites/online";
    public static final String FAVORITES_ONLINECOUNT_URL = "/favorites/online_count";

    public static final String JSON_FAVORITES_ONLINECOUNT = "users";

    /*
     * Picture Folder related
     */
    public static final String JSON_PICTUREFOLDER_FOLDERS = "folders";
    public static final String JSON_PICTUREFOLDER_ID = "folder_id";
    public static final String JSON_PICTUREFOLDER_IDS = "folder_ids";
    public static final String JSON_PICTUREFOLDER_NAME = "name";
    public static final String JSON_PICTUREFOLDER_PICTURE_COUNT = "number";
    public static final String JSON_PICTUREFOLDER_PASSPROTECTED_BOOL = "password_protected";
    public static final String JSON_PICTUREFOLDER_ACCESSGRANTED_BOOL = "access_granted";

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
    public static final String JSON_POSTIT_ARRAY = "postits";
    public static final String JSON_POSTIT_ID = "value";
    public static final String JSON_POSTIT_TEXT = "text";
    public static final String JSON_POSTIT_CUSTOM_BOOLEAN = "custom";
    public static final String JSON_POSTIT_CREATE_TIMESTAMP = "created";

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

    public static class ProfileDetails {
        public static final String BIRTHDATE = "birthdate";
        public static final String FIRST_NAME = "first_name";
        public static final String NICKNAMES = "nicknames";
        public static final String LAST_NAME = "last_name";
        public static final String EMAIL_ADDRESS = "email_address";

        public static final String HEIGHT = "height";
        public static final String WEIGHT = "weight";

        public static final String PHYSIQUE = "physique";
        public static final String PHYSIQUE_SLIM = "slim";
        public static final String PHYSIQUE_NORMAL = "normal";
        public static final String PHYSIQUE_ATHLETIC = "athletic";
        public static final String PHYSIQUE_BODYBUILDER = "bodybuilder";
        public static final String PHYSIQUE_STURDY = "sturdy";
        public static final String PHYSIQUE_LITTLE_TUMMY = "littletummy";
        public static final String PHYSIQUE_CHUBBY = "chubby";

        public static final String EYE_COLOR = "eye_color";
        public static final String EYE_COLOR_LIGHTBROWN = "light-brown";
        public static final String EYE_COLOR_DARKBROWN = "dark-brown";
        public static final String EYE_COLOR_BROWN = "brown";
        public static final String EYE_COLOR_LIGHTBLUE = "light-blue";
        public static final String EYE_COLOR_DARKBLUE = "dark-blue";
        public static final String EYE_COLOR_BLUE = "blue";
        public static final String EYE_COLOR_BLACK = "black";
        public static final String EYE_COLOR_GREEN = "green";
        public static final String EYE_COLOR_BLUEGREY = "blue-grey";
        public static final String EYE_COLOR_BLUEGREEN = "blue-green";
        public static final String EYE_COLOR_GREENBROWN = "green-brown";
        public static final String EYE_COLOR_GREENGREY = "green-grey";
        public static final String EYE_COLOR_GREY = "grey";

        public static final String HAIR_COLOR = "hair_color";
        public static final String HAIR_COLOR_LIGHTBROWN = "light-brown";
        public static final String HAIR_COLOR_DARKBROWN =  "dark-brown";
        public static final String HAIR_COLOR_BROWN =  "brown";
        public static final String HAIR_COLOR_LIGHTBLONDE =  "light-blonde";
        public static final String HAIR_COLOR_DARKBLONDE =  "dark-blonde";
        public static final String HAIR_COLOR_BLONDE =  "blonde";
        public static final String HAIR_COLOR_BLACK =  "black";
        public static final String HAIR_COLOR_RED =  "red";
        public static final String HAIR_COLOR_LIGHTGREY =  "light-grey";
        public static final String HAIR_COLOR_DARKGREY =  "dark-grey";
        public static final String HAIR_COLOR_DYEDRED =  "dyed-red";
        public static final String HAIR_COLOR_DYEDBLACK =  "dyed-black";
        public static final String HAIR_COLOR_DYEDBLUE =  "dyed-blue";
        public static final String HAIR_COLOR_DYEDGREEN =  "dyed-green";
        public static final String HAIR_COLOR_DYEDBLONDE =  "dyed-blonde";
        public static final String HAIR_COLOR_DYEDPURPLE =  "dyed-purple";

        public static final String HAIR_LENGTH = "hair_length";
        public static final String HAIR_LENGTH_BALD = "bald";
        public static final String HAIR_LENGTH_SHORT = "short";
        public static final String HAIR_LENGTH_MEDIUM = "medium";
        public static final String HAIR_LENGTH_LONG = "long";

        public static final String FACIAL_HAIR = "facial_hair";
        public static final String FACIAL_HAIR_NONE = "none";
        public static final String FACIAL_HAIR_SHAVED = "shaved";
        public static final String FACIAL_HAIR_THREEDAY = "three-day";
        public static final String FACIAL_HAIR_MUSTACHE = "mustache";
        public static final String FACIAL_HAIR_PETITGOATEE = "petit-goatee";
        public static final String FACIAL_HAIR_GOATEE = "goatee";
        public static final String FACIAL_HAIR_MUTTONCHOPS = "mutton-chops";
        public static final String FACIAL_HAIR_FULLBEARD = "full-beard";

        public static final String DRINKER = "drinker";
        public static final String DRINKER_NEVER = "never";
        public static final String DRINKER_SELDOM = "seldom";
        public static final String DRINKER_SOMEIMES = "sometimes";
        public static final String DRINKER_REGULARLY = "regularly";
        public static final String DRINKER_A_LOT = "a-lot";
        public static final String DRINKER_WINE_ONLY = "wine-only";

        public static final String OCCUPATION_LIST = "occupations";

        public static final String SMOKER = "smoker";
        public static final String SMOKER_JOINTS_ONLY  = "joints-only";
        public static final String SMOKER_REALLY_A_LOT  = "really-a-lot";
        public static final String SMOKER_A_LOT  = "a-lot";
        public static final String SMOKER_MODERATELY  = "moderately";
        public static final String SMOKER_ONWEEKENDS  = "on-weekends";
        public static final String SMOKER_ALMOST_NEVER  = "almost-never";
        public static final String SMOKER_NEVER  = "never";

        public static final String VEGETARIAN = "vegeterian";
        public static final String VEGETARIAN_YES = "yes";
        public static final String VEGETARIAN_NO = "no";
        public static final String VEGETARIAN_VEGAN = "vegan";

        public static final String WANTS_KIDS = "kids_want";
        public static final String WANTS_KIDS_YES = "yes";
        public static final String WANTS_KIDS_NO = "no";
        public static final String WANTS_KIDS_UNSURE = "unsure";

        public static final String HAS_KIDS = "kids_have";
        public static final String HAS_KIDS_NONE = "none";
        public static final String HAS_KIDS_ONE = "one";
        public static final String HAS_KIDS_MULTIPLE = "multiple";

        public static final String RELIGION = "religion";
        public static final String RELIGION_AGNOSTIC = "agnostic";
        public static final String RELIGION_ATHEIST = "atheist";
        public static final String RELIGION_ROMAN_CATHOLIC = "roman-catholic";
        public static final String RELIGION_OLD_CATHOLIC = "old-catholic";
        public static final String RELIGION_EVANGELICAL_REFORMED = "evangelical-reformed";
        public static final String RELIGION_PROTESTANT = "protestant";
        public static final String RELIGION_RUSSIAN_ORTHODOX = "russian-orthodox";
        public static final String RELIGION_GREEK_ORTHODOX = "greek-orthodox";
        public static final String RELIGION_JEWISH_ORTHODOX = "jewish-orthodox";
        public static final String RELIGION_ORTHODOX = "orthodox";
        public static final String RELIGION_CHRISTIAN = "christian";
        public static final String RELIGION_FREECHURCH = "free-church";
        public static final String RELIGION_FOLK_RELIGION = "folk-religion";
        public static final String RELIGION_HINDU = "hindu";
        public static final String RELIGION_SUNNITE = "sunnite";
        public static final String RELIGION_SHIITE = "shiite";
        public static final String RELIGION_JEWISH = "jewish";
        public static final String RELIGION_BUDDHIST = "buddhist";
        public static final String RELIGION_SHINTO = "shinto";
        public static final String RELIGION_SPIRITUAL = "spiritual";
        public static final String RELIGION_OTHER = "other";

        public static final String POLITICS = "politics";
        public static final String POLITIC_NOINTEREST = "no-interest";
        public static final String POLITIC_RIGHT = "right";
        public static final String POLITICS_MIDDLERIGHT = "middle-right";
        public static final String POLITICS_MIDDLE = "middle";
        public static final String POLITICS_MIDDLELEFT = "middle-left";
        public static final String POLITICS_LEFT = "left";

        public static final String CHATS_FREQUENCY = "chat_frequency";
        public static final String CHATS_WHICH = "which_chats";
        public static final String CHATS_NAMES = "chat_names";
        public static final String HOMEPAGE = "homepage";

        public static final String TARGET_PARTNER = "target_partner";
        public static final String TARGET_FRIENDS = "target_friends";

        public static final String EVENTS_TMP = "events_tmp";
        public static final String EVENT_ID = "event_id";
        public static final String EVENT_TEXT = "event_text";

        public static final String PROFILE_CREATION_DATE = "create_date";
        public static final String PROFILE_LASTUPDATE_DATE = "last_update";
        public static final String PROFILE_LASTONLINE_DATE = "last_online";

        public static final String LOCATION_CURRENT = "current_location";
        public static final String LOCATION_HOME = "home_location";
        public static final String LOCATION_HOME2 = "home2_location";

        public static final String PROFILE_DATE_LAST24h = "last24h";
    }

    public static final String JSON_LOCATION_LAT = "lat";
    public static final String JSON_LOCATION_LONG = "lng";
    public static final String JSON_LOCATION_COUNTRYID = "country";

    public static final String JSON_LOCATION_NAME = "name";

    public static final String POWERUSER_STATUS_URL = "/me/supporter";
    public static final String JSON_POWERUSER_EXPIRY = "power_user";

    public static class Errors {
        public static final String JSON_ERROR_TYPE = "error";
        public static final String JSON_ERROR_DESCRIPTION = "error_description";

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

    /*
     * Location
     */
    public static final String LOCATIONS_URL = "/me/locations";
    public static final String LOCATIONS_TYPE = "type";
    public static final String LOCATIONS_TYPE_CURRENT = "current";
    public static final String LOCATIONS_TYPE_HOME = "home";
    public static final String LOCATIONS_TYPE_HOME2 = "home2";
    public static final String LOCATIONS_TYPE_WORK = "work";
    public static final String LOCATIONS_TYPE_WORK2 = "work2";
    public static final String LOCATIONS_TYPE_WORK3 = "work3";
    public static final String LOCATIONS_LATITUDE = "lat";
    public static final String LOCATIONS_LONGITUE = "lng";
    public static final String LOCATIONS_COUNTRYCODE = "country";
    public static final String LOCATIONS_NAME = "name";
    public static final String LOCATIONS_ADDRESS = "address";

}
