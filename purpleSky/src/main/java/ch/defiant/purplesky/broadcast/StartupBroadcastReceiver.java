package ch.defiant.purplesky.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ch.defiant.purplesky.core.UpdateService;

public class StartupBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        UpdateService.registerUpdateService();
    }

}
