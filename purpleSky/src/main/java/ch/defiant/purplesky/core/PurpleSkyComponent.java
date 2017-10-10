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
import ch.defiant.purplesky.dialogs.EnterPasswordDialogFragment;
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
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * @author Patrick Bänziger
 */

@Component(modules =
        {
                PurpleSkyModule.class,
                PurpleSkyApplicationModule.class,
                AndroidInjectionModule.class,
                AndroidSupportInjectionModule.class
        }
)
public interface PurpleSkyComponent extends AndroidInjector<PurpleSkyApplication> {

    void inject(PurpleSkyApplication application);

    // Activities
    void inject(LoginActivity activity);

    void inject(VisitorTabbedActivity activity);

    void inject(PostitTabbedActivity activity);

    void inject(UserSearchTabbedActivity activity);

    void inject(PhotoVoteTabbedActivity activity);

    void inject(MultiUploadPictureActivity activity);

    void inject(FavoritesActivity activity);

    void inject(DisplayProfileActivity activity);

    void inject(ReportActivity activity);

    void inject(SettingFragmentActivity activity);

    void inject(UserSearchResultsActivity activity);

    void inject(AboutActivity activity);

    void inject(PictureGridViewActivity activity);

    void inject(LightboxActivity activity);

    void inject(EventActivity activity);

    // Base or abstract classes
    void inject(BaseListFragment activity);

    void inject(BaseFragment activity);

    void inject(ChatListActivity activity);

    void inject(ConversationActivity activity);

    // Services
    void inject(UserService activity);

    // Fragments
    void inject(PostitFragment fragment);

    void inject(VisitorFragment fragment);

    void inject(UsernameSearchFragment fragment);

    void inject(RadarGridFragment fragment);

    void inject(UserStatsFragment fragment);

    void inject(PictureFolderGridViewFragment fragment);

    void inject(PhotoVoteFragment fragment);

    void inject(PhotoVoteListFragment fragment);

    void inject(ChatListFragment fragment);

    void inject(FavoritesFragment fragment);

    void inject(ConversationFragment fragment);

    void inject(UserSearchResultsFragment fragment);

    void inject(OnlineStatusDialogFragment activity);

    void inject(RadarActivity activity);

    void inject(RadarOptionsDialogFragment fragment);

    void inject(CreatePostitDialogFragment fragment);

    void inject(UploadPhotoDialogFragment fragment);

    void inject(ReportUserFragment fragment);

    void inject(EnterPasswordDialogFragment fragment);

    void inject(MultiUploadFragment fragment);

    void inject(EventFragment fragment);

}
