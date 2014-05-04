package ch.defiant.purplesky.interfaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ch.defiant.purplesky.broadcast.LocalBroadcastReceiver;

/**
 * Interface to allow classes that don't want to extend {@link BroadcastReceiver} the same functionality. Use {@link LocalBroadcastReceiver} to wrap
 * the receiver which must implement this interface.
 * 
 * @author Patrick Bänziger
 * 
 */
public interface IBroadcastReceiver {

    public void onReceive(Context context, Intent intent);
}
