package ch.defiant.purplesky.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.content.LocalBroadcastManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.broadcast.BroadcastTypes;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UpdateService;
import ch.defiant.purplesky.gcm.GcmRegisterTask;
import ch.defiant.purplesky.util.DateUtility;

/**
 * Setting activity 
 * @author Chakotay
 *
 */
// TODO Convert into preferenceFragment as soon as minVersion > 3.0
public class SettingActivity extends SherlockPreferenceActivity {

    private GcmRegisterTask m_asyncTask = null;

    private PreferenceUpdateListener m_listener;
    private boolean m_playServicesAvailable;

    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PurpleSkyApplication.get().inject(this);

        getSupportActionBar().setTitle(R.string.Settings);
        addPreferencesFromResource(R.xml.preferences);

        createPreferences();
        attachListeners();

        m_listener = new PreferenceUpdateListener();
        PreferenceUtility.getPreferences().registerOnSharedPreferenceChangeListener(m_listener);
    }
    
    
    
    private void createPreferences() {
        m_playServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
        if (m_playServicesAvailable) {
            Preference intervalPref = findPreference(PreferenceConstants.updateInterval);
            intervalPref.setEnabled(false);
            intervalPref.setSummary(R.string.PreferenceNotifyIntervalPush);
        }
        Preference userStatus = findPreference("ch.defiant.purplesky.preferences.poweruserStatus");
        long expiry = PreferenceUtility.getPreferences().getLong(PreferenceConstants.powerUserExpiry, 0);
        Date expDate = new Date(expiry);
        if (expiry == 0 || new Date().after(expDate)) {
            userStatus.setSummary(R.string.preference_poweruserNotActivated);
        } else {
            userStatus.setSummary(getString(R.string.preference_poweruserExpiresOnX, DateUtility.getMediumDateString(expDate)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceUtility.getPreferences().unregisterOnSharedPreferenceChangeListener(m_listener);
    }

    private void attachListeners() {
        Preference clearAccount = findPreference("ch.defiant.purplesky.preferences.clearCredentials");
        clearAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                removeAllDataAndLogout();
                return true;
            }
        });

        findPreference("ch.defiant.purplesky.preferences.about").setOnPreferenceClickListener(
                new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(new Intent(SettingActivity.this, AboutActivity.class));
                        return  true;
                    }
                }
        );

    }

    private void removeAllDataAndLogout() {        
        // Delegates the task to the main activity
        finish();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BroadcastTypes.BROADCAST_LOGOUT));
    }

    private class PreferenceUpdateListener implements OnSharedPreferenceChangeListener {

        private final Collection<String> prefKeys = Arrays.asList(PreferenceConstants.updateEnabled,
                PreferenceConstants.updateInterval);

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (prefKeys.contains(key)) {
                boolean enabled = sharedPreferences.getBoolean(PreferenceConstants.updateEnabled, false);

                if (m_playServicesAvailable) {
                    // With backoff?

                    if (m_asyncTask != null && !m_asyncTask.isCancelled()) {
                        m_asyncTask.cancel(true);
                    }
                    m_asyncTask = new GcmRegisterTask(apiAdapter);
                    m_asyncTask.execute(enabled);
                } else {
                    if (enabled) {
                        UpdateService.registerUpdateService();
                    } else {
                        UpdateService.unregisterUpdateService();
                    }
                }
            }
        }
    }

}
