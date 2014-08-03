package ch.defiant.purplesky.translators;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.main.MainActivity;
import ch.defiant.purplesky.api.internal.JSONTranslator;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.constants.NotificationConstants;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.fragments.ChatListFragment;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.NVLUtility;
import ch.defiant.purplesky.util.NotificationUtility;
import ch.defiant.purplesky.util.StringUtility;

public class GCMChatNotificationTranslator {

    private static final String TAG = GCMChatNotificationTranslator.class.getSimpleName();
    private static final int INBOX_MAX_LINES = 5;

    public static void handleGCMNotification(Bundle extras, Context c) {
        // TODO Set time!
        int unreadConversations = StringUtility.permissiveInt(extras.getString(PurplemoonAPIConstantsV1.GCM_EXTRA_CHATS_UNREAD), 0);

        Map<String, MinimalUser> map = new HashMap<String, MinimalUser>();
        JSONArray users = new JSONArray();
        JSONArray chats = new JSONArray();

        try {
            users = new JSONArray(NVLUtility.nvl(extras.getString(PurplemoonAPIConstantsV1.GCM_EXTRA_USERS), ""));
            map = JSONTranslator.translateToUsers(users, MinimalUser.class);
            chats = new JSONArray(NVLUtility.nvl(extras.getString(PurplemoonAPIConstantsV1.GCM_EXTRA_NEWEST), ""));
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "JSON Parse Exception", e);
            }
        }

        List<Conversation> conversations = translateToConversations(chats);

        Builder b = NotificationUtility.createBasicNotification(c);

        Intent intent = new Intent(c, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Arguments
        intent.putExtra(MainActivity.EXTRA_LAUNCH_OPTION, MainActivity.NavigationDrawerEntries.LAUNCH_CHATLIST.ordinal());

        final Resources res = c.getResources();
        if (unreadConversations > 1) {
            processInboxStyle(unreadConversations, map, conversations, b, res);
        } else {
            processBigStyle(map, b, intent, conversations.get(0), res);
        }

        // NOTE: FLAG_CANCEL_CURRENT is VERY important! Otherwise the same intent (extras) will be delivered over and over again.
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        b.setContentIntent(pendingIntent);

        SharedPreferences prefs = PreferenceUtility.getPreferences();
        boolean shouldNotify = prefs.getBoolean(PreferenceConstants.notifyForMessages, true);

        if (shouldNotify) {
            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NotificationConstants.NEWMESSAGES, b.build());
        }

        Editor editPrefs = prefs.edit();
        editPrefs.putLong(PreferenceConstants.lastEventNotification, DateUtility.getUnixTime(new Date()));
        editPrefs.apply();
    }

    private static void processBigStyle(Map<String, MinimalUser> map, Builder b, Intent intent, Conversation firstConversation, final Resources res) {
        // Only one conversation.
        // Only have one message per conversation...
        String title = res.getString(R.string.NewMessages);
        MinimalUser u = map.get(firstConversation.profileId);
        if (u != null) {
            title = u.getUsername();
        }
        b.setContentTitle(title);
        b.setContentText(firstConversation.excerpt);
        BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(firstConversation.excerpt);
        b.setStyle(bigTextStyle);

        // Pass the argument along which chat to open
        Bundle nestedargs = new Bundle();
        nestedargs.putString(ChatListFragment.EXTRA_STRING_GOCHAT, firstConversation.profileId);
        intent.putExtra(MainActivity.EXTRA_LAUNCH_ARGS, nestedargs);
    }

    private static void processInboxStyle(int unreadConversations, Map<String, MinimalUser> map, List<Conversation> conversations, Builder b,
            final Resources res) {
        final Conversation firstConversation = conversations.get(0);
        final String titleNewMsgs = res.getString(R.string.NewMessages);
        String title = titleNewMsgs;
        MinimalUser firstuser = map.get(firstConversation.profileId);
        
        if(firstuser != null){
            title = firstuser.getUsername();
        }
        
        b.setContentTitle(title);
        b.setContentText(firstConversation.excerpt);
        InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        final int size = conversations.size();
        for (int i = 0; i < size && i < INBOX_MAX_LINES; i++) {
            Conversation conv = conversations.get(i);
            if (conv.unread == 0) {
                // Don't care about those with nothing in them!
                continue;
            }
            MinimalUser usr = map.get(conv.profileId);
            Spanned txt = Html.fromHtml(
                    "<html><body><b>" + usr.getUsername() +
                            "</b>&nbsp;&nbsp;&nbsp;" + conv.excerpt + "</body></html>"
                    );
            inboxStyle.addLine(txt);
        }
        String summaryString = res.getQuantityString(R.plurals.XUnreadConversations, unreadConversations, unreadConversations);
        inboxStyle.setBigContentTitle(titleNewMsgs);
        inboxStyle.setSummaryText(summaryString);
        b.setStyle(inboxStyle);
    }

    private static List<Conversation> translateToConversations(JSONArray chats) {
        ArrayList<Conversation> list = new ArrayList<Conversation>();
        for (int i = 0; i < chats.length(); i++) {
            Conversation c = new Conversation();
            JSONObject obj = chats.optJSONObject(i);

            c.unread = obj.optInt(PurplemoonAPIConstantsV1.GCM_EXTRA_CHATS_UNOPENED, 0);
            c.profileId = obj.optString(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID);
            c.excerpt = obj.optString(PurplemoonAPIConstantsV1.GCM_EXTRA_CHATS_EXCERPT);

            list.add(c);
        }
        return list;
    }

    private static class Conversation {
        String profileId;
        int unread;
        String excerpt;
    }
}
