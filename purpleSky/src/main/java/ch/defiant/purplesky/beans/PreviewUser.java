package ch.defiant.purplesky.beans;

import java.util.Map;

import ch.defiant.purplesky.enums.profile.DrinkerFrequency;
import ch.defiant.purplesky.enums.profile.EyeColor;
import ch.defiant.purplesky.enums.profile.FacialHair;
import ch.defiant.purplesky.enums.profile.HairColor;
import ch.defiant.purplesky.enums.profile.HairLength;
import ch.defiant.purplesky.enums.profile.HasKids;
import ch.defiant.purplesky.enums.profile.Physique;
import ch.defiant.purplesky.enums.profile.Politics;
import ch.defiant.purplesky.enums.profile.Religion;
import ch.defiant.purplesky.enums.profile.SmokerFrequency;
import ch.defiant.purplesky.enums.profile.Vegetarian;
import ch.defiant.purplesky.enums.profile.WantsKids;

public class PreviewUser extends BasicUser {

    private static final long serialVersionUID = 4349542817587099440L;

    private String m_firstOccupationType;
    private LocationBean m_homeLocation;
    private LocationBean m_home2Location;
    private LocationBean m_currentLocation;
    private Integer m_height;
    private Integer m_weight;

    private Physique m_physique;
    private EyeColor m_eyeColor;
    private HairColor m_hairColor;
    private HairLength m_hairLength;
    private FacialHair m_facialHair;
    private DrinkerFrequency m_drinkerfrequency;
    private SmokerFrequency m_smokerFrequency;
    private Vegetarian m_vegetarian;
    private WantsKids m_wantsKids;
    private HasKids m_hasKids;
    private Religion m_religion;
    private Politics m_politics;

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


    public Physique getPhysique() {
        return m_physique;
    }

    public void setPhysique(Physique physique) {
        m_physique = physique;
    }

    public HairColor getHairColor() {
        return m_hairColor;
    }

    public void setHairColor(HairColor hairColor) {
        m_hairColor = hairColor;
    }

    public EyeColor getEyeColor() {
        return m_eyeColor;
    }

    public void setEyeColor(EyeColor eyeColor) {
        m_eyeColor = eyeColor;
    }

    public HairLength getHairLength() {
        return m_hairLength;
    }

    public void setHairLength(HairLength hairLength) {
        m_hairLength = hairLength;
    }

    public FacialHair getFacialHair() {
        return m_facialHair;
    }

    public void setFacialHair(FacialHair facialHair) {
        m_facialHair = facialHair;
    }

    public void setDrinkerFrequency(DrinkerFrequency drinkerFrequency){
        m_drinkerfrequency = drinkerFrequency;
    }

    public DrinkerFrequency getDrinkerFrequency(){
        return m_drinkerfrequency;
    }

    public SmokerFrequency getSmokerFrequency(){
        return m_smokerFrequency;
    }

    public void setSmokerFrequency(SmokerFrequency smokerFrequency){
        m_smokerFrequency = smokerFrequency;
    }

    public Vegetarian getVegetarian(){
        return m_vegetarian;
    }

    public void setVegetarian(Vegetarian vegetarian){
        m_vegetarian = vegetarian;
    }

    public void setWantsKids(WantsKids wantsKids){
        m_wantsKids = wantsKids;
    }

    public WantsKids getWantsKids(){
        return m_wantsKids;
    }

    public void setHasKids(HasKids wantsKids){
        m_hasKids = wantsKids;
    }

    public HasKids getHasKids(){
        return m_hasKids;
    }

    public Religion getReligion(){
        return m_religion;
    }

    public void setReligion(Religion religion){
        m_religion = religion;
    }

    public Politics getPolitics() {
        return m_politics;
    }

    public void setPolitics(Politics politics) {
        m_politics = politics;
    }
}
