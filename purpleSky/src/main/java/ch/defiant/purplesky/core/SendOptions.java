package ch.defiant.purplesky.core;

import java.util.Date;

public class SendOptions {

    private UnreadHandling m_unreadHandling;
    private Date m_latestRead;

    public enum UnreadHandling {
        SEND, ABORT
    }

    public UnreadHandling getUnreadHandling() {
        return m_unreadHandling;
    }

    public void setUnreadHandling(UnreadHandling unreadHandling) {
        m_unreadHandling = unreadHandling;
    }

    public Date getLatestRead() {
        return m_latestRead;
    }

    public void setLatestRead(Date date) {
        m_latestRead = date;
    }

}
