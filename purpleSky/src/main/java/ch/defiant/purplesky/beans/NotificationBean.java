package ch.defiant.purplesky.beans;

import java.util.Date;

public class NotificationBean {

    private Date m_lastStatusUpdate;
    private Date m_lastMessagesUpdate;
    private Date m_lastFavoritesUpdate;
    private Date m_lastMessageReceived;
    private Date m_lastGeneralNewsUpdate;

    public Date getLastStatusUpdate() {
        return m_lastStatusUpdate;
    }

    public void setLastStatusUpdate(Date lastStatusUpdate) {
        m_lastStatusUpdate = lastStatusUpdate;
    }

    public Date getLastMessagesUpdate() {
        return m_lastMessagesUpdate;
    }

    public void setLastMessagesUpdate(Date lastMessagesUpdate) {
        m_lastMessagesUpdate = lastMessagesUpdate;
    }

    public Date getLastFavoritesUpdate() {
        return m_lastFavoritesUpdate;
    }

    public void setLastFavoritesUpdate(Date lastFavoritesUpdate) {
        m_lastFavoritesUpdate = lastFavoritesUpdate;
    }

    /**
     * @return Date when the last message was received for this user.
     */
    public Date getLastMessageReceived() {
        return m_lastMessageReceived;
    }

    public void setLastMessageReceived(Date lastMessageReceived) {
        m_lastMessageReceived = lastMessageReceived;
    }

    /**
     * Indicates when the {@link AlertBean} was last updated.
     * 
     * @return
     */
    public Date getLastGeneralNewsUpdate() {
        return m_lastGeneralNewsUpdate;
    }

    public void setLastGeneralNewsUpdate(Date lastGeneralNewsUpdate) {
        m_lastGeneralNewsUpdate = lastGeneralNewsUpdate;
    }

}
