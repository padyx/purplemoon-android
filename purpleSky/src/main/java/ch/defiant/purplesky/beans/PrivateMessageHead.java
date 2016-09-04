package ch.defiant.purplesky.beans;

import java.io.Serializable;
import java.util.Date;

import ch.defiant.purplesky.enums.MessageType;

public class PrivateMessageHead implements Serializable {

    private static final long serialVersionUID = -2959854240469107950L;

    public static final String TYPEIDENTIFIER = "ch.defiant.purplesky.beans.PrivateMessageHead";

    private MessageType m_messageType;
    private String m_authorProfileId;
    private Date m_timeSent;
    private String m_recipientProfileId;
    private long m_messageId;

    public PrivateMessageHead() {
    };

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

    public MessageType getMessageType() {
        return m_messageType;
    }

    public void setMessageType(MessageType messageType) {
        m_messageType = messageType;
    }

}
