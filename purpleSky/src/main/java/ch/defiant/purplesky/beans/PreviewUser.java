package ch.defiant.purplesky.beans;

import java.util.Map;

import ch.defiant.purplesky.enums.EyeColor;

public class PreviewUser extends BasicUser {

    private static final long serialVersionUID = 4349542817587099440L;

    private String m_firstOccupationType;
    private LocationBean m_homeLocation;
    private LocationBean m_home2Location;
    private LocationBean m_currentLocation;
    private Integer m_height;
    private Integer m_weight;

    private String m_physique; // FIXME Enum
    private EyeColor m_eyeColor; // FIXME Enum
    private String m_hairColor; // FIXME Enum
    private String m_hairLength; // FIXME Enum
    private String m_facialHair;  // FIXME Enum
    private String m_drinker;  // FIXME Enum
    private String m_smoker;  // FIXME Enum
    private String m_vegetarian;  // FIXME Enum
    private String m_wantsKids;  // FIXME Enum
    private String m_hasKids;  // FIXME Enum
    private String m_religion;  // FIXME Enum
    private String m_politics;  // FIXME Enum

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

    public Integer getWeight() {
        return m_weight;
    }

    public void setWeight(Integer weight) {
        m_weight = weight;
    }

    public Integer getHeight() {
        return m_height;
    }

    public void setHeight(Integer height) {
        m_height = height;
    }


    public String getPhysique() {
        return m_physique;
    }

    public void setPhysique(String physique) {
        m_physique = physique;
    }

    public String getHairColor() {
        return m_hairColor;
    }

    public void setHairColor(String hairColor) {
        m_hairColor = hairColor;
    }

    public EyeColor getEyeColor() {
        return m_eyeColor;
    }

    public void setEyeColor(EyeColor eyeColor) {
        m_eyeColor = eyeColor;
    }

    public String getHairLength() {
        return m_hairLength;
    }

    public void setHairLength(String hairLength) {
        m_hairLength = hairLength;
    }

    public String getFacialHair() {
        return m_facialHair;
    }

    public void setFacialHair(String facialHair) {
        m_facialHair = facialHair;
    }

}
