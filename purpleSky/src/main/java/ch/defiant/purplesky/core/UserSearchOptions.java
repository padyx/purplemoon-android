package ch.defiant.purplesky.core;

import java.io.Serializable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.util.Pair;
import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1.UserSearchOrder;
import ch.defiant.purplesky.enums.Gender;
import ch.defiant.purplesky.enums.Sexuality;

public class UserSearchOptions implements Serializable {

    private static final long serialVersionUID = 571458108330584270L;

    public static enum SearchType {
        FRIENDS(PurplemoonAPIConstantsV1.USERSEARCH_TYPE_FRIENDS),
        PARTNER(PurplemoonAPIConstantsV1.USERSEARCH_TYPE_PARTNER);

        private final String m_apiValue;

        private SearchType(String apiValue) {
            m_apiValue = apiValue;
        }

        public String getAPIValue() {
            return m_apiValue;
        }

    }
    private SearchType m_searchType;

    private String m_userName;
    @Deprecated
    private List<String> m_genderSexualities;
    private List<android.util.Pair<Gender, Sexuality>> attractions;
    private Integer m_minAge;
    private Integer m_maxAge;
    private String m_countryId;
    private boolean m_showOnlyOnline;
    private UserSearchOrder m_searchOrder;
    private Pair<Double, Double> m_location;
    private Integer m_number;
    private Class<? extends MinimalUser> m_userClass;
    private boolean m_needsOnlineStatus;
    private Integer m_maxDistance;
    public String getUserName() {
        return m_userName;
    }

    public void setUserName(String userName) {
        m_userName = userName;
    }

    public SearchType getSearchType() {
        return m_searchType;
    }

    public void setSearchType(SearchType searchType) {
        m_searchType = searchType;
    }

    public Integer getMinAge() {
        return m_minAge;
    }

    public void setMinAge(Integer minAge) {
        m_minAge = minAge;
    }

    public Integer getMaxAge() {
        return m_maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        m_maxAge = maxAge;
    }

    public String getCountryId() {
        return m_countryId;
    }

    public void setCountryId(String countryId) {
        m_countryId = countryId;
    }

    public boolean isShowOnlyOnline() {
        return m_showOnlyOnline;
    }

    public void setShowOnlyOnline(boolean showOnlyOnline) {
        m_showOnlyOnline = showOnlyOnline;
    }

    /**
     * Creates a search object from the parameters set.
     *
     * @return JSON Object
     * @throws JSONException
     */
    // TODO pbn This should not be implemented here, but in the concrete adapter
    public JSONObject createSearchObject() throws JSONException {
        JSONObject object = new JSONObject();

        if (getGenderSexualities() != null && !getGenderSexualities().isEmpty()) {
            JSONArray arr = new JSONArray();
            for (String pair : getGenderSexualities()) {
                if (pair == null) {
                    continue;
                }
                arr.put(pair);
            }
            object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_GENDER_SEXUALITY, arr);
        }

        if(getAttractions() != null) {
            object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_GENDER_SEXUALITY, createGenderSexualityOptions());
        }

        if (getMinAge() != null) {
            object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_AGEMIN, getMinAge());
        }
        if (getMaxAge() != null) {
            object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_AGEMAX, getMaxAge());
        }
        if (getCountryId() != null) {
            object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_COUNTRY, getCountryId());
        }
        if (isShowOnlyOnline()) {
            object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_ONLINE_ONLY, isShowOnlyOnline());
        }
        if (getMaxDistance() != null) {
            object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_DISTANCE_KM, getMaxDistance());
        }

        return object;
    }

    private JSONArray createGenderSexualityOptions() { // DUmmy method
        return null;
    }

    @Deprecated
    public List<String> getGenderSexualities() {
        return m_genderSexualities;
    }

    @Deprecated
    public void setGenderSexualities(List<String> sexualities) {
        m_genderSexualities = sexualities;
    }

    public UserSearchOrder getSearchOrder() {
        return m_searchOrder;
    }

    public void setSearchOrder(UserSearchOrder searchOrder) {
        m_searchOrder = searchOrder;
    }

    /**
     * Returns the location as a pair (Latitude, Longitude)
     */
    public Pair<Double, Double> getLocation() {
        return m_location;
    }

    /**
     * Set the location as a pair (Latitude, Longitude)
     *
     * @param location
     */
    public void setLocation(Pair<Double, Double> location) {
        m_location = location;
    }

    public Integer getNumber() {
        return m_number;
    }

    /**
     * Set the maximum number of results that shall be returned.
     *
     * @param number
     */
    public void setNumber(Integer number) {
        m_number = number;
    }

    public Class<? extends MinimalUser> getUserClass() {
        return m_userClass;
    }

    public void setUserClass(Class<? extends MinimalUser> userClass) {
        m_userClass = userClass;
    }

    public boolean isNeedsOnlineStatus() {
        return m_needsOnlineStatus;
    }

    public void setNeedsOnlineStatus(boolean needsOnlineStatus) {
        m_needsOnlineStatus = needsOnlineStatus;
    }

    public Integer getMaxDistance() {
        return m_maxDistance;
    }

    public void setMaxDistance(Integer maxDistance) {
        m_maxDistance = maxDistance;
    }

    public List<android.util.Pair<Gender, Sexuality>> getAttractions() {
        return attractions;
    }

    public void setAttractions(List<android.util.Pair<Gender, Sexuality>> attractions) {
        this.attractions = attractions;
    }

}
