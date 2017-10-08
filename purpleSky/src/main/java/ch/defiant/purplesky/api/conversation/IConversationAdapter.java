package ch.defiant.purplesky.api.conversation;

import java.io.IOException;
import java.util.List;

import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.core.SendOptions;
import ch.defiant.purplesky.enums.MessageRetrievalRestrictionType;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public interface IConversationAdapter {

    /**
     * Send a private message.
     *
     * @param message
     *            The message to send. Needs to have the recipient (either bean or profileId) and text set.
     * @param opts
     *            Send options. May not be null. Must have the unread handling set!
     * @return The private message, with all necessary data set
     * @throws java.io.IOException
     * @throws ch.defiant.purplesky.exceptions.WrongCredentialsException
     * @throws ch.defiant.purplesky.exceptions.PurpleSkyException
     */
    MessageResult sendMessage(PrivateMessage message, SendOptions opts) throws IOException,
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
     * @return List of Beans, recent contacts come first
     * @throws IOException
     * @throws PurpleSkyException
     */
    List<UserMessageHistoryBean> getRecentContacts(Integer resultCount, Integer startAt,
                                                          MessageRetrievalRestrictionType restrict) throws IOException, PurpleSkyException;

    /**
     * Retrieve the status of the conversation with the user.
     * @param profileId ProfileID of the user
     * @return Bean without excerpt
     * @throws IOException
     * @throws PurpleSkyException
     */
    UserMessageHistoryBean getConversationStatus(String profileId) throws IOException,
            PurpleSkyException;

    int getUnopenedMessagesCount() throws IOException, PurpleSkyException;

    /**
     * Retrieves the messages between the application user and another user.
     *
     * @param profileId
     *            The id of the other user
     * @param options
     *            Supports the {@link ch.defiant.purplesky.core.AdapterOptions#setUptoId(Long)}, {@link ch.defiant.purplesky.core.AdapterOptions#setSinceId(Long)},
     *            {@link ch.defiant.purplesky.core.AdapterOptions#setNumber(Integer)}
     * @return List of messages
     * @throws IOException
     * @throws PurpleSkyException
     */
    List<PrivateMessage> getRecentMessagesByUser(String profileId, AdapterOptions options)
            throws IOException, PurpleSkyException;

}
