package ch.defiant.purplesky.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.translators.GCMChatNotificationTranslator;
import ch.defiant.purplesky.translators.GCMNewsNotificationTranslator;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService {
    public static final String TAG = GCMIntentService.class.getSimpleName();

    public GCMIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        try {
            // The getMessageType() intent parameter must be the intent you received
            // in your BroadcastReceiver.
            String messageType = gcm.getMessageType(intent);

            if (!extras.isEmpty()) { // has effect of unparcelling Bundle
                /*
                 * Filter messages based on message type. Since it is likely that GCM will be extended in the future with new message types, just
                 * ignore any message types you're not interested in, or that you don't recognize.
                 */
                if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Received push message " + extras.toString());
                    }
                    // Post notification of received message.
                    handlePushMessage(extras);
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "Received unknown message from GCM! Message type was " + messageType);
                        Log.d(TAG, extras.toString());
                    }
                }
            }
        } finally {
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GCMBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void handlePushMessage(Bundle extras) {
        if (PersistantModel.getInstance().getOAuthAccessToken() == null) {
            // Dropping message
            // TODO Handle this (GCM)
            return;
        }

        String type = extras.getString(PurplemoonAPIConstantsV1.GCM_EXTRA_TYPE);
        if (PurplemoonAPIConstantsV1.GCM_EXTRA_TYPE_CHATS.equals(type)) {
            GCMChatNotificationTranslator.handleGCMNotification(extras, this);
        } else if (PurplemoonAPIConstantsV1.GCM_EXTRA_TYPE_NEWS.equals(type)) {
            GCMNewsNotificationTranslator.handleGCMNotification(extras, this);
        } else {
            Log.d(TAG, "Unknown message type");
        }
    }

}