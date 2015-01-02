package ch.defiant.purplesky.api.promotions.internal;

/**
 * @author Patrick Bänziger
 */
class PromotionAPIConstants {

    public static final String PROMOTION_URL = "/notifications/promos";

    public abstract static class Promotion {
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

    public abstract static class Event {

        public static final String JSON_ID = "event_id";
        public static final String JSON_PRIVATE = "private";
        public static final String JSON_PRELIMINARY = "preliminary";
        public static final String JSON_NAME = "event_name";
        public static final String JSON_ADMISSION = "admission";
        public static final String JSON_DESCRIPTION = "description";
        public static final String JSON_DATEFROM = "date_from";
        public static final String JSON_DATEUNTIL = "date_till";
        public static final String JSON_GENDERS = "genders";
        public static final String JSON_AGEMIN = "age_min";
        public static final String JSON_AGEMAX = "age_max";
        public static final String JSON_REGISTRATIONS = "registrations";
        public static final String JSON_JOURNEYS = "journeys";
        public static final String JSON_REGISTRATION = "registration";
        public static final String JSON_REGISTRATION_VISIBILITY = "visible_for";
        public static final String JSON_REGISTRATION_VISIBILITY_ALL = "all";
        public static final String JSON_REGISTRATION_VISIBILITY_FRIENDS_AND_KNOWN = "friends_known";
        public static final String JSON_REGISTRATION_VISIBILITY_FRIENDS = "friends";
        public static final String JSON_REGISTRATION_VISIBILITY_KNOWN = "known";
        public static final String JSON_REGISTRATION_VISIBILITY_NONE = "nobody";
        public static final String JSON_LOCATION = "location";

        public static final String JSON_GENDERS_MEN_ONLY = "men_only";
        public static final String JSON_GENDERS_WOMEN_ONLY = "women_only";
        public static final String JSON_GENDERS_MOSTLY_MEN= "mostly_men";
        public static final String JSON_GENDERS_MOSTLY_WOMEN = "mostly_women";
        public static final String JSON_GENDERS_ALL = "all";

        public static final int MAX_AGE_NULL_VALUE = 250;

        public static final String REGISTRATION_VISIBILITY_ARG = "visible_for";
    }

    public abstract static class EventLocation {
        public static final String JSON_ID = "location_id";
        public static final String JSON_COUNTRYCODE = "country";
        public static final String JSON_REGIONCODE = "region";
        public static final String JSON_VILLAGE = "village";
        public static final String JSON_LOCATIONNAME = "name";
        public static final String JSON_ADDRESS = "address";
        public static final String JSON_LATITUDE = "lat";
        public static final String JSON_LONGITUDE = "lng";
        public static final String JSON_WEBSITE = "website";
    }

    public static final String REGISTER_URL = "/events/register/";
    public static final String UNREGISTER_URL = "/events/unregister/";


    public static final String REGISTER_ERROR_NOTFOUND = "not_found";
    public static final String REGISTER_ERROR_PRELIMINARY = "preliminary";
    public static final String REGISTER_ERROR_TOOYOUNG = "too_young";
    public static final String REGISTER_ERROR_TOOOLD = "too_old";
    public static final String REGISTER_ERROR_WRONGGENDER = "wrong_gender";
}


