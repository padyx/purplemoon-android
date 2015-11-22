package ch.defiant.purplesky.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.beans.PushStatus;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.constants.SecureConstants;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.StringUtility;
import ch.defiant.purplesky.util.SystemUtility;

/**
 * Handles the application upgrade process
 * @author Patrick Bänziger
 *
 */
public final class UpgradeHandler {

    public static final String TAG = UpgradeHandler.class.getSimpleName();

    private final IPurplemoonAPIAdapter apiAdapter;

    public UpgradeHandler(IPurplemoonAPIAdapter adapter) {
        this.apiAdapter = adapter;
    }

    public boolean needsUpgrade(Context c){
        return SystemUtility.isApplicationUpdatedOrInstalled(c);
    }

    /**
     * Perform all necessary upgrade actions. Long-running, blocking task!
     * @param c
     * @param c
     * @return If the application tried to perform upgrade actions
     */
    public boolean performUpgradeActions(Context c){
        boolean needsUpgrade = needsUpgrade(c);
        if(needsUpgrade){
            boolean success = upgrade();
            if(success){
                // Make sure, next time we won't do this again unless updated
                SystemUtility.updateStoredApplicationVersion();
            }
        }
        return needsUpgrade;
    }


    /**
     * Performs all upgrade actions.
     * @return Whether all upgrade actions succeeded. If not, it should be retried later.
     */
    private boolean upgrade(){
        boolean success = true;
        Log.i(TAG, "Beginning upgrade actions");
        // BEGIN Upgrades

        success = success && upgradeNotifications();

        // END   Upgrades

        if(success){
            Log.i(TAG, "Upgrade actions finished successfully.");
        } else {
            Log.w(TAG, "Upgrade actions did not (all) succeed. Will retry on next launch.");
        }
        return success;
    }

    /**
     * Unregisters old push notification id from server, obtains a new one, and registers it
     * @return If re-registering succeeded.
     */
    private boolean upgradeNotifications() {
        SharedPreferences prefs = PreferenceUtility.getPreferences();
        String gcmStored = prefs.getString(PreferenceConstants.gcmToken, null);
        if(StringUtility.isNotNullOrEmpty(gcmStored)){
            if (unregisterPush(prefs, gcmStored)){
                return false;
            }
        }

        return doCGMRegister(prefs);
    }

    private boolean unregisterPush(SharedPreferences prefs, String gcmStored) {
        try{
            boolean unregisterPush = apiAdapter.unregisterPush(gcmStored);
            if(!unregisterPush){
                Log.w(TAG, "Unregistering push failed. Aborting upgrade action");
                return true;
            } else {
                Log.i(TAG, "Unregistering for push succeeded.");
            }
        } catch(IOException e){
            return true;
        } catch (PurpleSkyException e) {
            Log.w(TAG, "Encountered exception when unregistering from push messages!",e);
            return true;
        }
        // Delete from store
        prefs.edit().remove(PreferenceConstants.gcmToken).apply();
        return false;
    }

    private boolean doCGMRegister(SharedPreferences prefs) {
        try{
            String gcmToken = GoogleCloudMessaging.getInstance(PurpleSkyApplication.get()).
                    register(SecureConstants.get(SecureConstants.GCM_ID));

            if(StringUtility.isNullOrEmpty(gcmToken)){
                Log.w(TAG, "Setup of Google Cloud Messaging failed: No registratioId received");
                return false;
            }

            PushStatus pushStatus = apiAdapter.getPushStatus();

            // If we still have the GCM Id - and it matches the registered one, no update is needed
            if(pushStatus != null
                    && pushStatus.isEnabled()
                    && CompareUtility.equals(gcmToken, pushStatus.getDeviceToken())){
                return true;
            }

            boolean registered = apiAdapter.registerPush(gcmToken);
            if(registered){
                prefs.edit().
                        putString(PreferenceConstants.gcmToken, gcmToken).
                        putLong(PreferenceConstants.lastPushRegistrationAttempt, System.currentTimeMillis()).
                        apply();
                Log.i(TAG, "Register for push with server: Success");
            } else {
                Log.w(TAG, "Register for push with server: Failed");
            }
            return true;
        }catch(IOException ioe){
            Log.i(TAG, "Registering for Google cLoud Messaging failed: IOException", ioe);
        } catch (PurpleSkyException e) {
            Log.w(TAG, "Registering for Google Clous Messaging failed with unexpected error", e);
        }
        return false;
    }

}
