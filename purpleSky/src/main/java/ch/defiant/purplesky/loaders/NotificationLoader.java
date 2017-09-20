package ch.defiant.purplesky.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.beans.AlertBean;
import ch.defiant.purplesky.beans.UpdateBean;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

public class NotificationLoader extends SimpleAsyncLoader<Object> {

    private static final String TAG = NotificationLoader.class.getSimpleName();

    private static final long REFRESH_INTERVAL_MILLIS = 60 * 1000; // Each minute
    private static final String CACHE_FILE_NAME = "UpdateBeanCache";

    private final IPurplemoonAPIAdapter adapter;
    private final IConversationAdapter conversationAdapter;

    public NotificationLoader(Context c, IPurplemoonAPIAdapter adapter, IConversationAdapter conversationAdapter) {
        super(c, R.id.loader_drawermenu_notificationCounters);
        this.adapter = adapter;
        this.conversationAdapter = conversationAdapter;
    }

    @Override
    public Object loadInBackground() {
        boolean updateNecessary = isUpdateNecessary();
        final File cacheFile = new File(getContext().getCacheDir(), CACHE_FILE_NAME);

        if(updateNecessary || !cacheFile.exists()) {
            try {
                final UpdateBean updateBean = loadUpdateBeanFromApi();
                saveUpdateBeanToCache(cacheFile, updateBean);

                // Make sure the power user status is set properly
                if (!UserService.isCachedPowerUser()) {
                    try {
                        Date expiry = adapter.getPowerUserExpiry();
                        if (expiry != null) {
                            Editor edit = PreferenceUtility.getPreferences().edit();
                            edit.putLong(PreferenceConstants.powerUserExpiry, expiry.getTime());
                            edit.apply();
                        }
                    } catch (Exception e) {
                    }
                }

                return updateBean;
            } catch (IOException e) {
                return null;
            } catch (PurpleSkyException e) {
                return null;
            }
        } else {
            return loadCachedUpdateBean(cacheFile);
        }
    }

    @NonNull
    private UpdateBean loadUpdateBeanFromApi() throws IOException, PurpleSkyException {
        final UpdateBean updateBean = new UpdateBean();
        int onlineFavoriteCount = adapter.getOnlineFavoritesCount();
        updateBean.setFavoritesCount(onlineFavoriteCount);

        int unopenedMessagesCount = conversationAdapter.getUnopenedMessagesCount();
        updateBean.setMessagesCount(unopenedMessagesCount);

        AlertBean alertBean = adapter.getAlertBean();
        if (alertBean != null) {
            updateBean.setPostItCount(alertBean.getUnseenPostits());
            updateBean.setVisitCount(alertBean.getUnseenVisits());
        }

        Pair<OnlineStatus, String> onlineStatus = adapter.getOwnOnlineStatus();
        if (onlineStatus != null) {
            updateBean.setPredefinedOnlineStatus(onlineStatus.first);
            updateBean.setCustomOnlineStatus(onlineStatus.second);
        }
        return updateBean;
    }

    private boolean isUpdateNecessary() {
        SharedPreferences preferences = PreferenceUtility.getPreferences();
        if(!preferences.contains(PreferenceConstants.drawerCountsLastRefresh)){
            return true;
        }
        long lastRefresh = preferences.getLong(PreferenceConstants.drawerCountsLastRefresh, 0);

        return (System.currentTimeMillis() - lastRefresh) > REFRESH_INTERVAL_MILLIS;
    }

    @Nullable
    private UpdateBean loadCachedUpdateBean(File cacheFile) {
        ObjectInputStream stream = null;
        try {
            try {
                stream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(cacheFile)));
                Object o = stream.readObject();
                if(o instanceof UpdateBean){
                    return (UpdateBean) o;
                } else {
                    Log.e(TAG, "The cache did not contain the expected object but "+o.getClass().getCanonicalName());
                    return null;
                }
            } catch (ClassNotFoundException e) {
                Log.w(TAG, "Class not found when reading update file cache", e);
                return null;
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (IOException e){
            Log.w(TAG, "Loading cache file for update file failed", e);
            return null;
        }
    }

    private void saveUpdateBeanToCache(File cacheFile, UpdateBean bean){
        ObjectOutputStream stream = null;
        try {
            try {
                stream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(cacheFile)));
                stream.writeObject(bean);

            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (IOException e){
            Log.w(TAG, "Writing cache file for update file failed", e);
        }
        Editor edit = PreferenceUtility.getPreferences().edit();
        edit.putLong(PreferenceConstants.drawerCountsLastRefresh, System.currentTimeMillis());
        edit.apply();
    }
}