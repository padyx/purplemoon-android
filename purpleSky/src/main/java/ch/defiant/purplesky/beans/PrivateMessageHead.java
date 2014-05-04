package ch.defiant.purplesky.beans;

import java.io.Serializable;
import java.util.Date;

import ch.defiant.purplesky.enums.MessageType;

public class PrivateMessageHead implements Serializable {

    private static final long serialVersionUID = -2959854240469107950L;

    public static final String TYPEIDENTIFIER = "ch.defiant.purplesky.beans.PrivateMessageHead";

    private MessageType m_messageType;
    private MinimalUser m_author;
    private String m_authorProfileId;
    private Date m_timeSent;
    private MinimalUser m_recipient;
    private String m_recipientProfileId;
    private long m_messageId;
    private String m_replyToMessageId;
    private boolean m_unopened;

    public PrivateMessageHead() {
    };

    public PrivateMessageHead(MinimalUser u, Date timesent) {
        m_author = u;
        m_timeSent = timesent;
    }

    public void setAuthor(MinimalUser author) {
        m_author = author;
    }

    public MinimalUser getAuthor() {
        return m_author;
    }

    public String getAuthorProfileId() {
        return m_authorProfileId;
    }

    public void setAuthorProfileId(String authorProfileId) {
        m_authorProfileId = authorProfileId;
    }

    public void setTimeSent(Date timeSent) {
        m_timeSent = timeSent;
    }

    public Date getTimeSent() {
        return m_timeSent;
    }

    public void setRecipient(MinimalUser recipient) {
        m_recipient = recipient;
    }

    public MinimalUser getRecipient() {
        return m_recipient;
    }

    public String getRecipientProfileId() {
        return m_recipientProfileId;
    }

    public void setRecipientProfileId(String recipientProfileId) {
        m_recipientProfileId = recipientProfileId;
    }

    public void setMessageId(long messageId) {
        m_messageId = messageId;
    }

    public long getMessageId() {
        return m_messageId;
    }

    /**
     * If this message should be used to reply to a message, its id should be put here
     * 
     * @param replyToMessageId
     *            Id of the message to reply to
     */
    public void setReplyToMessageId(String replyToMessageId) {
        m_replyToMessageId = replyToMessageId;
    }

    public String getReplyToMessageId() {
        return m_replyToMessageId;
    }

    public MessageType getMessageType() {
        return m_messageType;
    }

    public void setMessageType(MessageType messageType) {
        m_messageType = messageType;
    }

    public boolean isUnopened() {
        return m_unopened;
    }

    public void setUnopened(boolean unopened) {
        m_unopened = unopened;
    }

}
