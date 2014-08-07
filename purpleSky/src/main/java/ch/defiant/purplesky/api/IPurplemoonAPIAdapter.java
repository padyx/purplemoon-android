package ch.defiant.purplesky.api;

import android.util.Pair;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.defiant.purplesky.beans.AlertBean;
import ch.defiant.purplesky.beans.DetailedUser;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.NotificationBean;
import ch.defiant.purplesky.beans.OnlineBean;
import ch.defiant.purplesky.beans.PreviewUser;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.PurplemoonLocation;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.beans.VisitsMadeBean;
import ch.defiant.purplesky.beans.VisitsReceivedBean;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.core.SendOptions;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.enums.MessageRetrievalRestrictionType;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.exceptions.WrongCredentialsException;

/**
 * Interface for Purplemoon API Functions
 * @author Patrick Bänziger
 *
 */
public interface IPurplemoonAPIAdapter {

    /**
     * Convenience method for requesting just one user's minimal data
     * 
     * @param userid
     *            Id of user to request bean for
     * @param withOnlineStatus
     *            If the online status for the user is needed
     * @return Bean
     * @throws IOException
     *             If no connection can be established
     * @throws PurpleSkyException
     * @see #getMinimalUserData(List)
     */
    public MinimalUser getMinimalUserData(String userid, boolean withOnlineStatus) throws IOException,
            PurpleSkyException;

    public boolean doLogin(String username, String password) throws IOException, PurpleSkyException;

    /**
     * Method to request beans for multiple users with minimal data
     * 
     * @param userids
     *            List of profile Ids
     * @param withOnlineStatus
     *            If the users online status is neede
     * @return List of Beans
     * @throws IOException
     *             If no connection can be established
     * @throws PurpleSkyException
     */
    public Map<String, MinimalUser> getMinimalUserData(List<String> userids, boolean withOnlineStatus)
            throws IOException, PurpleSkyException;

    /**
     * Convenience method for requesting just one user's preview data
     * 
     * @param userid
     *            Id of user to request bean for
     * @param withOnlineStatus
     *            If the online status should be retrieved, too
     * @return Bean
     * @throws IOException
     *             If no connection can be established
     * @throws PurpleSkyException
     * @see #getMinimalUserData(List)
     */
    public PreviewUser getPreviewUserData(String userid, boolean withOnlineStatus) throws IOException,
            PurpleSkyException;

    /**
     * Retrieve a set of preview users.
     * 
     * @param userids
     *            List of profileIds
     * @param withOnlineStatus
     *            If the online status should be retrieved
     * @return Map, with key being the profileId of this user and the value being the requested bean.
     * @throws IOException
     * @throws PurpleSkyException
     */
    public Map<String, PreviewUser> getPreviewUserData(List<String> userids, boolean withOnlineStatus)
            throws IOException, PurpleSkyException;

    /**
     * Retrieve a set of detailed users. Note: The API does not offer a bulk method for detailed user data, neither does this apiAdapter. Bulk querying
     * for details is <em>strongly</em> discouraged.
     * 
     * @param userids
     *            List of profileIds
     * @return Map, with key being the profileId of this user and the value being the requested bean.
     * @throws IOException
     * @throws PurpleSkyException
     */
    public DetailedUser getDetailedUserData(String userid) throws IOException, PurpleSkyException;

    public DetailedUser getMyDetailedUserData() throws IOException, PurpleSkyException;

    public List<OnlineBean> getOnlineFavorites() throws IOException, PurpleSkyException;

    /**
     * Send a private message.
     * 
     * @param message
     *            The message to send. Needs to have the recipient (either bean or profileId) and text set.
     * @param opts
     *            Send options. May not be null. Must have the unread handling set!
     * @return The private message, with all necessary data set
     * @throws IOException
     * @throws WrongCredentialsException
     * @throws PurpleSkyException
     */
    public MessageResult sendMessage(PrivateMessage message, SendOptions opts) throws IOException,
            PurpleSkyException;

    /**
     * Returns beans indicating the recent contacts.
     * 
     * @param resultCount
     *            How many results to be returned. Uses API default, if null
     * @param startAt
     *            At which user to start (paging). Starts at first, if null
     * @param restrict
     *            Determine order of the retrieved beans. Default is {@link ch.defiant.purplesky.enums.MessageRetrievalRestrictionType#LAST_CONTACT}
     * @param since_date
     *            Restricts to contacts updated this date. Null: No restriction
     * @return List of Beans, recent contacts come first
     * @throws IOException
     * @throws PurpleSkyException
     */
    public List<UserMessageHistoryBean> getRecentContacts(Integer resultCount, Integer startAt,
            MessageRetrievalRestrictionType restrict) throws IOException, PurpleSkyException;

    /**
     * Retrieve the status of the conversation with the user.
     * @param profileId ProfileID of the user
     * @return Bean without excerpt
     * @throws IOException
     * @throws PurpleSkyException
     */
    public UserMessageHistoryBean getConversationStatus(String profileId) throws IOException,
            PurpleSkyException;

    public int getOnlineFavoritesCount() throws IOException, PurpleSkyException;

    public int getUnopenedMessagesCount() throws IOException, PurpleSkyException;

    /**
     * Retrieves the messages between the application user and another user.
     * 
     * @param profileId
     *            The id of the other user
     * @param options
     *            Supports the {@link ch.defiant.purplesky.core.AdapterOptions#setUptoId(String)}, {@link ch.defiant.purplesky.core.AdapterOptions#setSinceId(String)},
     *            {@link ch.defiant.purplesky.core.AdapterOptions#setNumber(Integer)}
     * @return
     * @throws IOException
     * @throws PurpleSkyException
     */
    public List<PrivateMessage> getRecentMessagesByUser(String profileId, AdapterOptions options)
            throws IOException, PurpleSkyException;

    public boolean isLoggedIn();



    public NotificationBean getNotificationBean() throws IOException, PurpleSkyException;

    /**
     * Retrieves
     * 
     * @param text
     *            Search string
     * @param options
     *            Search options
     * @return Search results
     * @throws IOException
     * @throws PurpleSkyException
     * @throws IllegalArgumentException
     *             If the class is not supported.
     */
    public List<MinimalUser> searchUserByName(String text, UserSearchOptions options) throws IOException,
            PurpleSkyException;

    /**
     * Set the online status of the user. Either use predefined or custom string (exactly one).
     * 
     * @param status
     *            Onlinestatus to set
     * @param custom
     *            Custom online status (optional). May be null
     * @throws IOException
     * @return Whether the change succeeded.
     * @throws PurpleSkyException
     */
    public boolean setOnlineStatus(OnlineStatus status, String custom) throws IOException, PurpleSkyException;

    /**
     * Get the online status.
     * 
     * @return A pair, of which at least one object is set.
     * @throws IOException
     * @throws PurpleSkyException
     */
    public Pair<OnlineStatus, String> getOwnOnlineStatus() throws IOException, PurpleSkyException;

    /**
     * Retrieve the online status for the users specified
     * 
     * @param profileIds
     *            List of profile ids
     * @return A map of profileIds to the status
     */
    public Map<String, Pair<OnlineStatus, String>> getOnlineStatus(List<String> profileIds)
            throws IOException, PurpleSkyException;

    public AlertBean getAlertBean() throws IOException, PurpleSkyException;

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

    public List<MinimalUser> searchUser(UserSearchOptions options) throws IOException, PurpleSkyException;

    /**
     * Checks if the user is a power user.
     * 
     * @return The expiry date for the status, or <tt>null</tt> if the user is not a power user.
     * @throws IOException
     * @throws PurpleSkyException
     */
    public Date getPowerUserExpiry() throws IOException, PurpleSkyException;

    /**
     * Register for push messages.
     * 
     * @param gcmRegId
     *            CGM Register
     * @return whether the action succeeded
     * @throws IOException
     * @throws PurpleSkyException
     */
    public boolean registerPush(String gcmRegId) throws IOException, PurpleSkyException;

    /**
     * Unregister from push messages.
     * 
     * @param gcmRegId
     * @return whether the action succeeded
     * @throws IOException
     * @throws PurpleSkyException
     */
    public boolean unregisterPush(String gcmRegId) throws IOException, PurpleSkyException;

    /**
     * Get all locations from the users profile
     * @return Locations of the user from his profile
     * @throws IOException
     * @throws PurpleSkyException
     */
    Collection<PurplemoonLocation> getOwnLocations() throws IOException, PurpleSkyException;

    /**
     * Update the user's profile with a new location.
     * @param location
     * @throws IOException
     * @throws PurpleSkyException
     */
    void setOwnLocation(PurplemoonLocation location) throws IOException, PurpleSkyException;
}