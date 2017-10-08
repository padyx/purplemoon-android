package ch.defiant.purplesky.activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.AbstractTabbedActivity;
import ch.defiant.purplesky.beans.DetailedUser;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.NullUser;
import ch.defiant.purplesky.broadcast.BroadcastTypes;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.dialogs.CreatePostitDialogFragment;
import ch.defiant.purplesky.enums.profile.ProfileStatus;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;
import ch.defiant.purplesky.fragments.gallery.PictureFolderGridViewFragment;
import ch.defiant.purplesky.fragments.profile.UserStatsFragment;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.StringUtility;

/**
 * @author Patrick Bänziger
 */
public class DisplayProfileActivity extends AbstractTabbedActivity implements LoaderManager.LoaderCallbacks<Holder<DetailedUser>>{

    private String m_userId;
    private boolean m_isOwnProfile;
    private MinimalUser m_userBean;

    private static final String TAG = DisplayProfileActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        m_userId = getIntent().getStringExtra(ArgumentConstants.ARG_USERID);
        super.onCreate(savedInstanceState);

        if (m_userId == null) {
            throw new IllegalStateException("No userId!");
        }
        if (CompareUtility.equals(m_userId, PersistantModel.getInstance().getUserProfileId())) {
            m_isOwnProfile = true;
        }
        if (m_userBean == null) {
            getOrUpdateData();
        }
    }

    private static int[] titleRes = {
        R.string.Stats,
        R.string.Pictures,
        R.string.Messages // Optional
    };

    @Override
    protected Fragment createItemAtPosition(int i) {
        Bundle args = new Bundle();
        args.putString(ArgumentConstants.ARG_USERID, m_userId);
        Fragment fragment;
        if(i==0){
            fragment = new UserStatsFragment();
        } else if (i==1) {
            fragment = new PictureFolderGridViewFragment();
        } else { // TODO PBN Optional
            fragment = new ConversationFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected CharSequence getTitleAtPosition(int i) {
        return getString(titleRes[i]);
    }

    @Override
    public int getFragmentCount() {
        return 3;
    }

    @Override
    public int getSelfNavigationIndex() {
        return NAVIGATION_INDEX_INVALID;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            if (m_userBean != null) {
                actionBar.setTitle(m_userBean.getUsername());
            } else {
                actionBar.setTitle(R.string.Profile);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (m_userBean != null) {
            outState.putSerializable(ArgumentConstants.ARG_USER, m_userBean);
        }
    }

    private void getOrUpdateData() {
        getLoaderManager().restartLoader(R.id.loader_profile_main, null, this);
    }

    /**
     * Set the userbean for them to update themselves
     *
     * @param userbean
     *            Bean to send to children fragments
     */
    private void updateWithUserBean(MinimalUser userbean) {
        m_userBean = userbean;

        // Notify
        Intent i = new Intent(BroadcastTypes.BROADCAST_USERBEAN_RETRIEVED);
        i.putExtra(ArgumentConstants.ARG_USER, userbean);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

        final String username = userbean.getUsername();
        if (userbean instanceof DetailedUser && StringUtility.hasText(username)) {
            setActionBarTitle(username, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.displayprofile_menu, menu);
        if (m_isOwnProfile) {
            // Remove post it menu
            menu.removeItem(R.id.postit_menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // FIXME PBN
            case R.id.postit_menu: {
                CreatePostitDialogFragment fragment = new CreatePostitDialogFragment();
                Bundle args = new Bundle();
                args.putString(CreatePostitDialogFragment.ARGUMENT_RECIPIENT_PROFILEID_URI, m_userId);
                fragment.setArguments(args);
                fragment.show(getFragmentManager(), "GIVEPOSTIT");
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Holder<DetailedUser>> onCreateLoader(int arg0, Bundle params) {
        setProgressBarIndeterminateVisibility(true);

        return new SimpleAsyncLoader<Holder<DetailedUser>>(this, R.id.loader_profile_main) {

            @Override
            public Holder<DetailedUser> loadInBackground() {
                if (m_userId == null) {
                    Log.e(TAG, "Got no user id to load");
                    return new Holder<>(new Exception("Got no user id to load"));
                }

                try {
                    DetailedUser user = PurpleSkyApplication.get().getUserService().getDetailedUser(m_userId);
                    return new Holder<>(user);
                } catch (Exception e) {
                    return new Holder<>(e);
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Holder<DetailedUser>> arg0, Holder<DetailedUser> result) {
            if ( result == null || result.getContainedObject() == null  || result.getException() != null) {
                NullUser nullUser = new NullUser();
                if (result.getException() instanceof IOException) {
                    nullUser.setError(getString(R.string.ErrorNetworkGeneric));
                }
                updateWithUserBean(nullUser);
            } else {
                DetailedUser user = result.getContainedObject();
                if (user.getProfileStatus() != ProfileStatus.OK) {
                    NullUser nullUser = new NullUser();
                    nullUser.setError(user.getProfileStatus().getLocalizedString());
                    updateWithUserBean(nullUser);
                } else {
                    updateWithUserBean(user);
                }
            }
            setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onLoaderReset(Loader<Holder<DetailedUser>> arg0) {
    }
}
