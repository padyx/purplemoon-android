package ch.defiant.purplesky.beans;

import java.util.Date;

import ch.defiant.purplesky.enums.MessageStatus;
import ch.defiant.purplesky.enums.MessageType;

/**
 * @author Patrick Bänziger
 */
public interface IPrivateMessage {
    // TODO pbn remove?

    String getMessageText();

    String getRecipientId();
    void setRecipientId(String recipientId);

    String getSenderId();
    void setSenderId(String senderId);

    Date getTimeSent();

    MessageStatus getStatus();
    void setStatus(MessageStatus status);

    Long getInternalId();
    void setInternalId(Long id);

    Long getMessageId();
    void setMessageId(Long messageId);

    int getRetryCount();
    void setRetryCount(int retryCount);

    Date getNextSendAttempt();
    void setNextSendAttempt(Date nextAttempt);

    MessageType getMessageType();
    void setMessageType(MessageType type);

}
