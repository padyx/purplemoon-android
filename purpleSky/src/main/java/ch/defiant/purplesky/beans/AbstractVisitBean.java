package ch.defiant.purplesky.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

public abstract class AbstractVisitBean implements Serializable {

    private static final long serialVersionUID = 6658150826613633799L;

    private MinimalUser m_user;
    private String m_profileId;
    private TreeMap<Date, Boolean> m_visits;

    public String getProfileId() {
        return m_profileId;
    }

    public void setProfileId(String profileId) {
        m_profileId = profileId;
    }

    public TreeMap<Date, Boolean> getVisits() {
        return m_visits;
    }

    public void setVisits(TreeMap<Date, Boolean> visits) {
        m_visits = visits;
    }

    public MinimalUser getUser() {
        return m_user;
    }

    public void setUser(MinimalUser user) {
        m_user = user;
    }

}
