package ch.defiant.purplesky.beans;

import java.io.Serializable;

public class LocationBean implements Serializable {

    private static final long serialVersionUID = 4578512632591039880L;

    private Double m_latitude;
    private Double m_longitude;

    private String m_countryId;
    private String m_locationDescription;

    public LocationBean() {
    };

    public LocationBean(double longitude, double latitude, String countryId, String locationDesc) {
        setLongitude(longitude);
        setLatitude(latitude);
        setCountryId(countryId);
        setLocationDescription(locationDesc);
    }

    public Double getLatitude() {
        return m_latitude;
    }

    public void setLatitude(Double latitude) {
        m_latitude = latitude;
    }

    public Double getLongitude() {
        return m_longitude;
    }

    public void setLongitude(Double longitude) {
        m_longitude = longitude;
    }

    public String getCountryId() {
        return m_countryId;
    }

    public void setCountryId(String countryId) {
        m_countryId = countryId;
    }

    public String getLocationDescription() {
        return m_locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        m_locationDescription = locationDescription;
    }

}
