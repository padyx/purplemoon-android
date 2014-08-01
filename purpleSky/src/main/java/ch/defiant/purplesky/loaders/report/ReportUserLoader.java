package ch.defiant.purplesky.loaders.report;

import android.content.Context;

import java.io.IOException;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.report.IReportAdapter;
import ch.defiant.purplesky.api.report.ReportResponse;
import ch.defiant.purplesky.enums.UserReportReason;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;

/**
 * @author Patrick Bänziger
 */
public class ReportUserLoader extends SimpleAsyncLoader<Holder<ReportResponse>> {

    private final IReportAdapter reportAdapter;
    private final String userId;
    private final UserReportReason reason;
    private final String description;

    public ReportUserLoader(Context context, IReportAdapter reportAdapter, String userId, UserReportReason reason, String description) {
        super(context, R.id.loader_reportUser);
        this.reportAdapter = reportAdapter;
        this.userId = userId;
        this.reason = reason;
        this.description = description;
    }

    @Override
    public Holder<ReportResponse> loadInBackground() {
        try {
            return Holder.of(reportAdapter.reportUser(userId, reason, description));
        } catch (IOException e) {
            return new Holder<ReportResponse>(e);
        } catch (PurpleSkyException e) {
            return new Holder<ReportResponse>(e);
        }
    }
}
