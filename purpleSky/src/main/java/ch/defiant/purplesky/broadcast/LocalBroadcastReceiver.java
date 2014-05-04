package ch.defiant.purplesky.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ch.defiant.purplesky.interfaces.IBroadcastReceiver;

/**
 * Convenience wrapper for broadcast receivers that don't want to directly extend {@link BroadcastReceiver}.
 * 
 * @author Patrick Bänziger
 * 
 */
public class LocalBroadcastReceiver extends BroadcastReceiver {

    private IBroadcastReceiver m_receiver;

    /**
     * Constructor.
     * 
     * @param receiver
     *            The receiver, must be non-null.
     * @throws IllegalArgumentException
     *             If the receiver is null
     */
    public LocalBroadcastReceiver(IBroadcastReceiver receiver) {
        if (receiver == null)
            throw new IllegalArgumentException("Reciever was NULL!");
        m_receiver = receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        m_receiver.onReceive(context, intent);
    }

}
