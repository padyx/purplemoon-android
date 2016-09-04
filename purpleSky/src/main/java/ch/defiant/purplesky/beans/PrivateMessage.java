package ch.defiant.purplesky.beans;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

import ch.defiant.purplesky.enums.MessageStatus;
import ch.defiant.purplesky.enums.MessageType;

/**
 * Bean representing a full message, storing all related information
 * 
 * @author Patrick Bänziger
 * 
 */
public class PrivateMessage implements Serializable, IPrivateMessage {

    private static final long serialVersionUID = -7577615162687828508L;

    public static final String TYPEIDENTIFIER = "ch.defiant.purplesky.beans.PrivateMessage";

    private Long m_internalId;
    private Long m_messageId;
    private String m_messageText;
    private PrivateMessageHead m_messageHead = new PrivateMessageHead();
    private MessageStatus m_status;
    private int m_retryCount;
    private Date m_nextSendAttempt;

    @Nullable
    public Date getNextSendAttempt() {
        return m_nextSendAttempt;
    }

    public void setNextSendAttempt(@Nullable  Date nextSendAttempt) {
        m_nextSendAttempt = nextSendAttempt;
    }

    public int getRetryCount() {
        return m_retryCount;
    }

    public void setRetryCount(int retryCount) {
        m_retryCount = retryCount;
    }

    public void setMessageText(String messageText) {
        m_messageText = messageText;
    }

    public String getMessageText() {
        return m_messageText;
    }

    @Override
    public String getRecipientId() {
        return getMessageHead().getRecipientProfileId();
    }

    @Override
    public void setRecipientId(String recipientId) {
        getMessageHead().setRecipientProfileId(recipientId);
    }

    @Override
    public String getSenderId() {
        return getMessageHead().getAuthorProfileId();
    }

    public void setSenderId(String senderId){
        getMessageHead().setAuthorProfileId(senderId);
    }

    /**
     * Convenience method. Delegates to {@link PrivateMessageHead#getTimeSent()}
     */
    public Date getTimeSent() {
        if (getMessageHead() != null) {
            return getMessageHead().getTimeSent();
        } else {
            return null;
        }
    }

    @Override
    public MessageStatus getStatus() {
        return m_status;
    }

    @Override
    public void setStatus(MessageStatus status) {
        m_status = status;
    }

    public PrivateMessageHead getMessageHead() {
        return m_messageHead;
    }

    @Override
    public Long getInternalId() {
        return m_internalId;
    }

    @Override
    public void setInternalId(Long id) {
        m_internalId = id;
    }

    @Override
    public Long getMessageId() {
        return m_messageId;
    }

    @Override
    public void setMessageId(Long messageId) {
        m_messageId = messageId;
    }

    public void setTimeSent(Date timeSent) {
        getMessageHead().setTimeSent(timeSent);
    }

    @Override
    public void setMessageType(MessageType type){
        getMessageHead().setMessageType(type);
    }

    @Override
    public MessageType getMessageType() {
        return getMessageHead().getMessageType();
    }

}
