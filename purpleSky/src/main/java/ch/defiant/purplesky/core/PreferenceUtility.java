package ch.defiant.purplesky.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtility {

    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(PurpleSkyApplication.get());
    }

}
