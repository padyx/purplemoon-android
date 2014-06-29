package ch.defiant.purplesky.beans;

import android.location.Address;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import ch.defiant.purplesky.enums.PurplemoonLocationType;
import ch.defiant.purplesky.util.StringUtility;

/**
 * Bean that describes a location from a Purplemoon profile
 * @author Patrick Bäziger
 * @since 1.1.0
 */
public class PurplemoonLocation {

    private final PurplemoonLocationType locationType;
    private String locationName;
    private String countryCode;
    private String streetAddress;
    private double latitude;
    private double longitude;

    public PurplemoonLocation(PurplemoonLocationType type, Location location, Address address) {
        this.locationType = type;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        if (address != null){
            countryCode = address.getCountryCode();

            // Exact street location
            List<String> list = new ArrayList<String>();
            for(int i=0; i<address.getMaxAddressLineIndex(); i++){
                list.add(address.getAddressLine(i));
            }
            streetAddress = StringUtility.join(", ", list);

            // Location name
            this.locationName = streetAddress;
        }
    }

    public PurplemoonLocation(PurplemoonLocationType type, String name, String countryCode,
                              String streetAddress, double longitude, double latitude){
        this.locationType = type;
        this.countryCode = countryCode;
        this.locationName = name;
        this.streetAddress = streetAddress;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public PurplemoonLocationType getLocationType() {
        return locationType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getLocationName() {
        return locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
