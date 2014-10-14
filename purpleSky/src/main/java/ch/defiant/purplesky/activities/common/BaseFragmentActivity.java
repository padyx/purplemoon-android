package ch.defiant.purplesky.activities.common;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.core.PurpleSkyApplication;

/**
 * @author Chakotay
 */
public class BaseFragmentActivity extends Activity {

    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;

    private static final String SAVEINSTANCE_TITLE = "title";
    private static final String SAVEINSTANCE_SUBTITLE = "subtitle";
    private static final String EXTRA_LAUNCH_OPTION = "launchOption";
    private static final String EXTRA_LAUNCH_ARGS = "launchArgs";

    private CharSequence m_subTitle;
    private CharSequence m_title;
    private DrawerDelegate m_drawerDelegate;

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
        super.onCreate(savedInstanceState);
        PurpleSkyApplication.get().inject(this);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.fragment_container_layout);

        if (savedInstanceState != null) {
            m_title = savedInstanceState.getString(SAVEINSTANCE_TITLE);
            m_subTitle = savedInstanceState.getString(SAVEINSTANCE_SUBTITLE);
        }





        // Number updates for the drawer
        /*m_listener = new PurpleSkyApplication.UpdateListener() {
            @Override
            public void update(NavigationDrawerEventType t, int count) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        m_drawerDelegate.updateEventCounts();
                    }
                });
            }
        };*/

        //PurpleSkyApplication.get().setListener(m_listener);
        // triggerUpdate();
        // registerLogoutReceiver();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

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

        // Upgrade actions that don't require blocking stuff
        //getLoaderManager().initLoader(R.id.loader_main_upgradePush, null, this);
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
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        m_drawerDelegate = new DrawerDelegate(this);
    }

}
