package ch.defiant.purplesky.api.visit;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import ch.defiant.purplesky.beans.VisitsMadeBean;
import ch.defiant.purplesky.beans.VisitsReceivedBean;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public interface IVisitAdapter {

    /**
     * Retrieve the visits made by other users to the application user's profile.
     *
     * @param options
     *            Will handle the following options: Since_Timestamp, Start, Number.
     * @param overrideLastDateCheck
     *            Date to set the last check. Useful when loading more entries.
     * @return List of visits
     * @throws IOException
     * @throws PurpleSkyException
     */
    public List<VisitsReceivedBean> getReceivedVists(AdapterOptions options, Date overrideLastDateCheck)
            throws IOException, PurpleSkyException;

    /**
     * Retrieve the visits made by the application user.
     *
     * @param options
     *            Will handle the following options: Since_Timestamp, Start, Number.
     * @return List of visits
     * @throws IOException
     * @throws PurpleSkyException
     */
    public List<VisitsMadeBean> getOwnVists(AdapterOptions options) throws IOException, PurpleSkyException;
}
