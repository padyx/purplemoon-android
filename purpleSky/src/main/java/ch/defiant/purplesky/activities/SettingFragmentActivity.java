package ch.defiant.purplesky.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Date;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.broadcast.BroadcastTypes;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.gcm.GcmRegisterTask;
import ch.defiant.purplesky.util.DateUtility;

/**
 * @author Patrick Bänziger
 */
public class SettingFragmentActivity extends BaseFragmentActivity {

    private GcmRegisterTask m_asyncTask = null;
    private PreferenceUpdateListener m_listener;
    protected IPurplemoonAPIAdapter apiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        setActionBarTitle(getString(R.string.Settings), null);

        m_listener = new PreferenceUpdateListener();
        PreferenceUtility.getPreferences().registerOnSharedPreferenceChangeListener(m_listener);

        PreferenceFragment fragment = (PreferenceFragment) getFragmentManager().findFragmentById(R.id.fragment);
        createPreferences(fragment);
        attachListeners(fragment);
    }

    @Override
    public int getSelfNavigationIndex() {
        return NavigationDrawerEntries.LAUNCH_SETTINGS.ordinal();
    }

    private void createPreferences(PreferenceFragment fragment) {

        Preference userStatus = fragment.findPreference("ch.defiant.purplesky.preferences.poweruserStatus");
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

    private void attachListeners(PreferenceFragment fragment) {
        Preference clearAccount = fragment.findPreference("ch.defiant.purplesky.preferences.clearCredentials");
        clearAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                removeAllDataAndLogout();
                return true;
            }
        });

        fragment.findPreference("ch.defiant.purplesky.preferences.about").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(new Intent(SettingFragmentActivity.this, AboutActivity.class));
                        return true;
                    }
                }
        );
    }

    private void removeAllDataAndLogout() {
        // Delegates the task to the main activity
        finish();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BroadcastTypes.BROADCAST_LOGOUT));
    }

    private class PreferenceUpdateListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (PreferenceConstants.updateEnabled.equals(key)) {
                boolean enabled = sharedPreferences.getBoolean(PreferenceConstants.updateEnabled, false);

                // With backoff?
                if (m_asyncTask != null && !m_asyncTask.isCancelled()) {
                    m_asyncTask.cancel(true);
                }
                m_asyncTask = new GcmRegisterTask(apiAdapter);
                m_asyncTask.execute(enabled);
            }
        }
    }
}
