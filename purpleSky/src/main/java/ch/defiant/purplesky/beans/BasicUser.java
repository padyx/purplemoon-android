package ch.defiant.purplesky.beans;

import java.util.Date;
import java.util.Map;

public class BasicUser extends MinimalUser {

    private static final long serialVersionUID = -8010097654662341688L;
    private static final long EXPIRATION_BASIC = 1 * 60 * 60 * 1000;

    private String m_notes;
    private Boolean m_isKnown;
    private Boolean m_isFriend;
    private Boolean m_hasNotes;
    private Date m_onlineSince;
    private Map<String, ProfileTriplet> m_profileDetails;

    public String getNotes() {
        return m_notes;
    }

    public void setNotes(String notes) {
        m_notes = notes;
    }

    public Boolean isKnown() {
        return m_isKnown;
    }

    public void setKnown(Boolean isKnown) {
        m_isKnown = isKnown;
    }

    public Boolean isFriend() {
        return m_isFriend;
    }

    public void setFriend(Boolean isFriend) {
        m_isFriend = isFriend;
    }

    public Boolean hasNotes() {
        return m_hasNotes;
    }

    public void setHasNotes(Boolean hasNotes) {
        m_hasNotes = hasNotes;
    }

    public Date getOnlineSince() {
        return m_onlineSince;
    }

    public void setOnlineSince(Date onlineSince) {
        m_onlineSince = onlineSince;
    }

    /**
     * Get the profile details. This getter (and it's setter) implementation is protected, as in the basic user it is not provided. Subclass may make
     * this public if it provides these details.
     * 
     * @return List of profile triplets
     */
    protected Map<String, ProfileTriplet> getProfileDetails() {
        return m_profileDetails;
    }

    /**
     * Set the profile details. This setter (and it's getter) implementation is protected, as in the basic user it is not provided. Subclass may make
     * this public if it provides these details.
     * 
     * @param profileDetails
     *            Map of profile triplets (Key: ApiValue)
     */
    protected void setProfileDetails(Map<String, ProfileTriplet> profileDetails) {
        m_profileDetails = profileDetails;
    }

    @Override
    public long getExpiryDuration() {
        return EXPIRATION_BASIC;
    }

}
