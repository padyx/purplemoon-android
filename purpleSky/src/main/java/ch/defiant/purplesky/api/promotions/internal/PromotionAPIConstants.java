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
        public static final String JSON_PROMOURL = "url";
        public static final String JSON_EVENTID = "event_id";
        public static final String JSON_VALIDFROM = "valid_from";
        public static final String JSON_VALIDTO = "valid_till";
        public static final String JSON_IMPORTANCE = "importance";
    }

    public static final String EVENT_URL = "/eventstmp/details";

    public static class Event {

        public static final String JSON_ID = "event_id";
        public static final String JSON_STATUS = "status";
        public static final String JSON_PRELIMINARY = "preliminary";
        public static final String JSON_NAME = "name";
        public static final String JSON_ADMISSION = "admission";
        public static final String JSON_DESCRIPTION = "description";
        public static final String JSON_DATEFROM = "date_from";
        public static final String JSON_DATEUNTIL = "date_till";
        public static final String JSON_ALLOWEDGENERS = "genders_allowed";
        public static final String JSON_AGEMIN = "age_min";
        public static final String JSON_AGEMAX = "age_max";
        public static final String JSON_REGISTRATIONS = "registrations";
        public static final String JSON_JOURNEYS = "journeys";

        public static final String JSON_STATUS_PRIVATE = "private";
        public static final String JSON_STATUS_PUBLIC = "public";

        public static final int MAX_AGE_NULL_VALUE = 250;
    }

}


