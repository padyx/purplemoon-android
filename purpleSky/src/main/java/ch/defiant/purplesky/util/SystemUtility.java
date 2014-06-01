package ch.defiant.purplesky.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.PurpleSkyApplication;

public class SystemUtility {

    public static int getAppVersion(Context context) {
        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pkgInfo.versionCode;
        } catch (NameNotFoundException e) {
            // Should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    
    /**
     * @param c context
     * @return If the application was recently installed or updated.
     */
    public static boolean isApplicationUpdatedOrInstalled(Context c){
        SharedPreferences prefs = PreferenceUtility.getPreferences();
        
        int cachedVersion = prefs.getInt(PreferenceConstants.lastVersionInt, Integer.MIN_VALUE);
        int currentVersion = SystemUtility.getAppVersion(c);
        return cachedVersion < currentVersion;
    }
    
    public static void updateStoredApplicationVersion(){
        int appVersion = getAppVersion(PurpleSkyApplication.get());
        
        SharedPreferences prefs = PreferenceUtility.getPreferences();
        prefs.edit().putInt(PreferenceConstants.lastVersionInt, appVersion).commit();
    }

}
