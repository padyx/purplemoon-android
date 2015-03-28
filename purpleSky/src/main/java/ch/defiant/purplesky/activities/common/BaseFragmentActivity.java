package ch.defiant.purplesky.activities.common;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;

/**
 * @author Patrick Bänziger
 */
public abstract class BaseFragmentActivity extends Activity {

    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;
    @Inject
    protected IConversationAdapter conversationAdapter;

    protected static final int NAVIGATION_INDEX_INVALID = -1;

    private static final String SAVEINSTANCE_TITLE = "title";
    private static final String SAVEINSTANCE_SUBTITLE = "subtitle";
    public static final String EXTRA_LAUNCH_OPTION = "launchOption";
    public static final String EXTRA_LAUNCH_ARGS = "launchArgs";

    private CharSequence m_subTitle;
    private CharSequence m_title;
    private DrawerDelegate m_drawerDelegate;
    private PurpleSkyApplication.UpdateListener m_listener;
    private Handler m_handler;

    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;
    private static final String TAG = BaseFragmentActivity.class.getSimpleName();

    /**
     * Whether selecting the same navigation item again will reactivate it.
     * Default implementation returns false.
     * @return
     */
    public boolean isSelfSelectionReloads() {
        return false;
    }

    /**
     * Available Entries in the navigation drawer.
     */
    public enum NavigationDrawerEntries {
        LAUNCH_CHATLIST,
        LAUNCH_RADAR,
        LAUNCH_POSTIT,
        LAUNCH_VISITORS,
        LAUNCH_FAVORITES,
        LAUNCH_USERSEARCH,
        LAUNCH_PHOTOVOTE,
        LAUNCH_UPLOAD,
        LAUNCH_SETTINGS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        PurpleSkyApplication.get().inject(this);
        m_handler = new Handler();

        setContentView(R.layout.fragment_container_layout);

        if (savedInstanceState != null) {
            m_title = savedInstanceState.getString(SAVEINSTANCE_TITLE);
            m_subTitle = savedInstanceState.getString(SAVEINSTANCE_SUBTITLE);
        }

        // Number updates for the drawer
        m_listener = new PurpleSkyApplication.UpdateListener() {
            @Override
            public void update(NavigationDrawerEventType t, int count) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(m_drawerDelegate != null) {
                            m_drawerDelegate.updateEventCounts();
                        }
                    }
                });
            }
        };

        PurpleSkyApplication.get().setListener(m_listener);
        // triggerUpdate();
        // registerLogoutReceiver();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(isShowNavigationDrawer()) {
            setupDrawer();
            m_drawerDelegate.postCreate();
        }
        // Upgrade actions that don't require blocking stuff
        //getLoaderManager().initLoader(R.id.loader_main_upgradePush, null, this);

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            Log.w(TAG, "No view with ID main_content to fade in.");
        }
    }

    public void setActionBarTitle(CharSequence title, CharSequence subTitle){
        m_title = title;
        m_subTitle = subTitle;

        if(getActionBar() != null){
            ActionBar actionBar = getActionBar();
            actionBar.setTitle(m_title);
            actionBar.setSubtitle(m_subTitle);
        }
    }

    public void setActionBarTitlesFromActivity(){
        setActionBarTitle(m_title, m_subTitle);
    }

    private void setupDrawer() {
        if (getActionBar() != null) {
            // enable ActionBar app icon to behave as action to toggle nav drawer
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }
        m_drawerDelegate = new DrawerDelegate(this);
    }

    public void setDrawerEnabled(boolean enabled){
        m_drawerDelegate.setDrawerEnabled(enabled);
    }

    public abstract int getSelfNavigationIndex();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isShowNavigationDrawer() && m_drawerDelegate.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(isShowNavigationDrawer()) {
            // Pass any configuration change to the delegate
            m_drawerDelegate.onConfigurationChanged(newConfig);
        }
    }

    Handler getHandler(){
        return m_handler;
    }

    /**
     * Whether the navigation drawer shall be shown
     * @return
     */
    protected boolean isShowNavigationDrawer(){
        return true;
    }

}
