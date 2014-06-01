package ch.defiant.purplesky.util;

import android.content.res.Resources;
import android.location.Location;
import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.LocationBean;
import ch.defiant.purplesky.core.PurpleSkyApplication;

public class LocationUtility {

    /**
     * Translates the ISO country code to the localized country name.
     */
    public static String getCountryNameByIsoId(String iso) {
        if (StringUtility.isNullOrEmpty(iso)) {
            return null;
        }

        String[] ids = PurpleSkyApplication.get().getResources().getStringArray(R.array.countryIds);
        String[] names = PurpleSkyApplication.get().getResources().getStringArray(R.array.countryNames);
        if (BuildConfig.DEBUG) {
            assert ids.length == names.length : "Country ids must have same length as country names";
        }
        int pos = 0;
        for (String c : ids) {
            if (iso.equalsIgnoreCase(c)) {
                return names[pos];
            }
            pos++;
        }
        return null;
    }

    public static String getApproximateDistanceString(LocationBean one, LocationBean two) {
        if (one == null || two == null) {
            throw new NullPointerException("Locations may not be null");
        }
        float[] res = new float[1]; // Result is in meters
        Location.distanceBetween(one.getLatitude(), one.getLongitude(), two.getLatitude(), two.getLongitude(), res);

        final Resources resrc = PurpleSkyApplication.get().getResources();

        float dist = res[0];

        if (dist < 500.0) {
            return resrc.getString(R.string.DistanceLessThan500m);
        } else if (dist < 1000.0) { // <1km
            return resrc.getString(R.string.DistanceLessThan1km);
        } else { // Use rounded distance (no decimal digits)
            int intdist = (int) Math.round(dist / 1000.0);
            return resrc.getString(R.string.DistanceApproxX, intdist);
        }
    }

    public static LocationBean translateLocationToLocationBean(Location l) {
        LocationBean bean = new LocationBean();
        bean.setLatitude(l.getLatitude());
        bean.setLongitude(l.getLongitude());
        return bean;
    }
}
