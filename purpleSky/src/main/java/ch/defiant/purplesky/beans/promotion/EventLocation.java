package ch.defiant.purplesky.beans.promotion;

import android.net.Uri;

/**
 * @author Patrick Bänziger
 */
public class EventLocation {

    private int m_locationId;
    private String m_countryCode;
    private String m_regionCode;
    private String m_village;
    private String m_locationName;
    private String m_address;
    private double m_latitude;
    private double m_longitude;
    private Uri m_website;

    public int getLocationId() {
        return m_locationId;
    }

    public void setLocationId(int locationId) {
        m_locationId = locationId;
    }

    public String getCountryCode() {
        return m_countryCode;
    }

    public void setCountryCode(String countryCode) {
        m_countryCode = countryCode;
    }

    public String getRegionCode() {
        return m_regionCode;
    }

    public void setRegionCode(String regionCode) {
        m_regionCode = regionCode;
    }

    public String getVillage() {
        return m_village;
    }

    public void setVillage(String village) {
        m_village = village;
    }

    public String getLocationName() {
        return m_locationName;
    }

    public void setLocationName(String locationName) {
        m_locationName = locationName;
    }

    public String getAddress() {
        return m_address;
    }

    public void setAddress(String address) {
        m_address = address;
    }

    public double getLatitude() {
        return m_latitude;
    }

    public void setLatitude(double latitude) {
        m_latitude = latitude;
    }

    public double getLongitude() {
        return m_longitude;
    }

    public void setLongitude(double longitude) {
        m_longitude = longitude;
    }

    public Uri getWebsite() {
        return m_website;
    }

    public void setWebsite(Uri website) {
        m_website = website;
    }
}
