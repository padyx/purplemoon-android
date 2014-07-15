package ch.defiant.purplesky.core;

import ch.defiant.purplesky.activities.LoginActivity;
import ch.defiant.purplesky.activities.SettingActivity;
import ch.defiant.purplesky.activities.main.MainActivity;
import ch.defiant.purplesky.api.internal.APIModule;
import ch.defiant.purplesky.core.internal.CoreModule;
import ch.defiant.purplesky.db.internal.DatabaseModule;
import ch.defiant.purplesky.dialogs.CreatePostitDialogFragment;
import ch.defiant.purplesky.dialogs.OnlineStatusDialogFragment;
import ch.defiant.purplesky.dialogs.RadarOptionsDialogFragment;
import ch.defiant.purplesky.dialogs.UploadPhotoDialogFragment;
import ch.defiant.purplesky.fragments.AbstractTabbedFragment;
import ch.defiant.purplesky.fragments.BaseFragment;
import ch.defiant.purplesky.fragments.BaseListFragment;
import ch.defiant.purplesky.fragments.ChatListFragment;
import ch.defiant.purplesky.fragments.FavoritesFragment;
import ch.defiant.purplesky.fragments.RadarGridFragment;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;
import ch.defiant.purplesky.fragments.gallery.PictureFolderGridViewFragment;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteFragment;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteListFragment;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteTabbedFragment;
import ch.defiant.purplesky.fragments.postit.PostitFragment;
import ch.defiant.purplesky.fragments.postit.PostitTabbedFragment;
import ch.defiant.purplesky.fragments.profile.DisplayProfileFragment;
import ch.defiant.purplesky.fragments.profile.UserStatsFragment;
import ch.defiant.purplesky.fragments.usersearch.UserSearchResultsFragment;
import ch.defiant.purplesky.fragments.usersearch.UserSearchTabbedFragment;
import ch.defiant.purplesky.fragments.usersearch.UsernameSearchFragment;
import ch.defiant.purplesky.fragments.visits.VisitorFragment;
import ch.defiant.purplesky.fragments.visits.VisitorTabbedFragment;
import dagger.Module;

/**
 * Main module for injections.
 * @author Patrick Bänziger
 */
@Module(
    includes = {
            APIModule.class,
            CoreModule.class,
            DatabaseModule.class
    },
    injects = {
            PurpleSkyApplication.class,
            MainActivity.class,
            LoginActivity.class,
            SettingActivity.class,

            // Base or abstract classes
            BaseListFragment.class,
            BaseFragment.class,
            AbstractTabbedFragment.class,

            // Services
            UserService.class,

            // Fragments
            PostitTabbedFragment.class,
            PostitFragment.class,

            VisitorTabbedFragment.class,
            VisitorFragment.class,

            UserSearchTabbedFragment.class,
            UsernameSearchFragment.class,

            DisplayProfileFragment.class,
            UserStatsFragment.class,

            PhotoVoteTabbedFragment.class,
            PhotoVoteFragment.class,
            PhotoVoteListFragment.class,

            FavoritesFragment.class,
            ConversationFragment.class,
            UserSearchResultsFragment.class,

            PictureFolderGridViewFragment.class,
            ChatListFragment.class,
            OnlineStatusDialogFragment.class,
            RadarGridFragment.class,
            RadarOptionsDialogFragment.class,
            CreatePostitDialogFragment.class,
            UploadPhotoDialogFragment.class
    }
)
public class PurpleSkyModule {

    private final PurpleSkyApplication app;

    public PurpleSkyModule(PurpleSkyApplication app) {
        this.app = app;
    }

}
