package ch.defiant.purplesky.api.promotions.internal;

/**
 * @author Patrick Bänziger
 */
class PromotionAPIConstants {

    public static final String PROMOTION_URL = "/notifications/promos";

    public static class Promotion {
        public static final String JSON_ID = "id";
        public static final String JSON_TITLE = "title";
        public static final String JSON_TEXT = "text";
        public static final String JSON_PICTURE = "picture";
        public static final String JSON_PICTURE_URL = "url";
        public static final String JSON_PICTURE_WIDTH = "width";
        public static final String JSON_PICTURE_HEIGHT = "height";
        public static final String JSON_PROMOURL = "url";
        public static final String JSON_EVENTID = "event_id";
        public static final String JSON_VALIDFROM = "valid_from";
        public static final String JSON_VALIDTO = "valid_till";
        public static final String JSON_IMPORTANCE = "importance";
    }

    public static final String EVENT_URL = "/events/data/";
    public static final String EVENT_TYPE_EVENT = "event";
    public static final String EVENT_TYPE_LOCATION = "location";
    public static final String EVENT_TYPE_ORGANIZER = "organizer";
    public static final String EVENT_TYPE_REGISTRATION = "registration";
    public static final String EVENT_TYPE_JOURNEY = "journey";
    public static final String EVENT_TYPE_FLYERS = "flyers";

    public static final String EVENT_TYPE_CONCAT = "+";

    public static class Event {

        public static final String JSON_ID = "event_id";
        public static final String JSON_PRIVATE = "private";
        public static final String JSON_PRELIMINARY = "preliminary";
        public static final String JSON_NAME = "event_name";
        public static final String JSON_ADMISSION = "admission";
        public static final String JSON_DESCRIPTION = "description";
        public static final String JSON_DATEFROM = "date_from";
        public static final String JSON_DATEUNTIL = "date_till";
        public static final String JSON_ALLOWEDGENERS = "genders_allowed";
        public static final String JSON_AGEMIN = "age_min";
        public static final String JSON_AGEMAX = "age_max";
        public static final String JSON_REGISTRATIONS = "registrations";
        public static final String JSON_JOURNEYS = "journeys";
        public static final String JSON_REGISTRATION = "registration";
        public static final String JSON_REGISTRATION_VISIBILITY = "visible_for";
        public static final String JSON_REGISTRATION_VISiBILITY_ALL = "all";
        public static final String JSON_REGISTRATION_VISiBILITY_FRIENDS_AND_KNOWN = "friends_known";
        public static final String JSON_REGISTRATION_VISiBILITY_FRIENDS = "friends";
        public static final String JSON_REGISTRATION_VISiBILITY_KNOWN = "known";
        public static final String JSON_REGISTRATION_VISiBILITY_NONE = "nobody";

        public static final int MAX_AGE_NULL_VALUE = 250;
    }

}


