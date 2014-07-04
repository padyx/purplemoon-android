package ch.defiant.purplesky.loaders.radar;

import android.content.Context;
import android.location.Address;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.beans.PurplemoonLocation;
import ch.defiant.purplesky.enums.PurplemoonLocationType;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;

/**
 * Retrieves the current location from the purplemoon profile of the user.
 * @author Patrick Bänziger
 * @since 1.1.0
 */
public class GetCurrentPurplemoonAddress extends SimpleAsyncLoader<Address> {

    private final IPurplemoonAPIAdapter apiAdapter;

    public GetCurrentPurplemoonAddress(Context context, IPurplemoonAPIAdapter adapter) {
        super(context, R.id.loader_profilePositionRetrieval);

        this.apiAdapter = adapter;
    }

    @Override
    public Address loadInBackground() {
        try {
            Collection<PurplemoonLocation> locations = apiAdapter.getOwnLocations();
            Address location = null;
            if(locations != null){
                location=getCurrentLocation(locations);
            }
            return location;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (PurpleSkyException e) {
            e.printStackTrace();
            return  null;
        }
    }

    private Address getCurrentLocation(Collection<PurplemoonLocation> locations) {
        for (PurplemoonLocation loc : locations){
            if(loc != null && loc.getLocationType() == PurplemoonLocationType.CURRENT){
                return toAddress(loc);
            }
        }
        return null;
    }

    private Address toAddress(PurplemoonLocation loc) {
        Address address = new Address(Locale.getDefault());
        address.setLatitude(loc.getLatitude());
        address.setLongitude(loc.getLongitude());
        address.setAddressLine(0, loc.getStreetAddress());
        address.setCountryCode(loc.getCountryCode());
        address.setFeatureName(loc.getLocationName());
        return address;
    }

}
