package ch.defiant.purplesky.api.report;

import java.io.IOException;

import ch.defiant.purplesky.enums.UserReportReason;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

/**
 *
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public interface IReportAdapter {

    /**
     * Report the user.
     *
     * @param profileId
     * @param reason
     * @param description
     * @throws IOException
     * @throws PurpleSkyException
     */
    ReportResponse reportUser(String profileId, UserReportReason reason, String description) throws IOException, PurpleSkyException;

}