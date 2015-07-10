package ch.defiant.purplesky.constants;

public class PreferenceConstants {
    private static final String BASE = "ch.defiant.purplesky.preferences.";

    public static final String gcmToken = BASE + "gcmToken";

    public static final String oAuthToken = BASE + "oauthToken";
    public static final String userprofileId = BASE + "userprofileId";
    
    public static final String lastVersionInt = BASE + "lastVersionInt";

    public static final String updateEnabled = BASE + "updateEnabled";
    public static final String lastPushRegistrationAttempt = BASE + "lastPushRegistrationAttempt";

    /********************
     * Simple user search
     ********************/
    private static final String searchBase = BASE + "searchsimple.";

    public static final String searchTarget = searchBase + "target";
    public static final String searchTargetPerson = searchBase + "targetPerson";
    public static final String searchAgeMin = searchBase + "ageMin";
    public static final String searchAgeMax = searchBase + "ageMax";
    public static final String searchUseDistance = searchBase + "orderDistance";
    public static final String searchOnlineOnly = searchBase + "onlineOnly";
    public static final String searchCountry = searchBase + "country";

    /***************
     * Radar search
     ***************/
    private static final String radaroptionsBase = BASE + "radar.searchoptions.";
    public static final String radarOptionsBundle = radaroptionsBase + "optionsBundle";

    public static final String powerUserExpiry = searchBase + "powerUserExpiry";

    public static final String cachedOwnUserProfilePictureUrl = BASE + "cachedOwnUserProfilePictureUrl";
    public static final String cachedOwnUsername = BASE + "cachedOwnUsername";
    public static final String cachedOwnUserPropertyExpiry = BASE + "cachedOwnUserPropertyExpiry";

    private static final String lastSeenBase = BASE + "lastSeen.";
    public static final String lastSeenMessageTimestamp = lastSeenBase + "message";
    public static final String lastSeenVisitTimestamp = lastSeenBase + "visit";
    public static final String lastSeenMessagePostit = lastSeenBase + "postit";
    public static final String lastSeenPhotovotesTimestamp = lastSeenBase + "votes";

    private static final String notificationBase = BASE + "notification.";
    public static final String notificationVibrateEnabled = notificationBase + "vibrate";
    public static final String notificationCustomSound = notificationBase + "sound";

    /**
     * Time of last user notification for events except chats.
     */
    public static final String lastEventNotification = notificationBase + "lastEventNotification";

    public static final String notifyForMessages = notificationBase + "notifyForMessages";
    public static final String notifyForVisits = notificationBase + "notifyForVisits";
    public static final String notifyForPostit = notificationBase + "notifyForPostits";
    public static final String notifyForVotes = notificationBase + "notifyForVotes";

    public static final String radarLocationUpdateDialogShown = radaroptionsBase + "radarLocationUpdateDialogShown";
    public static final String radarAutomaticLocationUpdateEnabled = radaroptionsBase + "autoLocationUpdate";
    public static final String radarLastLocationUpdate = radaroptionsBase + "lastLocationUpdate";


    /*
     * Promotions
     */

    private static final String promotionBase = BASE + "promotion.";
    public static final String promotionEnabled = promotionBase + "enabled";
    public static final String promotionLastShown = promotionBase + "lastShown";

}
