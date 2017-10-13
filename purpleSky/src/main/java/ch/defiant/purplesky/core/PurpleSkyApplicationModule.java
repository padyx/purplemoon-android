package ch.defiant.purplesky.core;

import ch.defiant.purplesky.activities.AboutActivity;
import ch.defiant.purplesky.activities.DisplayProfileActivity;
import ch.defiant.purplesky.activities.EventActivity;
import ch.defiant.purplesky.activities.FavoritesActivity;
import ch.defiant.purplesky.activities.LightboxActivity;
import ch.defiant.purplesky.activities.LoginActivity;
import ch.defiant.purplesky.activities.MultiUploadPictureActivity;
import ch.defiant.purplesky.activities.PhotoVoteTabbedActivity;
import ch.defiant.purplesky.activities.PictureGridViewActivity;
import ch.defiant.purplesky.activities.PostitTabbedActivity;
import ch.defiant.purplesky.activities.RadarActivity;
import ch.defiant.purplesky.activities.ReportActivity;
import ch.defiant.purplesky.activities.SettingFragmentActivity;
import ch.defiant.purplesky.activities.UserSearchResultsActivity;
import ch.defiant.purplesky.activities.UserSearchTabbedActivity;
import ch.defiant.purplesky.activities.VisitorTabbedActivity;
import ch.defiant.purplesky.activities.chatlist.ChatListActivity;
import ch.defiant.purplesky.activities.chatlist.ConversationActivity;
import ch.defiant.purplesky.dialogs.EnterPasswordDialogFragment;
import ch.defiant.purplesky.dialogs.RadarOptionsDialogFragment;
import ch.defiant.purplesky.fragments.ChatListFragment;
import ch.defiant.purplesky.fragments.FavoritesFragment;
import ch.defiant.purplesky.fragments.MultiUploadFragment;
import ch.defiant.purplesky.fragments.RadarGridFragment;
import ch.defiant.purplesky.fragments.ReportUserFragment;
import ch.defiant.purplesky.fragments.SettingsFragment;
import ch.defiant.purplesky.fragments.SimpleUserSearchFragment;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;
import ch.defiant.purplesky.fragments.event.EventFragment;
import ch.defiant.purplesky.fragments.gallery.GalleryPictureFragment;
import ch.defiant.purplesky.fragments.gallery.GallerySwipeFragment;
import ch.defiant.purplesky.fragments.gallery.PictureFolderGridViewFragment;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteFragment;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteListFragment;
import ch.defiant.purplesky.fragments.postit.PostitFragment;
import ch.defiant.purplesky.fragments.profile.UserStatsFragment;
import ch.defiant.purplesky.fragments.usersearch.UserSearchResultsFragment;
import ch.defiant.purplesky.fragments.usersearch.UsernameSearchFragment;
import ch.defiant.purplesky.fragments.visits.VisitorFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Module for dependency injection.
 *
 * For now, all fragments and activities have subcomponents and modules generated from this class.
 *
 * @author Patrick Bänziger
 */
@Module
@SuppressWarnings("unused")
public abstract class PurpleSkyApplicationModule {

   /*
    * About
    */
    @ContributesAndroidInjector
    abstract AboutActivity contributeAboutActivityInjector();

    /*
     * ChatList
     */
    @ContributesAndroidInjector
    abstract ChatListActivity contributeChatListActivityInjector();

    @ContributesAndroidInjector
    abstract ChatListFragment contributeChatListFragmentInjector();

    @ContributesAndroidInjector
    abstract ConversationActivity contributeConversationActivityInjector();

    @ContributesAndroidInjector
    abstract ConversationFragment contributeConversationFragmentInjector();

    /*
     * Display profile
     */
    @ContributesAndroidInjector
    abstract DisplayProfileActivity contributeDisplayProfileActivityInjector();

    @ContributesAndroidInjector
    abstract UserStatsFragment contributeUserStatsFragmentInjector();

    @ContributesAndroidInjector
    abstract PictureFolderGridViewFragment contributePictureFolderGridViewFragmentInjector();

    @ContributesAndroidInjector
    abstract PictureGridViewActivity contributePictureGridViewActivityInjector();

    @ContributesAndroidInjector
    abstract GalleryPictureFragment contributeGalleryPictureFragmentInjector();

    @ContributesAndroidInjector
    abstract LightboxActivity contributeLightboxActivityInjector();

    @ContributesAndroidInjector
    abstract GallerySwipeFragment contributeGallerySwipeFragmentInjector();

    @ContributesAndroidInjector
    abstract EnterPasswordDialogFragment contributeEnterPasswordDialogFragmentInjector();

    /*
     * Event
     */
    @ContributesAndroidInjector
    abstract EventActivity contributeEventActivityInjector();

    @ContributesAndroidInjector
    abstract EventFragment contributeEventFragmentInjector();

    /*
     * Favorites
     */
    @ContributesAndroidInjector
    abstract FavoritesActivity contributeFavoritesActivityInjector();

    @ContributesAndroidInjector
    abstract FavoritesFragment contributeFavoritesFragmentInjector();

   /*
    * Login
    */
    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivityInjector();

    /*
     * MultiUploadPicture
     */
    @ContributesAndroidInjector
    abstract MultiUploadPictureActivity contributeMultiUploadPictureActivityInjector();

    @ContributesAndroidInjector
    abstract MultiUploadFragment contributeMultiUploadFragmentInjector();

    /*
     * PhotoVote
     */
    @ContributesAndroidInjector
    abstract PhotoVoteTabbedActivity contributePhotoVoteTabbedActivityInjector();

    @ContributesAndroidInjector
    abstract PhotoVoteFragment contributePhotoVoteFragmentInjector();

    @ContributesAndroidInjector
    abstract PhotoVoteListFragment contributePhotoVoteListFragmentInjector();

    /*
     * Postit
     */
    @ContributesAndroidInjector
    abstract PostitTabbedActivity contributePostitTabbedActivityInjector();

    @ContributesAndroidInjector
    abstract PostitFragment contributePostitFragmentInjector();

    /*
    * Radar
    */
    @ContributesAndroidInjector
    abstract RadarActivity contributeRadarActivityInjector();

    @ContributesAndroidInjector
    abstract RadarGridFragment contributeRadarFragmentInjector();

    @ContributesAndroidInjector
    abstract RadarOptionsDialogFragment contributeRadarOptionsDialogFragmentInjector();

    /*
    * Report
    */
    @ContributesAndroidInjector
    abstract ReportActivity contributeReportActivityInjector();

    @ContributesAndroidInjector
    abstract ReportUserFragment contributeReportUserFragmentInjector();

   /*
    * Settings
    */
    @ContributesAndroidInjector
    abstract SettingFragmentActivity contributeSettingFragmentActivityInjector();

    @ContributesAndroidInjector
    abstract SettingsFragment contributeSettingsFragmentInjector();

    /*
     * User search
     */
    @ContributesAndroidInjector
    abstract UserSearchTabbedActivity contributeUserSearchTabbedActivityInjector();

    @ContributesAndroidInjector
    abstract SimpleUserSearchFragment contributeSimpleUserSearchFragmentInjector();

    @ContributesAndroidInjector
    abstract UsernameSearchFragment contributeUsernameSearchFragmentInjector();

    /*
     * User search results
     */
    @ContributesAndroidInjector
    abstract UserSearchResultsActivity contributeUserSearchResultsActivityInjector();

    @ContributesAndroidInjector
    abstract UserSearchResultsFragment contributeUserSearchResultsFragmentInjector();

    /*
     * Visitor
     */
    @ContributesAndroidInjector
    abstract VisitorTabbedActivity contributeVisitorTabbedActivityInjector();

    @ContributesAndroidInjector
    abstract VisitorFragment contributeVisitorFragmentInjector();



}
