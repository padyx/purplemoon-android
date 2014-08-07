package ch.defiant.purplesky.api.report.internal;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import ch.defiant.purplesky.api.common.APINetworkUtility;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.api.report.IReportAdapter;
import ch.defiant.purplesky.api.report.ReportResponse;
import ch.defiant.purplesky.enums.UserReportReason;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.HTTPURLResponseHolder;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class ReportAdapter implements IReportAdapter {

    public static final ReportAdapter INSTANCE = new ReportAdapter();

    @Override
    public ReportResponse reportUser(String profileId, UserReportReason reason, String description) throws IOException, PurpleSkyException {
        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(ReportAPIConstants.REPORT_URL);
        sb.append(profileId);

        BasicNameValuePair param1 = new BasicNameValuePair(ReportAPIConstants.REPORT_REASON_PARAM, new ReportReasonTranslator().translate(reason));
        BasicNameValuePair param2 = new BasicNameValuePair(ReportAPIConstants.REPORT_DESCRIPTION_PARAM, description);

        List<NameValuePair> args = Arrays.<NameValuePair>asList(param1, param2);

        HTTPURLResponseHolder response = APINetworkUtility.postForResponseHolderNoThrow(new URL(sb.toString()), args, null);
        if (response.isSuccessful()) {
            return ReportResponse.OK;
        } else {
            return new ReportResponseTranslator().translate(response.getError());
        }
    }
}
