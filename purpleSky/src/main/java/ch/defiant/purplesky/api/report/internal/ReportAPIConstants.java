package ch.defiant.purplesky.api.report.internal;

/**
 * API constants (v1) for reporting users
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class ReportAPIConstants {

    public static final String REPORT_URL = "/users/report/";
    public static final String REPORT_REASON_PARAM = "reason";
    public static final String REPORT_DESCRIPTION_PARAM = "description";

    public static final String REPORT_REASON_INSULT_THREAT = "insult_threat";
    public static final String REPORT_REASON_FAKE = "fake";
    public static final String REPORT_REASON_WRONGAGE = "wrong_age";
    public static final String REPORT_REASON_ABSURD = "absurd_profile";
    public static final String REPORT_REASON_DUPE = "duplicate";
    public static final String REPORT_REASON_ADVERTISING = "advertising";
    public static final String REPORT_REASON_POLITICALEXTREMIST = "political_extreme";
    public static final String REPORT_REASON_XRATED = "x_rated";
    public static final String REPORT_REASON_COPYRIGHT = "copyright_infringement";
    public static final String REPORT_REASON_OTHER = "other";

    public static final int REPORT_TEXT_MAX_LENGTH = 5000;

    public static final String ERROR_INVALID_REASON = "invalid_reason";
    public static final String ERROR_TEXTTOOLONG = "texttoolong";
    public static final String ERROR_INVALIDUSER = "invalid_user";
    public static final String ERROR_TOOMANY = "toomany";

}
