package ch.defiant.purplesky.util;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;

public class NotificationUtility {

    public static Builder createBasicNotification(Context c) {
        Builder b = new NotificationCompat.Builder(c);
        b.setSmallIcon(R.drawable.icon_notification);
        b.setAutoCancel(true);
        int defaults = Notification.DEFAULT_LIGHTS;
        SharedPreferences prefs = PreferenceUtility.getPreferences();
        if (prefs.getBoolean(PreferenceConstants.notificationVibrateEnabled, true)) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }

        String customSound = prefs.getString(PreferenceConstants.notificationCustomSound, null);
        Uri customSoundUri = null;
        if (customSound != null) {
            customSoundUri = Uri.parse(customSound);
            if (customSoundUri != null) {
                String type = c.getContentResolver().getType(customSoundUri);
                if (type == null) {
                    // Reset it to default
                    Editor editor = prefs.edit();
                    String defaultVal = Settings.System.DEFAULT_NOTIFICATION_URI.toString();
                    editor.putString(PreferenceConstants.notificationCustomSound, defaultVal).apply();
                }
            }
        }

        if (customSoundUri != null) {
            b.setSound(customSoundUri);
        } else {
            // No sound
        }

        b.setDefaults(defaults); // Without sound
        return b;
    }
}
