package ch.defiant.purplesky.api.photovotes.internal;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class PhotoVoteAPIConstants {

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

}
