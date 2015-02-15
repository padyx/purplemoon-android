package ch.defiant.purplesky.activities.common;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.DisplayProfileActivity;
import ch.defiant.purplesky.activities.FavoritesActivity;
import ch.defiant.purplesky.activities.MultiUploadPictureActivity;
import ch.defiant.purplesky.activities.PhotoVoteTabbedActivity;
import ch.defiant.purplesky.activities.PostitTabbedActivity;
import ch.defiant.purplesky.activities.RadarActivity;
import ch.defiant.purplesky.activities.SettingFragmentActivity;
import ch.defiant.purplesky.activities.UserSearchTabbedActivity;
import ch.defiant.purplesky.activities.VisitorTabbedActivity;
import ch.defiant.purplesky.activities.chatlist.ChatListActivity;
import ch.defiant.purplesky.beans.UpdateBean;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserService.UserPreviewPictureSize;
import ch.defiant.purplesky.dialogs.OnlineStatusDialogFragment;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.loaders.NotificationLoader;
import ch.defiant.purplesky.loaders.ProfileImageLoader;
import ch.defiant.purplesky.loaders.StatusLoader;
import ch.defiant.purplesky.loaders.UpgradeAndPushLoader;
import ch.defiant.purplesky.util.LayoutUtility;
import ch.defiant.purplesky.util.StringUtility;

/**
 * Delegate handling drawer and navigation-related tasks.
 * @author Patrick Bänziger
 *
 */
class DrawerDelegate implements LoaderManager.LoaderCallbacks<Object>{

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;

    private class OwnProfileListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(m_activity, DisplayProfileActivity.class);
            intent.putExtra(ArgumentConstants.ARG_USERID, PersistantModel.getInstance().getUserProfileId());
            m_activity.startActivity(intent);
        }
    }
    private class ChangeStatusListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            new OnlineStatusDialogFragment().show(m_activity.getFragmentManager(), "status");
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Title to be shown when the drawer is open.
     */
    private final CharSequence m_drawerTitle;
    private final BaseFragmentActivity m_activity;

    private ActionBarDrawerToggle m_drawerToggle;
    private DrawerLayout m_drawerLayout;
    private List<DrawerItem> m_drawerTitles = new ArrayList<DrawerItem>();
    private ListView m_drawerList;
    private TextView m_onlineStatusLbl;


    public DrawerDelegate(BaseFragmentActivity a){
        m_activity = a;
        m_drawerTitle = m_activity.getTitle();
        initialize();
    }

    public boolean isDrawerOpen(){
        return m_drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void toggleDrawer(){
        if(isDrawerOpen()){
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    public void openDrawer(){
        m_drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer(){
        m_drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void setDrawerEnabled(boolean enabled){
        m_drawerToggle.setDrawerIndicatorEnabled(enabled);
    }

    void onConfigurationChanged(Configuration config){
        m_drawerToggle.onConfigurationChanged(config);
    }

    void postCreate(){
        // Sync the toggle state after onRestoreInstanceState has occurred.
        m_drawerToggle.syncState();
        if (isDrawerOpen()) {
            m_activity.setActionBarTitle(m_drawerTitle, null);
        } else {
            m_activity.setActionBarTitlesFromActivity();
        }
    }

    void updateStatus(Pair<OnlineStatus, String> p) {
        if (StringUtility.isNotNullOrEmpty(p.second)) {
            m_onlineStatusLbl.setText(R.string.Custom);
        } else {
            m_onlineStatusLbl.setText(p.first.getLocalizedString(m_activity));
        }
    }

    void updateCounts(UpdateBean b) {
        PurpleSkyApplication c = PurpleSkyApplication.get();
        c.setEventCount(NavigationDrawerEventType.FAVORITES, b.getFavoritesCount());
        c.setEventCount(NavigationDrawerEventType.MESSAGE, b.getMessagesCount());
        c.setEventCount(NavigationDrawerEventType.POSTIT, b.getPostItCount());
        c.setEventCount(NavigationDrawerEventType.VISIT, b.getVisitCount());
    }

    void selectItem(BaseFragmentActivity.NavigationDrawerEntries startFragment, Bundle args) {
        // update the main content by replacing fragments

        Class<? extends Activity> f = null;
        switch (startFragment) {
            case LAUNCH_CHATLIST:
                f = ChatListActivity.class;
                break;
            case LAUNCH_RADAR:
                f = RadarActivity.class;
                break;
            case LAUNCH_POSTIT:
                f = PostitTabbedActivity.class;
                break;
            case LAUNCH_VISITORS:
                f = VisitorTabbedActivity.class;
                break;
            case LAUNCH_FAVORITES:
                f = FavoritesActivity.class;
                break;
            case LAUNCH_USERSEARCH:
                f = UserSearchTabbedActivity.class;
                break;
            case LAUNCH_PHOTOVOTE:
                f = PhotoVoteTabbedActivity.class;
                break;
            case LAUNCH_UPLOAD:
                f = MultiUploadPictureActivity.class;
                break;
            case LAUNCH_SETTINGS:
                f = SettingFragmentActivity.class;
                break;
            default:
                throw new IllegalArgumentException("Could not find right fragment");
        }
        int position = startFragment.ordinal();
        m_drawerList.setItemChecked(position, true);
        m_activity.setActionBarTitle(m_activity.getString(m_drawerTitles.get(position).titleRes), null);

        // Hide any progress bar that might be visible in the actionbar
        m_activity.setProgressBarIndeterminateVisibility(false);

        // When we select something from the navigation drawer, clear the task (discard any deep navigation done here)
        Intent intent = new Intent(m_activity, f);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        m_activity.startActivity(intent, args);
        m_activity.finish();
    }

    void selectFirstItem() {
        selectItem(0);
    }

    /**
     * Initializes or updates the header of the drawer containing the user profile picture, etc.
     */
    void setupOrUpdateDrawerHeader() {
        int imgSize = LayoutUtility.dpToPx(m_activity.getResources(), 50);
        m_activity.findViewById(R.id.drawer_layout_profileImgV).setOnClickListener(new OwnProfileListener());
        m_activity.findViewById(R.id.drawer_layout_statusLbl).setOnClickListener(new ChangeStatusListener());
        ImageView profileImgV = (ImageView) m_activity.findViewById(R.id.drawer_layout_profileImgV);
        m_onlineStatusLbl = (TextView) m_activity.findViewById(R.id.drawer_layout_statusLbl);

        String url = PersistantModel.getInstance().getCachedOwnProfilePictureURLDirectory();
        if (url == null) {
            Picasso.with(m_activity).load(R.drawable.social_person).fit().into(profileImgV);
            startProfileImageLoad();
        } else {
            UserPreviewPictureSize size = UserPreviewPictureSize.getPictureForPx(imgSize);
            Picasso.with(m_activity).load(url + size.getAPIValue()).error(R.drawable.social_person)
                    .placeholder(R.drawable.social_person).resize(imgSize, imgSize).centerCrop().into(profileImgV);
        }
    }

    /**
     * Updates the event counters for the navigation drawer entries.
     */
    void updateEventCounts(){
        ((DrawerAdapter)m_drawerList.getAdapter()).notifyDataSetChanged();
    }


    boolean onOptionsItemSelected(MenuItem item) {
        return m_drawerToggle.onOptionsItemSelected(item);
    }

    private void initialize() {
        setupDrawerContent();
        setupDrawerLayout();
        setupOrUpdateDrawerHeader();
        setupDrawerToggle();

        triggerUpdate();
    }

    private void setupDrawerContent() {
        populateDrawer();
    }

    private void setupDrawerToggle() {
        m_drawerToggle = new ActionBarDrawerToggle(
                m_activity, /* host Activity */
                m_drawerLayout, /* DrawerLayout object */
                R.string.Accessibility_NavigationOpen, /* "open drawer" description for accessibility */
                R.string.Accessibility_NavigationClose /* "close drawer" description for accessibility */
        ) {


            @Override
            public void onDrawerClosed(View view) {
                m_activity.setActionBarTitlesFromActivity();
                ActivityCompat.invalidateOptionsMenu(m_activity); // creates call to onPrepareOptionsMenu()
                m_activity.invalidateOptionsMenu();
            }


            @Override
            public void onDrawerOpened(View drawerView) {
                m_activity.getActionBar().setTitle(m_drawerTitle);
                m_activity.getActionBar().setSubtitle(null);
                // creates call to onPrepareOptionsMenu()
                m_activity.invalidateOptionsMenu();

                // Set up the Non-List Content (which could update)
                setupOrUpdateDrawerHeader();
            }

        };
        m_drawerLayout.setDrawerListener(m_drawerToggle);
    }

    private void setupDrawerLayout(){
        m_drawerList = (ListView) m_activity.findViewById(R.id.drawer_layout_list);
        m_drawerLayout = (DrawerLayout) m_activity.findViewById(R.id.drawer_layout);
        // set a custom shadow that overlays the main content when the drawer opens
        m_drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        m_drawerList.setAdapter(new DrawerAdapter(m_activity, R.layout.drawer_item, m_drawerTitles));
        m_drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private void populateDrawer() {
        int selectedIndex = m_activity.getSelfNavigationIndex();

        addDrawerItem(R.string.Messages, R.drawable.content_email, NavigationDrawerEventType.MESSAGE, selectedIndex , R.drawable.rounded_rect_red);
        addDrawerItem(R.string.Radar, R.drawable.radar, null, selectedIndex, R.drawable.rounded_button_blue); // TODO Replace icon
        addDrawerItem(R.string.Postits, R.drawable.postit_black, NavigationDrawerEventType.POSTIT, selectedIndex, R.drawable.rounded_button_blue);
        addDrawerItem(R.string.ProfileVisits, R.drawable.visits_1step, NavigationDrawerEventType.VISIT, selectedIndex, R.drawable.rounded_button_blue);
        addDrawerItem(R.string.Favorites_Online_, R.drawable.rating_important, NavigationDrawerEventType.FAVORITES, selectedIndex, R.drawable.rounded_button_blue);
        addDrawerItem(R.string.SearchUser_ShortHome, R.drawable.action_search, null, selectedIndex, R.drawable.rounded_button_blue);
        addDrawerItem(R.string.PhotoVotes, R.drawable.picture_rate, null, selectedIndex, R.drawable.rounded_button_blue);
        addDrawerItem(R.string.Upload, R.drawable.content_new_picture, null, selectedIndex, R.drawable.rounded_button_blue);
        addDrawerItem(R.string.Settings, R.drawable.action_settings, null, selectedIndex, R.drawable.rounded_button_blue);
    }

    private void addDrawerItem(
            @StringRes int title,
            @DrawableRes int drawableRes,
            NavigationDrawerEventType eventType,
            int selectionIndex,
            @DrawableRes int eventCountRes){
        boolean selected = selectionIndex == m_drawerTitles.size();
        m_drawerTitles.add(new DrawerItem(title, drawableRes, eventType, selected, eventCountRes));
    }

    private void selectItem(final int position) {
        if (position == m_activity.getSelfNavigationIndex() && !m_activity.isSelfSelectionReloads()) {
            closeDrawer();
            return;
        }

        // launch the target Activity after a short delay, to allow the close animation to play
        m_activity.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                selectItem(BaseFragmentActivity.NavigationDrawerEntries.values()[position], new Bundle());
            }
        }, NAVDRAWER_LAUNCH_DELAY);

        // change the active item on the list so the user can see the item changed
        // FIXME PBN IMPLEMENT
        // setSelectedNavDrawerItem(itemId);
        // fade out the main content
        View mainContent = m_activity.findViewById(R.id.drawer_layout);
        if (mainContent != null) {
            mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }
        closeDrawer();
    }

    private void clearBackstackAndLaunch(Fragment f) {
        closeDrawer();
    }

    void startProfileImageLoad(){
        m_activity.getLoaderManager().restartLoader(R.id.loader_drawermenu_profileimage, null, this);
    }

    private void triggerUpdate() {
        LoaderManager lm = m_activity.getLoaderManager();
        lm.initLoader(R.id.loader_drawermenu_notificationCounters, null, this);
        lm.initLoader(R.id.loader_drawermenu_status, null, this);
    }

    @Override
    public Loader<Object> onCreateLoader(int type, Bundle arg1) {
        switch (type) {
            case R.id.loader_drawermenu_profileimage:
                return new ProfileImageLoader(m_activity);
            case R.id.loader_drawermenu_notificationCounters:
                return new NotificationLoader(m_activity, m_activity.apiAdapter, m_activity.conversationAdapter);
            case R.id.loader_drawermenu_status:
                return new StatusLoader(m_activity, m_activity.apiAdapter);
            case R.id.loader_main_upgradePush:
                return new UpgradeAndPushLoader(m_activity, m_activity.apiAdapter);
            case R.id.loader_main_logout:
                // FIXME Rremove and add it in the settings
                // return new LogoutLoader(this, apiAdapter);
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
                    setupOrUpdateDrawerHeader();
                    return;
                case R.id.loader_drawermenu_notificationCounters:
                    updateCounts((UpdateBean) result);
                    return;
                case R.id.loader_drawermenu_status:
                    @SuppressWarnings("unchecked")
                    Pair<OnlineStatus, String> p = (Pair<OnlineStatus, String>) result;
                    updateStatus(p);
                    return;
                case R.id.loader_main_logout:
//                    Boolean succeeded = (Boolean) result;
//                    if (succeeded) {
//                        logoutComplete();
//                    } else {
//                        FragmentManager manager = getFragmentManager();
//                        Fragment frag = manager.findFragmentByTag(LOGOUT_FRAGMENT_TAG);
//                        if (frag != null) {
//                            FragmentTransaction t = manager.beginTransaction();
//                            t.detach(frag);
//                            t.commitAllowingStateLoss(); // What the user never saw...
//                        }
//                        Toast.makeText(this, "Could not logout. Check your internet connection", Toast.LENGTH_LONG)
//                                .show();
//                    }
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

}