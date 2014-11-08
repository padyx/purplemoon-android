package ch.defiant.purplesky.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.LoginActivity;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.util.StringUtility;

public class PersistantModel {

    private static final PersistantModel s_instance = new PersistantModel();
    private final SharedPreferences m_preferences = PreferenceUtility.getPreferences();

    private String m_userProfileId;

    private String m_oAuthAccessToken;

    private PersistantModel() {
        restoreCredentials();
    }

    private boolean restoreCredentials() {
        // Try to get token
        m_userProfileId = m_preferences.getString(PreferenceConstants.userprofileId, null);
        m_oAuthAccessToken = m_preferences.getString(PreferenceConstants.oAuthToken, null);

        // Make sure that we do have a token
        if (StringUtility.isNotNullOrEmpty(m_oAuthAccessToken) && StringUtility.isNotNullOrEmpty(m_userProfileId)) {
            if (BuildConfig.DEBUG) {
                Log.d("PersistantModel", "Restored token and userid for new persistant model.");
            }
            // Good!
            return true;
        } else {
            // Also clear the profileId;

            return false;
        }
    }

    private void storeCredentials() {
        Editor edit = m_preferences.edit();
        edit.putString(PreferenceConstants.oAuthToken, getOAuthAccessToken());
        edit.putString(PreferenceConstants.userprofileId, getUserProfileId());
        edit.apply();
    }

    public static PersistantModel getInstance() {
        return s_instance;
    }

    /**
     * Will clear the password from storage, switch state to "logged out".
     */
    public void handleWrongCredentials(Context c) {
        clearCredentials();
        if (c != null) {
            // Go back to login
            Intent intent = new Intent(c, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            c.startActivity(intent);
            Toast.makeText(PurpleSkyApplication.get(), PurpleSkyApplication.get().getString(R.string.CredentialsInvalid_Toast),
                    Toast.LENGTH_LONG).show();

            if (c instanceof Activity) {
                ((Activity) c).finish();
            }

        } else {
            Toast.makeText(PurpleSkyApplication.get(), PurpleSkyApplication.get().getString(R.string.CredentialsInvalid_Toast),
                    Toast.LENGTH_LONG).show();
        }
    }

    public synchronized String getUserProfileId() {
        return m_userProfileId;
    }

    public synchronized String getOAuthAccessToken() {
        return m_oAuthAccessToken;
    }

    public synchronized void setUserCredentials(String userProfileId, String oAuthAccessToken) {
        m_userProfileId = userProfileId;
        m_oAuthAccessToken = oAuthAccessToken;
        storeCredentials();
    }

    /**
     * This will remove any preferences and saved user data. It will also end the update service in case it should be running.
     */
    private synchronized void clearCredentials() {
        m_oAuthAccessToken = null;
        m_userProfileId = null;
        // Synchronous
        m_preferences.edit().clear().commit();
    }

    private static final long EXPIRY_PROFILEPIC = 1000 * 60 * 60 * 24; // A day

    /**
     * Retrieve cached profile picture of user
     * 
     * @return The cached url or <tt>null</tt> if it expired or is not found
     */
    public String getCachedOwnProfilePictureURLDirectory() {
        String url = m_preferences.getString(PreferenceConstants.cachedOwnUserProfilePictureUrl, null);
        long expiry = m_preferences.getLong(PreferenceConstants.cachedOwnUserProfilePictureUrlExpiry, 0);
        if (url == null || (System.currentTimeMillis() - expiry < EXPIRY_PROFILEPIC)) {
            return url;
        }
        return null;
    }

    public void setCachedOwnProfilePictureURLDirectory(String s) {
        Editor edit = m_preferences.edit();
        edit.putString(PreferenceConstants.cachedOwnUserProfilePictureUrl, s);
        edit.putLong(PreferenceConstants.cachedOwnUserProfilePictureUrlExpiry, System.currentTimeMillis());
        edit.apply();
    }
}
