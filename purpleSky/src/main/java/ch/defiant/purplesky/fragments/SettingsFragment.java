package ch.defiant.purplesky.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.Date;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.util.DateUtility;

/**
 * @author Patrick Bänziger
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        setup();
    }

    private void setup() {
        Preference userStatus = findPreference("ch.defiant.purplesky.preferences.poweruserStatus");
        long expiry = PreferenceUtility.getPreferences().getLong(PreferenceConstants.powerUserExpiry, 0);
        Date expDate = new Date(expiry);
        if (expiry == 0 || new Date().after(expDate)) {
            userStatus.setSummary(R.string.preference_poweruserNotActivated);
        } else {
            userStatus.setSummary(getString(R.string.preference_poweruserExpiresOnX, DateUtility.getMediumDateString(expDate)));
        }
    }
}
