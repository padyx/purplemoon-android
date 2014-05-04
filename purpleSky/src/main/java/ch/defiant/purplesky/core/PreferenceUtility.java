package ch.defiant.purplesky.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtility {

    public static SharedPreferences getPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(PurpleSkyApplication.getContext());
        return sharedPref;
    }
}
