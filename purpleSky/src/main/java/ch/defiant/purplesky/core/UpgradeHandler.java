package ch.defiant.purplesky.core;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.StringUtility;
import ch.defiant.purplesky.util.SystemUtility;

/**
 * Handles the application upgrade process
 * @author Patrick Bänziger
 *
 */
public final class UpgradeHandler {

    public static final String TAG = UpgradeHandler.class.getSimpleName();

    private UpgradeHandler() { }

    public static boolean needsUpgrade(Context c){
        return SystemUtility.isApplicationUpdatedOrInstalled(c);
    }

    /**
     * Perform all necessary upgrade actions. Long-running, blocking task!
     * @param c
     * @param c
     * @return If the application tried to perform upgrade actions
     */
    public static boolean performUpgradeActions(Context c){
        boolean needsUpgrade = needsUpgrade(c);
        if(needsUpgrade){
            boolean success = upgrade(c);
            if(success){
                // Make sure, next time we won't do this again unless updated
                SystemUtility.updateStoredApplicationVersion();
            }
        }
        return needsUpgrade;
    }


    /**
     * Performs all upgrade actions.
     * @param c 
     * @param c 
     * @return Whether all upgrade actions succeeded. If not, it should be retried later.
     */
    private static boolean upgrade(Context c){
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
     * Unregisters old push notification id from server, then clears it.
     * Re-registering is NOT performed here.
     * @return If unregistering succeeded.
     */
    private static boolean upgradeNotifications() {
        SharedPreferences prefs = PreferenceUtility.getPreferences();
        String gcmStored = prefs.getString(PreferenceConstants.gcmToken, null);
        if(StringUtility.isNotNullOrEmpty(gcmStored)){
            try{
                boolean unregisterPush = PurplemoonAPIAdapter.getInstance().unregisterPush(gcmStored);
                if(!unregisterPush){
                    Log.w(TAG, "Unregistering push failed. Aborting upgrade action");
                    return false;
                } else {
                    Log.i(TAG, "Unregistering for push succeeded.");
                }
            } catch(IOException e){
                return false;
            } catch (PurpleSkyException e) {
                Log.w(TAG, "Encountered exception when unregistering from push messages!",e);
                return false;
            }
            // Delete from store
            prefs.edit().remove(PreferenceConstants.gcmToken).commit();
        }
        return true;
    }

}
