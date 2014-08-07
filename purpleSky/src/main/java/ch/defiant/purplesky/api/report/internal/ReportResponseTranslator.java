package ch.defiant.purplesky.api.report.internal;

import ch.defiant.purplesky.api.common.ITranslator;
import ch.defiant.purplesky.api.report.ReportResponse;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class ReportResponseTranslator implements ITranslator<String, ReportResponse> {

    @Override
    public ReportResponse translate(String source) {
        if(ReportAPIConstants.ERROR_INVALID_REASON.equals(source)){
            return ReportResponse.ERROR;
        } else if (ReportAPIConstants.ERROR_INVALIDUSER.equals(source)){
            return ReportResponse.INVALID_USER;
        } else if (ReportAPIConstants.ERROR_TEXTTOOLONG.equals(source)){
            return ReportResponse.TEXT_TOO_LONG;
        } else if (ReportAPIConstants.ERROR_TOOMANY.equals(source)){
            return ReportResponse.TOO_MANY;
        }
        return ReportResponse.ERROR;
    }
}
