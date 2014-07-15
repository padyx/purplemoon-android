package ch.defiant.purplesky.loaders;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Pair;

import java.io.IOException;
import java.util.Date;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.beans.AlertBean;
import ch.defiant.purplesky.beans.UpdateBean;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

public class NotificationLoader extends SimpleAsyncLoader<Object> {

    private final IPurplemoonAPIAdapter adapter;

    public NotificationLoader(Context c, IPurplemoonAPIAdapter adapter) {
        super(c, R.id.loader_drawermenu_notificationCounters);
        this.adapter = adapter;
    }

    @Override
    public Object loadInBackground() {
        UpdateBean updateBean = new UpdateBean();
        try {
            AlertBean alertBean = adapter.getAlertBean();

            int onlineFavoritCount = adapter.getOnlineFavoritesCount();
            updateBean.setFavoritesCount(onlineFavoritCount);

            int unopenedMessagesCount = adapter.getUnopenedMessagesCount();
            updateBean.setMessagesCount(unopenedMessagesCount);

            updateBean.setPostItCount(alertBean.getUnseenPostits());
            updateBean.setVisitCount(alertBean.getUnseenVisits());

            Pair<OnlineStatus, String> onlineStatus = adapter.getOwnOnlineStatus();
            if (onlineStatus != null) {
                updateBean.setPredefinedOnlineStatus(onlineStatus.first);
                updateBean.setCustomOnlineStatus(onlineStatus.second);
            }

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
    }
}