package ch.defiant.purplesky.gcm;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.constants.SecureConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

public class GcmRegisterTask extends AsyncTask<Boolean, Void, Void> {
    private static final String TAG = GcmRegisterTask.class.getSimpleName();

    private final IPurplemoonAPIAdapter m_adapter;

    public GcmRegisterTask(IPurplemoonAPIAdapter apiAdapter) {
        m_adapter = apiAdapter;
    }

    @Override
    protected Void doInBackground(Boolean... params) {
        boolean register = true;
        if (params != null && params.length > 0 && params[0] != null) {
            register = params[0];
        }
        if (register) {
            return register();
        } else {
            return unregister();
        }
    }

    private Void unregister() {
        int tries = 3;
        while (tries > 0) {
            tries--;

            try {
                String oldRegId = PreferenceUtility.getPreferences().getString(PreferenceConstants.gcmToken, null);
                if (oldRegId != null) {
                    boolean stillActive = m_adapter.unregisterPush(oldRegId);
                    if (!stillActive) {
                        return null;
                    }
                } else {
                    return null;
                }
            } catch (IOException e) {
                Log.i(TAG, "Sending push registration to server failed with IOException. Remaining retries: " + tries);
            } catch (PurpleSkyException e) {
                Log.i(TAG, "Sending push registration to server failed with PurpleskyException. Remaining retries: " + tries);
            }
        }
        return null;
    }

    private Void register() {
        int tries = 3;
        String regId = null;
        while (tries > 0) {
            tries--;
            try {
                regId = GoogleCloudMessaging.getInstance(PurpleSkyApplication.get()).
                        register(SecureConstants.get(SecureConstants.GCM_ID));

                if (regId != null && !regId.isEmpty()) {
                    break;
                }
            } catch (IOException e) {
                Log.i(TAG, "Registring for push messages failed with IOException. Remaining retries: " + tries);
            }
        }

        if (regId == null || regId.isEmpty()) {
            Log.i(TAG, "Could not register for push messages!");
            return null;
        }

        tries = 3;
        String oldRegId = PreferenceUtility.getPreferences().getString(PreferenceConstants.gcmToken, null);
        while (tries > 0) {
            tries--;

            try {
                if (oldRegId != null && !oldRegId.equals(regId)) {
                    if(m_adapter.unregisterPush(oldRegId)){
                        oldRegId = null; // Unregistering once is enough
                    }
                    
                }
                if (m_adapter.registerPush(regId)) {
                    PreferenceUtility.getPreferences().edit().putString(PreferenceConstants.gcmToken, regId).commit();
                    Log.i(TAG, "Registering for push messages successful.");
                    return null;
                }
            } catch (IOException e) {
                Log.i(TAG, "Sending push registration to server failed with IOException. Remaining retries: " + tries);
            } catch (PurpleSkyException e) {
                Log.i(TAG, "Sending push registration to server failed with PurpleskyException. Remaining retries: " + tries);
            }
        }
        return null;
    }

}
