package ch.defiant.purplesky.beans;

import java.util.Date;

/**
 * @author Patrick Bänziger
 */
public class PushStatus {

    private boolean m_enabled;
    private String m_deviceToken;
    private Date m_lastPush;

    public boolean isEnabled() {
        return m_enabled;
    }

    public void setEnabled(boolean enabled) {
        m_enabled = enabled;
    }

    public String getDeviceToken() {
        return m_deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        m_deviceToken = deviceToken;
    }

    public Date getLastPush() {
        return m_lastPush;
    }

    public void setLastPush(Date lastPush) {
        m_lastPush = lastPush;
    }
}
