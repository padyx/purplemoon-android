package ch.defiant.purplesky.loaders;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.PurplemoonAPIAdapter;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


/**
 * Unregisters from GCM if present.
 * Returns a boolean which indicates success (true) or failure.
 * @author Patrick Bänziger
 */
public class LogoutLoader extends SimpleAsyncLoader<Object> {

    private static final String TAG = LogoutLoader.class.getSimpleName();

    public LogoutLoader(Context context) {
        super(context, R.id.loader_main_logout);
    }

    @Override
    public Boolean loadInBackground() {
        SharedPreferences prefs = PreferenceUtility.getPreferences();
        String gcmId = prefs.getString(PreferenceConstants.gcmToken, null);
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if(gcmId != null && result == ConnectionResult.SUCCESS){
            try{
                boolean unregisterPush = PurplemoonAPIAdapter.getInstance().unregisterPush(gcmId);
                Log.i(TAG, "Unregistering from Push messages on Logout. Has succeeded? "+unregisterPush);
                return unregisterPush;
            }catch(IOException e){
                Log.i(TAG, "Could not unregister from push. Abort logout",e);
            } catch (PurpleSkyException e) {
                Log.w(TAG, "Error during unregistering",e);
            }
            return false;
        }
        // Not available or configured - OK!
        return true;
    }

}
