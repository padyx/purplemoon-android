package ch.defiant.purplesky.beans;

import java.io.Serializable;
import java.util.Date;

public class UserMessageHistoryBean implements Serializable {
    private static final long serialVersionUID = -8781002868355826215L;

    private String m_profileId;
    private String m_cachedUsername;
    private String m_cachedProfilePictureUrl;
    private int m_unopenedMessageCount;
    private Date m_lastContact;
    private MinimalUser m_userBean;
    private boolean m_hasMessages;
    private Date m_otherUserLastRead;
    private Date m_lastSent;
    private Date m_lastReceived;
    private String m_LastMessageExcerpt;

    public String getProfileId() {
        return m_profileId;
    }

    public void setProfileId(String profileId) {
        m_profileId = profileId;
    }

    public int getUnopenedMessageCount() {
        return m_unopenedMessageCount;
    }

    public void setUnopenedMessageCount(int unopenedMessageCount) {
        m_unopenedMessageCount = unopenedMessageCount;
    }

    public Date getLastContact() {
        return m_lastContact;
    }

    public void setLastContact(Date lastContact) {
        m_lastContact = lastContact;
    }

    public MinimalUser getUserBean() {
        return m_userBean;
    }

    public void setUserBean(MinimalUser userBean) {
        m_userBean = userBean;
    }

    public boolean hasMessages() {
        return m_hasMessages;
    }

    public void setHasMessages(boolean hasMessages) {
        m_hasMessages = hasMessages;
    }

    public Date getOtherUserLastRead() {
        return m_otherUserLastRead;
    }

    public void setOtherUserLastRead(Date otherUserLastRead) {
        this.m_otherUserLastRead = otherUserLastRead;
    }

    public String getLastMessageExcerpt() {
        return m_LastMessageExcerpt;
    }

    public void setLastMessageExcerpt(String lastMessageExcerpt) {
        m_LastMessageExcerpt = lastMessageExcerpt;
    }

    public Date getLastSent() {
        return m_lastSent;
    }

    public void setLastSent(Date lastSent) {
        m_lastSent = lastSent;
    }

    public Date getLastReceived() {
        return m_lastReceived;
    }

    public void setLastReceived(Date lastReceived) {
        m_lastReceived = lastReceived;
    }

    public String getCachedUsername() {
        return m_cachedUsername;
    }

    public void setCachedUsername(String cachedUsername) {
        m_cachedUsername = cachedUsername;
    }

    @Override
    public String toString() {
        return "UserMessageHistoryBean [m_profileId=" + m_profileId + ", m_cachedUsername=" + m_cachedUsername
                + ", m_unopenedMessageCount=" + m_unopenedMessageCount + ", m_lastContact=" + m_lastContact
                + ", m_userBean=" + m_userBean + ", m_hasMessages=" + m_hasMessages + ", m_otherUserLastRead="
                + m_otherUserLastRead + ", m_lastSent=" + m_lastSent + ", m_lastReceived=" + m_lastReceived
                + ", m_LastMessageExcerpt='" + m_LastMessageExcerpt + "']";
    }

    public String getCachedProfilePictureUrl() {
        return m_cachedProfilePictureUrl;
    }

    public void setCachedProfilePictureUrl(String cachedProfilePictureUrl) {
        m_cachedProfilePictureUrl = cachedProfilePictureUrl;
    }
}
