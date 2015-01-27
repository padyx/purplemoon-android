package ch.defiant.purplesky.core;

import ch.defiant.purplesky.activities.AboutActivity;
import ch.defiant.purplesky.activities.ChatListActivity;
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
import ch.defiant.purplesky.api.conversation.internal.ConversationModule;
import ch.defiant.purplesky.api.gallery.internal.GalleryModule;
import ch.defiant.purplesky.api.internal.APIModule;
import ch.defiant.purplesky.api.photovotes.internal.PhotoVoteModule;
import ch.defiant.purplesky.api.postits.internal.PostitModule;
import ch.defiant.purplesky.api.promotions.internal.PromotionModule;
import ch.defiant.purplesky.api.report.internal.ReportModule;
import ch.defiant.purplesky.api.visit.internal.VisitModule;
import ch.defiant.purplesky.core.internal.CoreModule;
import ch.defiant.purplesky.db.internal.DatabaseModule;
import ch.defiant.purplesky.dialogs.CreatePostitDialogFragment;
import ch.defiant.purplesky.dialogs.OnlineStatusDialogFragment;
import ch.defiant.purplesky.dialogs.RadarOptionsDialogFragment;
import ch.defiant.purplesky.dialogs.UploadPhotoDialogFragment;
import ch.defiant.purplesky.fragments.BaseFragment;
import ch.defiant.purplesky.fragments.BaseListFragment;
import ch.defiant.purplesky.fragments.ChatListFragment;
import ch.defiant.purplesky.fragments.FavoritesFragment;
import ch.defiant.purplesky.fragments.MultiUploadFragment;
import ch.defiant.purplesky.fragments.RadarGridFragment;
import ch.defiant.purplesky.fragments.ReportUserFragment;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;
import ch.defiant.purplesky.fragments.event.EventFragment;
import ch.defiant.purplesky.fragments.gallery.PictureFolderGridViewFragment;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteFragment;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteListFragment;
import ch.defiant.purplesky.fragments.postit.PostitFragment;
import ch.defiant.purplesky.fragments.profile.UserStatsFragment;
import ch.defiant.purplesky.fragments.usersearch.UserSearchResultsFragment;
import ch.defiant.purplesky.fragments.usersearch.UsernameSearchFragment;
import ch.defiant.purplesky.fragments.visits.VisitorFragment;
import dagger.Module;

/**
 * Main module for injections.
 * @author Patrick Bänziger
 */
@Module(
    includes = {
            APIModule.class,
            CoreModule.class,
            DatabaseModule.class,
            ReportModule.class,
            PostitModule.class,
            GalleryModule.class,
            PhotoVoteModule.class,
            VisitModule.class,
            ConversationModule.class,
            PromotionModule.class
    },
    injects = {
            PurpleSkyApplication.class,

            // Activities
            LoginActivity.class,
            RadarActivity.class,
            VisitorTabbedActivity.class,
            PostitTabbedActivity.class,
            UserSearchTabbedActivity.class,
            PhotoVoteTabbedActivity.class,
            MultiUploadPictureActivity.class,
            FavoritesActivity.class,
            DisplayProfileActivity.class,
            ReportActivity.class,
            SettingFragmentActivity.class,
            UserSearchResultsActivity.class,
            AboutActivity.class,
            PictureGridViewActivity.class,
            LightboxActivity.class,
            EventActivity.class,


            // Base or abstract classes
            BaseListFragment.class,
            BaseFragment.class,

            ChatListActivity.class,

            // Services
            UserService.class,

            // Fragments
            PostitFragment.class,
            VisitorFragment.class,
            UsernameSearchFragment.class,

            UserStatsFragment.class,
            PictureFolderGridViewFragment.class,

            PhotoVoteFragment.class,
            PhotoVoteListFragment.class,
            ChatListFragment.class,

            FavoritesFragment.class,
            ConversationFragment.class,
            UserSearchResultsFragment.class,

            OnlineStatusDialogFragment.class,
            RadarGridFragment.class,
            RadarOptionsDialogFragment.class,
            CreatePostitDialogFragment.class,
            UploadPhotoDialogFragment.class,
            ReportUserFragment.class,
            MultiUploadFragment.class,
            EventFragment.class
    }
)
public class PurpleSkyModule {

    private final PurpleSkyApplication app;

    public PurpleSkyModule(PurpleSkyApplication app) {
        this.app = app;
    }

}
