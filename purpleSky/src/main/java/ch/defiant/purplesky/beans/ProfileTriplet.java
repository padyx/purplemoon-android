package ch.defiant.purplesky.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ProfileTriplet implements Serializable {

    private static final long serialVersionUID = -5078193385902785617L;

    private String m_APIKey;
    private String m_displayKey;
    private String m_displayValue;
    private Serializable m_rawValue;
    private Map<String, ProfileTriplet> m_children;
    private List<Map<String, ProfileTriplet>> m_list;
    private int order;

    /**
     * Constructor for a simple, non-nested property. You should either specify a displayvalue (localized string) or a raw value (serializable object,
     * most likely Integer, Boolean, String, Long or Double).
     * 
     * @param APIKey
     *            Key value from API.
     * @param displaykey
     *            Translated key for displaying.
     * @param displayvalue
     *            Translated value for displaying.
     * @param rawValue
     *            Raw value for this triplet.
     */
    public ProfileTriplet(String APIKey, String displaykey, String displayvalue, Serializable rawValue) {
        setAPIKey(APIKey);
        setDisplayKey(displaykey);
        setDisplayValue(displayvalue);
        setRawValue(rawValue);
    }

    /**
     * Constructor for a more complex, nested property.
     * 
     * @param APIKey
     *            Key value from API.
     * @param displaykey
     *            Translated key for displaying.
     * @param triplets
     *            Child triplets for this APIKey.
     */
    public ProfileTriplet(String APIKey, String displaykey, Map<String, ProfileTriplet> triplets) {
        m_APIKey = APIKey;
        m_displayKey = displaykey;
        setChildren(triplets);
    }

    /**
     * Constructor for a 'list' triplet.
     * 
     * @param triplets
     *            List elements
     */
    public ProfileTriplet(String apiKey, List<Map<String, ProfileTriplet>> triplets) {
        m_APIKey = apiKey;
        setList(triplets);
    }

    /**
     * Serialization constructor. Do not use!
     */
    public ProfileTriplet() {
    }

    public String getAPIKey() {
        return m_APIKey;
    }

    public void setAPIKey(String aPIKey) {
        m_APIKey = aPIKey;
    }

    public String getDisplayKey() {
        return m_displayKey;
    }

    public void setDisplayKey(String displayKey) {
        m_displayKey = displayKey;
    }

    /**
     * If this profile triplet is trivial (Key->Value), returns the translated value.
     * 
     * @return
     */
    public String getDisplayValue() {
        return m_displayValue;
    }

    public void setDisplayValue(String displayValue) {
        m_displayValue = displayValue;
    }

    /**
     * If this profile triplet is non-trivial, returns the child elements.
     * 
     * @return List of Profile Triplets
     */
    public Map<String, ProfileTriplet> getChildren() {
        return m_children;
    }

    /**
     * To create a nested profile property, use this method to add child-triplets.
     * 
     * @param triplets
     */
    public void setChildren(Map<String, ProfileTriplet> triplets) {
        m_children = triplets;
    }

    public boolean isList() {
        return getList() != null;
    }

    public boolean isNested() {
        return getChildren() != null || getList() != null;
    }

    public boolean isSimple() {
        return getChildren() == null && getList() == null;
    }

    /**
     * Raw value in the case where the value is not a translatable string. Best example: Height/Weight are likely integers.
     * 
     * @return Stored raw value
     */
    public Serializable getRawValue() {
        return m_rawValue;
    }

    public void setRawValue(Serializable rawValue) {
        m_rawValue = rawValue;
    }

    public int getOrder() {
        return order;
    }

    /**
     * Order to determine the
     * 
     * @param order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    public List<Map<String, ProfileTriplet>> getList() {
        return m_list;
    }

    public void setList(List<Map<String, ProfileTriplet>> list) {
        m_list = list;
    }
}
