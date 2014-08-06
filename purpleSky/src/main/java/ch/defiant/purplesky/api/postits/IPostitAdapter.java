package ch.defiant.purplesky.api.postits;

import android.util.Pair;

import java.io.IOException;
import java.util.List;

import ch.defiant.purplesky.beans.PostIt;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.exceptions.WrongCredentialsException;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public interface IPostitAdapter {

    /**
     * Get received postits.
     *
     * @param options
     *            Supports the options 'start', 'number' and 'sinceTimestamp'
     * @return List of postits
     * @throws java.io.IOException
     * @throws ch.defiant.purplesky.exceptions.PurpleSkyException
     */
    public List<PostIt> getReceivedPostIts(AdapterOptions options) throws IOException, PurpleSkyException;

    /**
     * Get received postits.
     *
     * @param options
     *            Supports the options 'start', 'number' and 'sinceTimestamp'
     * @return List of postits
     * @throws IOException
     * @throws PurpleSkyException
     */
    public List<PostIt> getGivenPostIts(AdapterOptions options) throws IOException, PurpleSkyException;

    /**
     * Retrieve the possible postits for the user
     *
     * @param profileId User to give a postit
     * @return List of pairs (postit-id, postit-description)
     * @throws IOException
     * @throws PurpleSkyException
     */
    public List<Pair<Integer, String>> getPostitOptions(String profileId) throws IOException,
            PurpleSkyException;

    /**
     * Create a postit for the specified user, either with a specified postit type or the custom text.
     * @param profileId id of the user to give to postit to
     * @param postitValue predefined postit id, or <tt>null</tt>
     * @param postitCustomText custom postit text, or <tt>null</tt>
     * @return whether the postit creation was successful
     * @throws IOException
     * @throws WrongCredentialsException
     * @throws PurpleSkyException
     */
    public boolean createPostit(String profileId, Integer postitValue, String postitCustomText)
            throws IOException, WrongCredentialsException,
            PurpleSkyException;

}
