package ch.defiant.purplesky.beans;

import java.util.Map;

public class PreviewUser extends BasicUser {

    private static final long serialVersionUID = 4349542817587099440L;

    private String m_firstOccupationType;
    private LocationBean m_homeLocation;
    private LocationBean m_home2Location;
    private LocationBean m_currentLocation;

    public String getFirstOccupationType() {
        return m_firstOccupationType;
    }

    public void setFirstOccupationType(String firstOccupationType) {
        m_firstOccupationType = firstOccupationType;
    }

    /**
     * Returns the profile details.
     * 
     * @return List of profile triplets
     */
    @Override
    public Map<String, ProfileTriplet> getProfileDetails() {
        return super.getProfileDetails();
    }

    /**
     * Set the profile details
     * 
     * @param profileDetails
     *            List of profile triplets
     */
    @Override
    public void setProfileDetails(Map<String, ProfileTriplet> profileDetails) {
        super.setProfileDetails(profileDetails);
    }

    public LocationBean getHomeLocation() {
        return m_homeLocation;
    }

    public void setHomeLocation(LocationBean homeLocation) {
        m_homeLocation = homeLocation;
    }

    public LocationBean getHome2Location() {
        return m_home2Location;
    }

    public void setHome2Location(LocationBean home2Location) {
        m_home2Location = home2Location;
    }

    public LocationBean getCurrentLocation() {
        return m_currentLocation;
    }

    public void setCurrentLocation(LocationBean currentLocation) {
        m_currentLocation = currentLocation;
    }

}
