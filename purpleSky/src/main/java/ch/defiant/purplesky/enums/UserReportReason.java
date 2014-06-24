package ch.defiant.purplesky.enums;

import javax.xml.transform.URIResolver;

import ch.defiant.purplesky.R;

/**
 * Reasons to report a use for.
 *
 * @author Patrick Bänziger
 * @since 1.1.0
 */
public enum UserReportReason {
    INSULT_THREAT (R.string.ReportReason_InsultThreat),
    FAKE(R.string.ReportReason_Fake),
    WRONGAGE(R.string.ReportReason_WrongAge),
    ABSURD(R.string.ReportReason_AbsurdProfile),
    DUPE(R.string.ReportReason_Duplicate),
    ADVERTISING(R.string.ReportReason_Advertising),
    POLITICALEXTREMIS(R.string.ReportReason_PoliticalExtremist),
    XRATED(R.string.ReportReason_XRated),
    COPYRIGHT(R.string.ReportReason_CopyrightViolation),
    OTHER(R.string.ReportReason_Other);

    private final int stringRes;

    private UserReportReason (int stringRes){
        this.stringRes = stringRes;
    }

    public int getStringRes() {
        return stringRes;
    }
}
