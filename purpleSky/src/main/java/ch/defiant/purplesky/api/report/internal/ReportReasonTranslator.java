package ch.defiant.purplesky.api.report.internal;

import ch.defiant.purplesky.api.common.ITranslator;
import ch.defiant.purplesky.enums.UserReportReason;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public class ReportReasonTranslator implements ITranslator<UserReportReason, String> {

    public String translate(UserReportReason reason) {
        switch (reason) {
            case INSULT_THREAT:
                return ReportAPIConstants.REPORT_REASON_INSULT_THREAT;
            case FAKE:
                return ReportAPIConstants.REPORT_REASON_FAKE;
            case WRONGAGE:
                return ReportAPIConstants.REPORT_REASON_WRONGAGE;
            case ABSURD:
                return ReportAPIConstants.REPORT_REASON_ABSURD;
            case DUPE:
                return ReportAPIConstants.REPORT_REASON_DUPE;
            case ADVERTISING:
                return ReportAPIConstants.REPORT_REASON_ADVERTISING;
            case POLITICALEXTREMIST:
                return ReportAPIConstants.REPORT_REASON_POLITICALEXTREMIST;
            case XRATED:
                return ReportAPIConstants.REPORT_REASON_XRATED;
            case COPYRIGHT:
                return ReportAPIConstants.REPORT_REASON_COPYRIGHT;
            case OTHER:
                return ReportAPIConstants.REPORT_REASON_OTHER;
            default:
                throw new IllegalArgumentException("No api value for " + reason);
        }
    }
}
