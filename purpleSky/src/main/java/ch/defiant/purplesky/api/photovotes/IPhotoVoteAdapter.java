package ch.defiant.purplesky.api.photovotes;

import java.io.IOException;
import java.util.List;

import ch.defiant.purplesky.beans.PhotoVoteBean;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public interface IPhotoVoteAdapter {

    /**
     * Returns the remaining photos to vote on. Only call at the beginning, then cache and decrement it!
     *
     * @return Number of photos the user can still vote on
     * @throws java.io.IOException
     * @throws ch.defiant.purplesky.exceptions.PurpleSkyException
     */
    public int getRemainingPhotoVotes() throws IOException, PurpleSkyException;

    /**
     * Retrieves the next bean that the user can vote on. If <code>bean</code> is non-null, then the verdict contained will be posted to the photo and
     * a new bean returned.
     *
     * @param bean
     * @return The next photovote bean to vote on
     * @throws IOException
     * @throws PurpleSkyException
     */
    public PhotoVoteBean getNextPhotoVoteAndVote(PhotoVoteBean bean) throws IOException, PurpleSkyException;

    public List<PhotoVoteBean> getReceivedVotes(AdapterOptions opts) throws IOException, PurpleSkyException;

    public List<PhotoVoteBean> getGivenVotes(AdapterOptions opts) throws IOException, PurpleSkyException;

}
