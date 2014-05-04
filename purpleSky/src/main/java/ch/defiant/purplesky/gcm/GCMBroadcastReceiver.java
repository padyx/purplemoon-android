package ch.defiant.purplesky.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        // Set the component name that should handle the intent
        ComponentName comp = new ComponentName(ctx.getPackageName(), GCMIntentService.class.getName());

        // Start the service, keep the device awake for its duration!
        startWakefulService(ctx, (intent.setComponent(comp)));

        setResultCode(Activity.RESULT_OK);
    }

}
