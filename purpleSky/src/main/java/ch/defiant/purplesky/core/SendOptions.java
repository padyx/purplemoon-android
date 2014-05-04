package ch.defiant.purplesky.core;

import java.util.Date;

import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1;

public class SendOptions {

    private UnreadHandling m_unreadHandling;
    private Date m_latestRead;

    public enum UnreadHandling {
        SEND(PurplemoonAPIConstantsV1.MESSAGE_SEND_UNREAD_HANDLING_SEND),
        ABORT(PurplemoonAPIConstantsV1.MESSAGE_SEND_UNREAD_HANDLING_ABORT);

        private final String m_apivalue;

        private UnreadHandling(String apivalue) {
            m_apivalue = apivalue;
        }

        public String getAPIValue() {
            return m_apivalue;
        }

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
