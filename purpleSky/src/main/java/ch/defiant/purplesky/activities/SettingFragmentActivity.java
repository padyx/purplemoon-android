package ch.defiant.purplesky.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.broadcast.BroadcastTypes;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.db.IDatabaseProvider;
import ch.defiant.purplesky.gcm.GcmRegisterTask;
import ch.defiant.purplesky.util.DateUtility;

/**
 * @author Patrick Bänziger
 */
public class SettingFragmentActivity extends BaseFragmentActivity {

    private String TAG = SettingFragmentActivity.class.getSimpleName();

    private GcmRegisterTask m_asyncTask = null;
    private PreferenceUpdateListener m_listener;
    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;
    @Inject
    protected IDatabaseProvider m_databaseProvider;

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
                        removeAllDataAndLogout();

                        startActivity(new Intent(SettingFragmentActivity.this, AboutActivity.class));
                        return true;
                    }
                }
        );
    }

    private void removeAllDataAndLogout() {
        // Async task: unregister
        // Stop pending task first
        GcmRegisterTask existingTask = GcmRegisterTask.INSTANCE.get();
        if(existingTask != null && !existingTask.isCancelled()){
            existingTask.cancel(true);
        }
        GcmRegisterTask task = new GcmRegisterTask(apiAdapter);
        GcmRegisterTask.INSTANCE.compareAndSet(null, task);
        task.execute(false); // deregister

        try {
            task.get(1, TimeUnit.SECONDS);
            Log.i(TAG, "Unregistering task completed");
        } catch (InterruptedException e) {
            if(Log.isLoggable(TAG, Log.DEBUG)){
                Log.d(TAG, "Interrupted while waiting for unregistering on logout", e);
            }
        } catch (ExecutionException e) {
            Log.w(TAG, "While unregistering on logout, an error occured", e);
        } catch (TimeoutException e) {
            if(Log.isLoggable(TAG, Log.DEBUG)){
                Log.d(TAG, "Unregister operation took longer than expected");
            }
        }
        PersistantModel.getInstance().clearCredentials();
        // Clear all preferences - need to do it synchronously!
        PreferenceUtility.getPreferences().edit().clear().commit();

        m_databaseProvider.truncateAllTables();

        // Delegates the task to the main activity
        startActivity(new Intent(this, LoginActivity.class));
        finish();
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
