package ch.defiant.purplesky.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.main.MainActivity;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.NotificationBean;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.constants.NotificationConstants;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1.MessageRetrievalRestrictionType;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;
import ch.defiant.purplesky.exceptions.WrongCredentialsException;
import ch.defiant.purplesky.fragments.ChatListFragment;
import ch.defiant.purplesky.util.CompareUtility;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import javax.inject.Inject;

public class UpdateService extends IntentService {

    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;

    public UpdateService() {
        super("UpdateService");
        PurpleSkyApplication.get().inject(this);
    }

    private final static int UPDATE_INTERVAL_DEFAULT = 1 * 60 * 1000;
    private static final String TAG = UpdateService.class.getSimpleName();

    @Override
    protected void onHandleIntent(Intent intent) {
        final SharedPreferences pref = PreferenceUtility.getPreferences();
        long lastRun = pref.getLong(PreferenceConstants.lastUpdateServiceRun, 0);

        List<UserMessageHistoryBean> newMessages = new ArrayList<UserMessageHistoryBean>();
        try {
            // Are we cancelled? Following is longer running network-I/O
            if (Thread.currentThread().isInterrupted())
                return;

            boolean hasMessages = false;
            boolean hasNewGeneralNews = false;
            NotificationBean notifBean = apiAdapter.getNotificationBean();
            if (notifBean != null) {
                if (notifBean.getLastMessageReceived() != null && CompareUtility.compare(notifBean.getLastMessageReceived().getTime(), lastRun) == 1) {
                    // Last received time > last check
                    hasMessages = true;
                }
                if (notifBean.getLastGeneralNewsUpdate() != null
                        && CompareUtility.compare(notifBean.getLastGeneralNewsUpdate().getTime(), lastRun) == 1) {
                    hasNewGeneralNews = true;
                }
            }

            if (hasNewGeneralNews) {
                // Fetch other bean
                // TODO PBN Load events
            }
            // Are we cancelled? Following is longer running network-I/O
            if (Thread.currentThread().isInterrupted())
                return;

            int totalUnopenedMessages = 0;
            if (hasMessages) {
                List<UserMessageHistoryBean> recentContactsList = apiAdapter.getRecentContacts(null, null,
                        MessageRetrievalRestrictionType.UNOPENED_ONLY);

                if (recentContactsList != null) {
                    for (UserMessageHistoryBean bean : recentContactsList) {
                        if(bean != null){
                            Integer unopened = bean.getUnopenedMessageCount();
                            if (bean.getLastContact() != null && unopened > 0) {
                                newMessages.add(bean);
                                totalUnopenedMessages += unopened;
                            }
                        }
                    }
                }
            }
            // Notify
            PurpleSkyApplication.get().setEventCount(NavigationDrawerEventType.MESSAGE, totalUnopenedMessages);

            if (!newMessages.isEmpty()) {
                notifyMessages(newMessages, totalUnopenedMessages);
            }

        } catch (IOException e) {
            // IOException, backOff
        } catch (WrongCredentialsException e) {
            PersistantModel.getInstance().handleWrongCredentials(null);
            stopSelf();
            return;
        } catch (Exception e) {
            Log.e(TAG, "Update service exception, ignoring", e);
        }
        lastRun = System.currentTimeMillis();
        pref.edit().putLong(PreferenceConstants.lastUpdateServiceRun, lastRun).commit();
    }

    /**
     * Registers the UpdateService to be scheduled for running.
     */
    public static void registerUpdateService() {
        SharedPreferences pref = PreferenceUtility.getPreferences();
        int intervalMillis = pref.getInt(PreferenceConstants.updateInterval, UPDATE_INTERVAL_DEFAULT);
        boolean updateEnabled = pref.getBoolean(PreferenceConstants.updateEnabled, true);

        // Unregister any update service from the alarm manager
        unregisterUpdateService();
        // Register a new one, only if the play services are not available
        int playAvailableResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(PurpleSkyApplication.get());
        if (updateEnabled && (playAvailableResult != ConnectionResult.SUCCESS)) {
            PendingIntent pi = createPendingIntentUpdateService();

            // Startup delay:
            final long currTimeMillis = System.currentTimeMillis();
            long delay = currTimeMillis % intervalMillis;

            AlarmManager manager = (AlarmManager) PurpleSkyApplication.get().getSystemService(ALARM_SERVICE);
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, currTimeMillis + delay, intervalMillis, pi);
        }
    }

    /**
     * Unregisters the UpdateService.
     */
    public static void unregisterUpdateService() {
        AlarmManager manager = (AlarmManager) PurpleSkyApplication.get().getSystemService(ALARM_SERVICE);
        manager.cancel(createPendingIntentUpdateService());
    }

    private static PendingIntent createPendingIntentUpdateService() {
        Intent intent = new Intent(PurpleSkyApplication.get(), UpdateService.class);
        PendingIntent pi = PendingIntent.getService(PurpleSkyApplication.get(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pi;
    }

    private void notifyMessages(List<UserMessageHistoryBean> newMessages, int totalUnopenedMessages) {
        Context appContext = PurpleSkyApplication.get();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext);

        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Arguments
        intent.putExtra(MainActivity.EXTRA_LAUNCH_OPTION, MainActivity.NavigationDrawerEntries.LAUNCH_CHATLIST.ordinal());
        if (newMessages.size() == 1) {
            Bundle nestedargs = new Bundle();
            UserMessageHistoryBean historyBean = newMessages.get(0);
            nestedargs.putString(ChatListFragment.EXTRA_STRING_GOCHAT, historyBean.getProfileId());
            intent.putExtra(MainActivity.EXTRA_LAUNCH_ARGS, nestedargs);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(UpdateService.this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.icon_notification);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setContentTitle(getResources().getString(R.string.NewMessages));
        builder.setContentText(getString(R.string.XMessagesByYContacts, totalUnopenedMessages, newMessages.size()));

        if (newMessages.size() == 1) {
            UserMessageHistoryBean msg = newMessages.get(0);
            builder.setWhen(msg.getLastContact().getTime());
            MinimalUser userBean = msg.getUserBean();
            if (userBean != null) {
                builder.setContentTitle(userBean.getUsername());
                builder.setContentText(String.format(getResources().getQuantityString(R.plurals.NewMessage_Quantity, 1), 1));
                setLargeIcon(builder, userBean);
            }
        } else {
            builder.setNumber(totalUnopenedMessages);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationConstants.NEWMESSAGES, builder.build());
    }

    /**
     * Try to set the preview picture of the user as the large notification icon. This will succeed only if the image is cached already.
     * 
     * @param builder
     *            Notifiaction Builder where to set the icon
     * @param userBean
     *            Userbean describing the user
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setLargeIcon(NotificationCompat.Builder builder, MinimalUser userBean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }

        // FIXME Image for icon

        // // TODO This is a fixed size...Maybe extract to a constant (with all other callers of getPictureSize)
        // URL u = UserService.getInstance().getUserPreviewPicturUrl(userBean, UserPreviewPictureSize.getPictureSizeForDpi(50, getResources()));
        // if (u != null) {
        // Picasso.with(this).
        // CacheableBitmapDrawable bitmap = ImageManager.getInstance().getCachedBitmap(u.toString());
        // if (bitmap != null) {
        // int height = (int) getResources().getDimension(android.R.dimen.notification_large_icon_height);
        // int width = (int) getResources().getDimension(android.R.dimen.notification_large_icon_width);
        // builder.setLargeIcon(Bitmap.createScaledBitmap(bitmap.getBitmap(), width, height, false));
        // }
        // }

    }

}
