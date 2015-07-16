package ch.defiant.purplesky.activities.common;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.util.Set;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.dialogs.IAlertDialogFragmentResponder;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;

/**
 * @author Patrick Bänziger
 */
public abstract class BaseFragmentActivity extends AppCompatActivity implements IAlertDialogFragmentResponder {

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
    private Toolbar m_actionbarToolbar;

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
        // Call request window feature _before_
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

    @Nullable
    public Toolbar getActionbarToolbar(){
        if(m_actionbarToolbar != null){
            return m_actionbarToolbar;
        } else {
            m_actionbarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if(m_actionbarToolbar != null) {
                setSupportActionBar(m_actionbarToolbar);
            }
        }
        return m_actionbarToolbar;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (toolbar != null) {
            m_actionbarToolbar = toolbar;
            setSupportActionBar(toolbar);
        }
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

    public void setActionBarTitle(CharSequence title){
        m_title = title;
        if(getActionbarToolbar() != null){
            Toolbar actionBar = getActionbarToolbar();
            actionBar.setTitle(m_title);
        }
    }

    public void setActionBarTitle(CharSequence title, CharSequence subTitle){
        m_title = title;
        m_subTitle = subTitle;

        if(getActionbarToolbar() != null){
            Toolbar actionBar = getActionbarToolbar();
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

    @Override
    public void doPositiveAlertClick(int dialogId) {
    }

    @Override
    public void doNegativeAlertClick(int dialogId) {
    }

    @Override
    public void doNeutralAlertClick(int dialogId) {
    }

    @Override
    public void doListSelectResult(int dialogId, Set<Integer> selected) {
    }

}
