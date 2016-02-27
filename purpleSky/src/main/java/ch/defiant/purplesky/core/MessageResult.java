package ch.defiant.purplesky.core;

import java.io.Serializable;
import java.util.List;

import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessage;

public class MessageResult implements Serializable {

    private static final long serialVersionUID = 360939643515847199L;

    private List<IPrivateMessage> m_unreadMessages;
    private IPrivateMessage m_sentMessage;

    public List<IPrivateMessage> getUnreadMessages() {
        return m_unreadMessages;
    }

    public MessageResult setUnreadMessages(List<IPrivateMessage> unreadMessages) {
        m_unreadMessages = unreadMessages;
        return this;
    }

    public IPrivateMessage getSentMessage() {
        return m_sentMessage;
    }

    public MessageResult setSentMessage(IPrivateMessage sentMessage) {
        m_sentMessage = sentMessage;
        return this;
    }

}
