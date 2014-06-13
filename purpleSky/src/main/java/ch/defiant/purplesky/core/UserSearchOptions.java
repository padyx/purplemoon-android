package ch.defiant.purplesky.core;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
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
import ch.defiant.purplesky.util.BundleUtil;
import ch.defiant.purplesky.util.EnumUtility;
import ch.defiant.purplesky.util.StringUtility;

public class UserSearchOptions implements Serializable {

    private static final long serialVersionUID = 571458108330584270L;
    private static final String TAG = UserSearchOptions.class.getSimpleName();

    public LastOnline getLastOnline() {
        return m_lastOnline;
    }

    public void setLastOnline(LastOnline m_lastOnline) {
        this.m_lastOnline = m_lastOnline;
    }

    public static enum SearchType { FRIENDS, PARTNER  }
    public static enum LastOnline { NOW, RECENTLY, PAST_DAY, PAST_WEEK, PAST_MONTH}

    private SearchType m_searchType;
    private String m_userName;
    @Deprecated
    private List<String> m_genderSexualities;
    private List<Pair<Gender, Sexuality>> attractions;
    private Integer m_minAge;
    private Integer m_maxAge;
    private String m_countryId;
    private LastOnline m_lastOnline;
    private UserSearchOrder m_searchOrder;
    private Pair<Double, Double> m_location;
    private Integer m_number;
    private Class<? extends MinimalUser> m_userClass;
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

    public Integer getMaxDistance() {
        return m_maxDistance;
    }

    public void setMaxDistance(Integer maxDistance) {
        m_maxDistance = maxDistance;
    }

    public List<Pair<Gender, Sexuality>> getAttractions() {
        return attractions;
    }

    public void setAttractions(List<Pair<Gender, Sexuality>> attractions) {
        this.attractions = attractions;
    }

    // TODO pbn Write unit test

    private static final String PREFIX = "UserSearchOptions.";

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString(PREFIX+"username", getUserName());
        b.putString(PREFIX+"searchtype", EnumUtility.getName(getSearchType()));
        b.putString(PREFIX+"lastOnline", EnumUtility.getName(getLastOnline()));
        BundleUtil.safePut(b, PREFIX+"minAge", getMinAge());
        BundleUtil.safePut(b,PREFIX+"maxAge", getMaxAge());
        b.putString(PREFIX+"country", getCountryId());
        b.putString(PREFIX+"searchOrder", EnumUtility.getName(getSearchOrder()));
        if(getLocation() != null){
            b.putDouble(PREFIX+"locationN", getLocation().getFirst());
            b.putDouble(PREFIX+"locationE", getLocation().getSecond());
        }
        BundleUtil.safePut(b, PREFIX+"number", getNumber());
        BundleUtil.safePut(b, PREFIX+"maxDistance", getMaxDistance());

        // Attractions -> Store as G,S;G,S;...
        if(getAttractions() != null){
            StringBuilder sb = new StringBuilder();
            for (Pair<Gender, Sexuality> p: getAttractions()){
                sb.append(p.getFirst().name());
                sb.append(",");
                sb.append(p.getSecond().name());
                sb.append(";");
            }
            b.putString(PREFIX+"attractions", sb.toString());
        }

        return b;
    }

    /**
     * Restores the options from the bundle.<br/>
     * <b>Note:</b> User class (getUserClass) is not restored, but set to MinimalUser instead
     *
     * @param bundle
     * @return Created object - never <tt>null</tt>
     */
    public static UserSearchOptions from(Bundle bundle){
        UserSearchOptions options = new UserSearchOptions();
        options.setUserName(bundle.getString(PREFIX + "username"));
        options.setSearchType(EnumUtility.fromName(bundle.getString(PREFIX + "searchType"), SearchType.class));
        options.setLastOnline(EnumUtility.fromName(bundle.getString(PREFIX + "lastOnline"), LastOnline.class));
        options.setMinAge(BundleUtil.getIntWithNull(bundle, PREFIX + "minAge"));
        options.setMaxAge(BundleUtil.getIntWithNull(bundle, PREFIX + "maxAge"));
        options.setCountryId(BundleUtil.getStringWithNull(bundle, PREFIX + "country"));
        if(bundle.containsKey(PREFIX+"locationN") && bundle.containsKey(PREFIX+"locationE")){
            options.setLocation(new Pair<Double, Double>(bundle.getDouble(PREFIX+"locationN"), bundle.getDouble(PREFIX+"locationE")));
        }
        options.setNumber(BundleUtil.getIntWithNull(bundle, PREFIX+"number"));
        options.setMaxDistance(BundleUtil.getIntWithNull(bundle, PREFIX+"maxDistance"));

        String attractions = BundleUtil.getStringWithNull(bundle, PREFIX+"attractions");
        if(attractions != null){
            List<Pair<Gender, Sexuality>> pairs = new ArrayList<Pair<Gender, Sexuality>>();
            String[] attr = attractions.split(";");
            for (String a : attr){
                if(StringUtility.hasText(a)){
                    String[] pair = a.split(",");
                    if(pair.length != 2){
                        Log.e(TAG, "Could not restore user search options from bundle. " +
                                "Length was "+pair.length);
                        continue;
                    }
                    Gender gender = EnumUtility.fromName(pair[0], Gender.class);
                    Sexuality sexuality = EnumUtility.fromName(pair[1], Sexuality.class);
                    if(gender != null && sexuality != null){
                        pairs.add(new Pair<Gender, Sexuality>(gender, sexuality));
                    }
                }
            }
            if(!pairs.isEmpty()){
                options.setAttractions(pairs);
            }
        }

        return options;
    }
}
