package ch.defiant.purplesky.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.constants.SecureConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.UpgradeHandler;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.StringUtility;

/**
 * This loader is called regardless of whether an update is required or not!
 * @author Patrick Bänziger
 *
 */
public class UpgradeAndPushLoader extends SimpleAsyncLoader<Object> {

    private static final String TAG = UpgradeAndPushLoader.class.getSimpleName();
    private final IPurplemoonAPIAdapter apiAdapter;

    public UpgradeAndPushLoader(Context context, IPurplemoonAPIAdapter apiAdapter) {
        super(context, R.id.loader_main_upgradePush);
        this.apiAdapter = apiAdapter;
    }

    @Override
    public Void loadInBackground() {
        // Perform upgrade actions
        new UpgradeHandler(apiAdapter).performUpgradeActions(getContext());

        checkEventNotificationConfiguration();

        return null;
    }

    private void checkEventNotificationConfiguration() {
        if(BuildConfig.DEBUG){
            Log.d(TAG, "Checking Google Cloud Messaging Status");
        }
        SharedPreferences prefs = PreferenceUtility.getPreferences();
        if(prefs.getBoolean(PreferenceConstants.updateEnabled, true)){
            int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
            if (result == ConnectionResult.SUCCESS) {
                Log.i(TAG, "Google Cloud Messaging available.");
                doCGMRegister(prefs);
            } else {
                Log.i(TAG, "Google Cloud Messaging NOT available.");
                // UpdateService is started by StartupBroadcast Receiver
            }
        }
    }

    private void doCGMRegister(SharedPreferences prefs) {
        // If we still have the GCM Id, all is well (no update)
        String gcmToken = prefs.getString(PreferenceConstants.gcmToken, null);
        if(StringUtility.isNotNullOrEmpty(gcmToken)){
            return;
        }

        // Otherwise, we need to get a new one
        try{
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getContext());
            String regId = gcm.register(SecureConstants.get("gcm.id"));

            if(StringUtility.isNullOrEmpty(regId)){
                Log.w(TAG, "Setup of Google Cloud Messaging failed: No registratioId received");
                return;
            }

            boolean registered = apiAdapter.registerPush(regId);
            if(registered){
                prefs.edit().putString(PreferenceConstants.gcmToken, regId).apply();
                Log.i(TAG, "Register for push with server: Success");
            } else {
                Log.w(TAG, "Register for push with server: Failed");
            }
        }catch(IOException ioe){
            Log.i(TAG, "Registering for Google cLoud Messaging failed: IOException", ioe);
        } catch (PurpleSkyException e) {
            Log.w(TAG, "Registering for Google Clous Messaging failed with unexpected error", e);
        }
    }

}
