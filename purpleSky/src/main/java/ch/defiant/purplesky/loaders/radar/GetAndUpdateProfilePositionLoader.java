package ch.defiant.purplesky.loaders.radar;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.beans.PurplemoonLocation;
import ch.defiant.purplesky.enums.PurplemoonLocationType;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.CollectionUtil;

/**
 * Loader that retrieves the current newLocation from the profile and updates it, if necessary.
 * Returns an {@link android.location.Address} as a result describing the updated location.

 * @author Patrick Bänziger
 * @since 1.1.0
 */
public class GetAndUpdateProfilePositionLoader extends SimpleAsyncLoader<Address> {

    private static final int ACCURACY_BOUND_METERS = 500;
    private static String TAG = GetAndUpdateProfilePositionLoader.class.getSimpleName();

    private final IPurplemoonAPIAdapter apiAdapter;
    private final Location newLocation;

    public GetAndUpdateProfilePositionLoader(Context context, IPurplemoonAPIAdapter apiAdapter, Location location) {
        super(context, R.id.loader_profilePositionUpdate);
        this.apiAdapter = apiAdapter;
        this.newLocation = location;
    }

    @Override
    public Address loadInBackground() {
        try {
            PurplemoonLocation location;
            Collection<PurplemoonLocation> locations = apiAdapter.getOwnLocations();
            location = getCurrentLocation(locations);
            Address address = null;
            if(newLocation != null) {
                address = getAddressFromLocation(getContext(), newLocation);
                if (location != null) {
                    if(requiresUpdate(newLocation, location)){
                        updateLocation(newLocation, address, locations);
                    }
                }  else {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "No current location found in api. Sending location");
                    }
                    updateLocation(newLocation, address, locations);
                }
            }
            return address;
            // TODO Handle errors
        } catch (IOException e) {
            return null;
        } catch (PurpleSkyException e) {
            return null;
        }
    }

    private static Address getAddressFromLocation(Context c, Location newLocation) throws IOException {
        List<Address> address = new Geocoder(c)
                .getFromLocation(newLocation.getLatitude(), newLocation.getLongitude(), 1);
        if(address == null || address.isEmpty()){
            if(BuildConfig.DEBUG){
                Log.d(TAG, "No address returned from geocoder");
            }
        }
        return CollectionUtil.firstElement(address);
    }

    private void updateLocation(Location newLocation, Address address, Collection<PurplemoonLocation> locations) throws IOException, PurpleSkyException {
        // TODO Recognize if close to home/work locations and set the location to that

        PurplemoonLocation location = new PurplemoonLocation(PurplemoonLocationType.CURRENT, newLocation, address);
        apiAdapter.setOwnLocation(location);
    }

    private static boolean requiresUpdate(Location newLocation, PurplemoonLocation location) {
        float[] results = new float[1];
        Location.distanceBetween(
                newLocation.getLatitude(), newLocation.getLongitude(),
                location.getLatitude(), location.getLongitude(),
                results);

        return results[0] > ACCURACY_BOUND_METERS;
    }

    private PurplemoonLocation getCurrentLocation(Collection<PurplemoonLocation> locations) {
        if(locations == null){
            return null;
        }
        for (PurplemoonLocation l: locations){
            if(l.getLocationType() == PurplemoonLocationType.CURRENT){
                return l;
            }
        }
        return null;
    }
}
