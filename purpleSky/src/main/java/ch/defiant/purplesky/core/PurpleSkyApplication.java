package ch.defiant.purplesky.core;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import javax.inject.Inject;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class PurpleSkyApplication extends DaggerApplication  {

    private static PurpleSkyApplication instance;
    private final FragmentTransfer fragment_transfer_instance = FragmentTransfer.INSTANCE;

    private UserService m_userservice;
    private HashMap<NavigationDrawerEventType, Integer> m_notificationCounts;
    private WeakReference<UpdateListener> m_listener = new WeakReference<>(null);
    private PersistantModel m_model;

    public PurpleSkyApplication() {
        instance = this;
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new ThreadPolicy.Builder().detectAll().build());
            StrictMode.setVmPolicy(new VmPolicy.Builder().detectAll().build());
        }
    }


    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerPurpleSkyComponent.builder().build();
    }

    public static PurpleSkyApplication get() {
        return instance;
    }


    public UserService getUserService() {
        if (m_userservice == null) {
            m_userservice = new UserService();
        }
        return m_userservice;
    }

    public NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
    
    public PersistantModel getPersistantModel(){
        if(m_model == null){
            m_model = PersistantModel.getInstance();
        }
        return m_model;
    }

    /**
     * Retrieve the cached notification count
     * 
     * @param type
     * @return The cached count, or <tt>null</tt> if uncached.
     */
    public synchronized Integer getEventCount(NavigationDrawerEventType type) {
        if (m_notificationCounts == null) {
            m_notificationCounts = new HashMap<>();
        }
        return m_notificationCounts.get(type);
    }

    public synchronized void setEventCount(NavigationDrawerEventType type, int count) {
        if (m_notificationCounts == null) {
            m_notificationCounts = new HashMap<>();
        }
        m_notificationCounts.put(type, count);
        UpdateListener l = m_listener.get();
        if (l != null) {
            l.update(type, count);
        }
    }

    public synchronized void decreaseEventCount(NavigationDrawerEventType type, int count) {
        Integer cnt = getEventCount(type);
        if (cnt == null) {
            // Can't decrease a non cached value
            return;
        }
        int c = cnt - count;
        if (c < 0) {
            c = 0;
        }
        setEventCount(type, c);
    }

    public abstract static class UpdateListener {
        public abstract void update(NavigationDrawerEventType t, int count);
    }

    public void setListener(UpdateListener l) {
        m_listener = new WeakReference<>(l);
    }

    public FragmentTransfer getFragmentTransferInstance() {
        return fragment_transfer_instance;
    }

}
