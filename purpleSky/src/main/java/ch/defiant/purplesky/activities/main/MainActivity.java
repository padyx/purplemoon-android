package ch.defiant.purplesky.activities.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.UpdateBean;
import ch.defiant.purplesky.broadcast.BroadcastTypes;
import ch.defiant.purplesky.broadcast.LocalBroadcastReceiver;
import ch.defiant.purplesky.core.DBHelper;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.PurpleSkyApplication.UpdateListener;
import ch.defiant.purplesky.core.UpdateService;
import ch.defiant.purplesky.customwidgets.ProgressFragmentDialog;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.interfaces.IBroadcastReceiver;
import ch.defiant.purplesky.loaders.LogoutLoader;
import ch.defiant.purplesky.loaders.NotificationLoader;
import ch.defiant.purplesky.loaders.ProfileImageLoader;
import ch.defiant.purplesky.loaders.StatusLoader;
import ch.defiant.purplesky.loaders.UpgradeAndPushLoader;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

/**
 * The main activity hosting all fragments once the user is logged in.
 * @author Patrick Bänziger
 *
 */
public class MainActivity extends SherlockFragmentActivity implements LoaderCallbacks<Object> {

    private static final String LOGOUT_FRAGMENT_TAG = "LOGOUT";

  
    // TODO Move
    /**
     * Available Entries in the navigation drawer.
     */
    public enum NavigationDrawerEntries {
        LAUNCH_CHATLIST,
        // LAUNCH_RADAR,
        LAUNCH_POSTIT,
        LAUNCH_VISITORS,
        LAUNCH_FAVORITES,
        LAUNCH_USERSEARCH,
        LAUNCH_PHOTOVOTE,
        LAUNCH_UPLOAD,
        LAUNCH_SETTINGS
    };

    /**
     * When used in arguments, determines which fragment is displayed
     */
    public static final String EXTRA_LAUNCH_OPTION = "launch_fragment";
    /**
     * When used in arguments, passed along to launched fragments. Only meaningful with
     * {@link MainActivity#EXTRA_LAUNCH_OPTION}
     */
    public static final String EXTRA_LAUNCH_ARGS = "launch_arguments";

    private static final String SAVEINSTANCE_TITLE = "title";
    private static final String SAVEINSTANCE_SUBTITLE = "subtitle";

    public static final String TAG = MainActivity.class.getSimpleName();
    private boolean logoutInProgress;

    private CharSequence m_title;
    private CharSequence m_subtitle;

    private UpdateListener m_listener;
    private LocalBroadcastReceiver m_logoutBroadcastReceiver;
    private DrawerDelegate m_drawerDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.fragment_container_layout);

        if (savedInstanceState != null) {
            m_title = savedInstanceState.getString(SAVEINSTANCE_TITLE);
            m_subtitle = savedInstanceState.getString(SAVEINSTANCE_SUBTITLE);
        }

        setupDrawer();

        if (savedInstanceState == null) {
            Bundle e = getIntent().getExtras();
            if (e != null && e.containsKey(EXTRA_LAUNCH_OPTION)) {
                int launchFragment = e.getInt(EXTRA_LAUNCH_OPTION);
                m_drawerDelegate.selectItem(NavigationDrawerEntries.values()[launchFragment], e.getBundle(EXTRA_LAUNCH_ARGS));
            } else {
                m_drawerDelegate.selectFirstItem();
            }
        }

        // Number updates for the drawer
        m_listener = new UpdateListener() {
            @Override
            public void update(NavigationDrawerEventType t, int count) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        m_drawerDelegate.updateEventCounts();
                    }
                });
            }
        };

        // Upgrade actions that don't require blocking stuff
        getSupportLoaderManager().initLoader(R.id.loader_main_upgradePush, null, this);

        PurpleSkyApplication.getContext().setListener(m_listener);
        triggerUpdate();
        registerLogoutReceiver();
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        m_drawerDelegate.postCreate();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(m_logoutBroadcastReceiver);
        m_logoutBroadcastReceiver = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (logoutInProgress) {
            Fragment frag = getSupportFragmentManager().findFragmentByTag(LOGOUT_FRAGMENT_TAG);
            if (frag == null) {
                ProgressFragmentDialog fragmentDialog = new ProgressFragmentDialog();
                fragmentDialog.setMessage("Logging out...");
                fragmentDialog.setCancelable(false);
                fragmentDialog.show(getSupportFragmentManager(), LOGOUT_FRAGMENT_TAG);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (m_title != null) {
            outState.putString(SAVEINSTANCE_TITLE, m_title.toString());
        }
        if(m_subtitle != null) {
            outState.putString(SAVEINSTANCE_SUBTITLE, m_subtitle.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        // MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        // If the nav drawer is open, hide action items related to the content view

        // If needed add stuff here

        // boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        // menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        // WORKAROUND
        /*
         * Following code replaces commented version (ABS Incompatibility) Remove when API Target > 11 and no ABS
         * anymore
         */
        // if (mDrawerToggle.onOptionsItemSelected(item)) {
        // return true;
        // }
        if (item.getItemId() == android.R.id.home) {
            m_drawerDelegate.toggleDrawer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }
    
    @Override
    public void setTitle(CharSequence title) {
        m_title = title;
        m_subtitle = null;
        if (!m_drawerDelegate.isDrawerOpen()) {
            setActionBarTitles(m_title, m_subtitle);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the delegate
        m_drawerDelegate.onConfigurationChanged(newConfig);
    }

    @Override
    public Loader<Object> onCreateLoader(int type, Bundle arg1) {
        switch (type) {
            case R.id.loader_drawermenu_profileimage:
                return new ProfileImageLoader(this);
            case R.id.loader_drawermenu_notificationCounters:
                return new NotificationLoader(this);
            case R.id.loader_drawermenu_status:
                return new StatusLoader(this);
            case R.id.loader_main_upgradePush:
                return new UpgradeAndPushLoader(this);
            case R.id.loader_main_logout:
                return new LogoutLoader(this);
            default:
                throw new IllegalArgumentException("Loader type not found: " + type);
        }
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object result) {
        if (result != null) {
            switch (loader.getId()) {
                case R.id.loader_drawermenu_profileimage:
                    PersistantModel.getInstance().setCachedOwnProfilePictureURLDirectory((String) result);
                    // Refresh
                    m_drawerDelegate.setupOrUpdateDrawerHeader();
                    return;
                case R.id.loader_drawermenu_notificationCounters:
                    m_drawerDelegate.updateCounts((UpdateBean) result);
                    return;
                case R.id.loader_drawermenu_status:
                    @SuppressWarnings("unchecked")
                    Pair<OnlineStatus, String> p = (Pair<OnlineStatus, String>) result;
                    m_drawerDelegate.updateStatus(p);
                    return;
                case R.id.loader_main_logout:
                    Boolean succeeded = (Boolean) result;
                    if (succeeded) {
                        logoutComplete();
                    } else {
                        FragmentManager manager = getSupportFragmentManager();
                        Fragment frag = manager.findFragmentByTag(LOGOUT_FRAGMENT_TAG);
                        if (frag != null) {
                            FragmentTransaction t = manager.beginTransaction();
                            t.detach(frag);
                            t.commitAllowingStateLoss(); // What the user never saw...
                        }
                        Toast.makeText(this, "Could not logout. Check your internet connection", Toast.LENGTH_LONG)
                                .show();
                    }
                    break;
                case R.id.loader_main_upgradePush:
                    // NOP
                    break;
                default:
                    throw new IllegalArgumentException("Loader type not found: " + loader.getId());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> arg0) { }

    void launchFragment(Fragment f) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container_frame, f).commit();
    }

    void setActionBarTitles(CharSequence title, CharSequence subtitle){
        ActionBar bar = getSupportActionBar();
        bar.setTitle(title);
        bar.setSubtitle(subtitle);
    }
    
    void backupActionBarTitles(){
        ActionBar bar = getSupportActionBar();
        m_title = bar.getTitle();
        m_subtitle = bar.getSubtitle();
    }
    
    void restoreActionBarTitles(){
        setActionBarTitles(m_title, m_subtitle);
    }

    private void setupDrawer() {
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        m_drawerDelegate = new DrawerDelegate(this);
    }

    private void registerLogoutReceiver() {
        m_logoutBroadcastReceiver = new LocalBroadcastReceiver(new IBroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                logoutInProgress = true;
                Log.i(TAG, "Received logout broadcast. Launching loader to logout.");
                getSupportLoaderManager().destroyLoader(R.id.loader_main_upgradePush);
                getSupportLoaderManager().restartLoader(R.id.loader_main_logout, new Bundle(), MainActivity.this);
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(m_logoutBroadcastReceiver,
                new IntentFilter(BroadcastTypes.BROADCAST_LOGOUT));
    }

    private void logoutComplete() {
        // Deactivate the update service if present
        UpdateService.unregisterUpdateService();
        // Delete Database
        deleteDatabase(DBHelper.DATABASE_NAME);
        // Delete preferences
        PreferenceUtility.getPreferences().edit().clear().commit();
    
        // Good bye
        Log.i(TAG, "Logout actions completed. Goodbye");
        PersistantModel.getInstance().handleWrongCredentials(this);
    }

    private void triggerUpdate() {
        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(R.id.loader_drawermenu_notificationCounters, null, this);
        lm.initLoader(R.id.loader_drawermenu_status, null, this);
    }

}
