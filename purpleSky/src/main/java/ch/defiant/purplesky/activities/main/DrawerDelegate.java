package ch.defiant.purplesky.activities.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Pair;
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
import ch.defiant.purplesky.activities.SettingActivity;
import ch.defiant.purplesky.activities.main.MainActivity.NavigationDrawerEntries;
import ch.defiant.purplesky.beans.UpdateBean;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.core.UserService.UserPreviewPictureSize;
import ch.defiant.purplesky.dialogs.OnlineStatusDialogFragment;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.fragments.ChatListFragment;
import ch.defiant.purplesky.fragments.DisplayProfileFragment;
import ch.defiant.purplesky.fragments.FavoritesFragment;
import ch.defiant.purplesky.fragments.MultiUploadFragment;
import ch.defiant.purplesky.fragments.RadarGridFragment;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteTabbedFragment;
import ch.defiant.purplesky.fragments.postit.PostitTabbedFragment;
import ch.defiant.purplesky.fragments.usersearch.UserSearchTabbedFragment;
import ch.defiant.purplesky.fragments.visits.VisitorTabbedFragment;
import ch.defiant.purplesky.util.LayoutUtility;
import ch.defiant.purplesky.util.StringUtility;

/**
 * Delegate handling drawer and navigation-related tasks.
 * @author Chakotay
 *
 */
class DrawerDelegate {
    
    private class OwnProfileListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            DisplayProfileFragment f = new DisplayProfileFragment();
            Bundle b = new Bundle();
            b.putString(ArgumentConstants.ARG_USERID, PersistantModel.getInstance().getUserProfileId());
            f.setArguments(b);
            clearBackstackAndLaunch(f);
        }
    }
    private class ChangeStatusListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            new OnlineStatusDialogFragment().show(m_activity.getSupportFragmentManager(), "status");
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
    private final MainActivity m_activity;
    
    private ActionBarDrawerToggle m_drawerToggle;
    private DrawerLayout m_drawerLayout;
    private List<DrawerItem> m_drawerTitles;
    private ListView m_drawerList;
    private TextView m_onlineStatusLbl;


    public DrawerDelegate(MainActivity a){
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

    void onConfigurationChanged(Configuration config){
        m_drawerToggle.onConfigurationChanged(config);
    }

    void postCreate(){
        // Sync the toggle state after onRestoreInstanceState has occurred.
        m_drawerToggle.syncState();
        if (isDrawerOpen()) {
            m_activity.setActionBarTitles(m_drawerTitle, null);
        } else {
            m_activity.restoreActionBarTitles();
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

    void selectItem(NavigationDrawerEntries startFragment, Bundle args) {
        // update the main content by replacing fragments
    
        Fragment f = null;
        switch (startFragment) {
            case LAUNCH_CHATLIST:
                f = new ChatListFragment();
                break;
            case LAUNCH_RADAR:
                f = new RadarGridFragment();
            break;
            case LAUNCH_POSTIT:
                f = new PostitTabbedFragment();
                break;
            case LAUNCH_VISITORS:
                f = new VisitorTabbedFragment();
                break;
            case LAUNCH_FAVORITES:
                f = new FavoritesFragment();
                break;
            case LAUNCH_USERSEARCH:
                f = new UserSearchTabbedFragment();
                break;
            case LAUNCH_PHOTOVOTE:
                f = new PhotoVoteTabbedFragment();
                break;
            case LAUNCH_UPLOAD:
                f = new MultiUploadFragment();
                break;
            case LAUNCH_SETTINGS:
                m_activity.startActivity(new Intent(m_activity, SettingActivity.class));
                return;
                // TODO Replace by fragment implementation
            default:
                throw new IllegalArgumentException("Could not find right fragment");
        }
        // Retaining will give stupid errors with UI stuff, sadly.
        f.setRetainInstance(false);
        f.setArguments(args);
        int position = startFragment.ordinal();
        m_drawerList.setItemChecked(position, true);
        m_activity.setActionBarTitles(m_activity.getString(m_drawerTitles.get(position).titleRes), null);
        m_activity.backupActionBarTitles();
    
        // Hide any progress bar that might be visible in the actionbar
        m_activity.setProgressBarIndeterminateVisibility(false);
    
        // When we select something from the navigation drawer, the back stack is discarded
        clearBackstackAndLaunch(f);
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
            m_activity.getSupportLoaderManager().restartLoader(R.id.loader_drawermenu_profileimage, null, m_activity); // Restart, if it
            // failed once
        } else {
            UserPreviewPictureSize size = UserService.UserPreviewPictureSize.getPictureForPx(imgSize);
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

    private void initialize() {
        setupDrawerContent();
        setupDrawerLayout();
        setupOrUpdateDrawerHeader();
        setupDrawerToggle();
    }

    private void setupDrawerContent() {
        m_drawerTitles = populateDrawer();
    }

    private void setupDrawerToggle() {
        m_drawerToggle = new ActionBarDrawerToggle(
                m_activity, /* host Activity */
                m_drawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.Accessibility_NavigationOpen, /* "open drawer" description for accessibility */
                R.string.Accessibility_NavigationClose /* "close drawer" description for accessibility */
                ) {


            @Override
            public void onDrawerClosed(View view) {
                m_activity.restoreActionBarTitles();
                ActivityCompat.invalidateOptionsMenu(m_activity); // creates call to onPrepareOptionsMenu()
            }


            @Override
            public void onDrawerOpened(View drawerView) {
                m_activity.backupActionBarTitles();
                m_activity.setActionBarTitles(m_drawerTitle, null);
                // creates call to onPrepareOptionsMenu()
                ActivityCompat.invalidateOptionsMenu(m_activity); 

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

    private List<DrawerItem> populateDrawer() {
        ArrayList<DrawerItem> l = new ArrayList<DrawerItem>();
        l.add(new DrawerItem(R.string.Messages, R.drawable.content_email, NavigationDrawerEventType.MESSAGE));
        l.add(new DrawerItem(R.string.Radar, R.drawable.radar, null)); // TODO Replace icon
        l.add(new DrawerItem(R.string.Postits, R.drawable.postit_bw, NavigationDrawerEventType.POSTIT));
        l.add(new DrawerItem(R.string.ProfileVisits, R.drawable.visits_1step, NavigationDrawerEventType.VISIT));
        l.add(new DrawerItem(R.string.Favorites_Online_, R.drawable.rating_important,
                NavigationDrawerEventType.FAVORITES));
        l.add(new DrawerItem(R.string.SearchUser_ShortHome, R.drawable.action_search, null));
        l.add(new DrawerItem(R.string.PhotoVotes, R.drawable.picture_rate, null));
        l.add(new DrawerItem(R.string.Upload, R.drawable.content_new_picture, null));
        l.add(new DrawerItem(R.string.Settings, R.drawable.action_settings, null));
        return l;
    }

    private void selectItem(int position) {
        selectItem(NavigationDrawerEntries.values()[position], new Bundle());
    }

    private void clearBackstackAndLaunch(Fragment f) {
        m_activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        m_activity.launchFragment(f);
        closeDrawer();
    }

}