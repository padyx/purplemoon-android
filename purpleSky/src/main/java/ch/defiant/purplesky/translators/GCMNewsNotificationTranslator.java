package ch.defiant.purplesky.translators;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.InboxStyle;

import java.util.Date;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.PhotoVoteTabbedActivity;
import ch.defiant.purplesky.activities.PostitTabbedActivity;
import ch.defiant.purplesky.activities.VisitorTabbedActivity;
import ch.defiant.purplesky.activities.chatlist.ChatListActivity;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.constants.NotificationConstants;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.MathUtility;
import ch.defiant.purplesky.util.NotificationUtility;
import ch.defiant.purplesky.util.StringUtility;

public class GCMNewsNotificationTranslator {

    public static final String TAG = GCMNewsNotificationTranslator.class.getSimpleName();

    public static void handleGCMNotification(Bundle extras, Context c) {

        int unseenVisits = StringUtility.permissiveInt(extras.getString(PurplemoonAPIConstantsV1.GCM_EXTRA_VISITSUNSEEN), 0);
        int unseenPostits = StringUtility.permissiveInt(extras.getString(PurplemoonAPIConstantsV1.GCM_EXTRA_POSTITSUNSEEN), 0);
        int unseenVotes = StringUtility.permissiveInt(extras.getString(PurplemoonAPIConstantsV1.GCM_EXTRA_VOTESUNSEEN), 0);

        {
            PurpleSkyApplication appContext = PurpleSkyApplication.get();
            appContext.setEventCount(NavigationDrawerEventType.POSTIT, unseenPostits);
            appContext.setEventCount(NavigationDrawerEventType.VISIT, unseenVisits);
        }

        long timestVisits = StringUtility.permissiveLong(extras.getString(PurplemoonAPIConstantsV1.GCM_EXTRA_VISITS_LASTTIMESTAMP), 0);
        long timestPostits = StringUtility.permissiveLong(extras.getString(PurplemoonAPIConstantsV1.GCM_EXTRA_POSTITS_LASTTIMESTAMP), 0);
        long timestVotes = StringUtility.permissiveLong(extras.getString(PurplemoonAPIConstantsV1.GCM_EXTRA_VOTES_LASTTIMESTAMP), 0);

        long newest = MathUtility.max(timestPostits, timestVisits, timestVotes);

        int unseenEvents = unseenPostits + unseenVisits + unseenVotes;
        if (unseenEvents == 0) {
            return;
        }

        SharedPreferences prefs = PreferenceUtility.getPreferences();
        long lastNotification = prefs.getLong(PreferenceConstants.lastEventNotification, 0);

        // Should not notify: Newest is older than previous notification to user
        if (lastNotification >= newest) {
            // Should actually not happen
            return;
        }

        Builder b = NotificationUtility.createBasicNotification(c);
        b.setContentTitle(c.getString(R.string.NewEvents));
        InboxStyle inbox = new NotificationCompat.InboxStyle();

        int lines = 0;
        int launchArg = -1;
        Class<? extends Activity> activity = ChatListActivity.class; // Default

        boolean notify = prefs.getBoolean(PreferenceConstants.notifyForPostit, true);
        final Resources res = c.getResources();
        if (timestPostits > lastNotification && unseenPostits > 0 && notify) {
            CharSequence text = res.getQuantityString(R.plurals.XNewPostits, unseenPostits, unseenPostits);
            inbox.addLine(text);
            if (lines == 0) {
                activity = PostitTabbedActivity.class;
                b.setContentText(text);
                launchArg = BaseFragmentActivity.NavigationDrawerEntries.LAUNCH_POSTIT.ordinal();
            }
            lines++;
        }
        notify = prefs.getBoolean(PreferenceConstants.notifyForVotes, false);
        if (timestVotes > lastNotification && unseenVotes > 0 && notify) {
            CharSequence text = res.getQuantityString(R.plurals.XNewPhotovotes, unseenVotes, unseenVotes);
            inbox.addLine(text);
            if (lines == 0) {
                activity = PhotoVoteTabbedActivity.class;
                b.setContentText(text);
                launchArg = BaseFragmentActivity.NavigationDrawerEntries.LAUNCH_PHOTOVOTE.ordinal();
            }
            lines++;
        }
        notify = prefs.getBoolean(PreferenceConstants.notifyForVisits, false);
        if (timestVisits > lastNotification && unseenVisits > 0 && notify) {
            CharSequence text = res.getQuantityString(R.plurals.XNewVisits, unseenVisits, unseenVisits);
            inbox.addLine(text);
            if (lines == 0) {
                activity = VisitorTabbedActivity.class;
                b.setContentText(text);
                launchArg = BaseFragmentActivity.NavigationDrawerEntries.LAUNCH_VISITORS.ordinal();
            }
            lines++;
        }
        inbox.setSummaryText(res.getQuantityString(R.plurals.XNewEvents, unseenEvents, unseenEvents));
        if (lines == 0) {
            return;
        }

        b.setStyle(inbox);

        Intent intent = new Intent(c, activity);
        if (launchArg != -1 && lines == 1) {
            // Only go to a specific fragment, if it is clear which one
            intent.putExtra(BaseFragmentActivity.EXTRA_LAUNCH_OPTION, launchArg);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        b.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationConstants.NEWEVENTS, b.build());

        Editor editPrefs = prefs.edit();
        editPrefs.putLong(PreferenceConstants.lastEventNotification, DateUtility.getUnixTime(new Date()));
        editPrefs.apply();
    }
}
