package ch.defiant.purplesky.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.beans.PushStatus;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.constants.SecureConstants;
import ch.defiant.purplesky.core.UpgradeHandler;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.StringUtility;

/**
 * Performs any upgrade actions that don't need to block.
 *
 * @author Patrick Bänziger
 */
public class UpgradeTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = UpgradeTask.class.getSimpleName();
    private final IPurplemoonAPIAdapter apiAdapter;
    private final Context context;

    public static final AtomicReference<UpgradeTask> INSTANCE = new AtomicReference<>();

    public UpgradeTask(Context context, IPurplemoonAPIAdapter apiAdapter) {
        this.context = context;
        this.apiAdapter = apiAdapter;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Perform upgrade actions
        try {
            new UpgradeHandler(apiAdapter).performUpgradeActions(context);
            return null;
        } finally {
            INSTANCE.compareAndSet(this, null);
        }
    }

}
