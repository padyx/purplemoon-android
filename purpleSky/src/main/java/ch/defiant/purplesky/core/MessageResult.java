package ch.defiant.purplesky.core;

import java.io.Serializable;
import java.util.List;

import ch.defiant.purplesky.beans.PrivateMessage;

public class MessageResult implements Serializable {

    private static final long serialVersionUID = 360939643515847199L;

    private List<PrivateMessage> m_unreadMessages;
    private PrivateMessage m_sentMessage;

    public List<PrivateMessage> getUnreadMessages() {
        return m_unreadMessages;
    }

    public MessageResult setUnreadMessages(List<PrivateMessage> unreadMessages) {
        m_unreadMessages = unreadMessages;
        return this;
    }

    public PrivateMessage getSentMessage() {
        return m_sentMessage;
    }

    public MessageResult setSentMessage(PrivateMessage sentMessage) {
        m_sentMessage = sentMessage;
        return this;
    }

}
