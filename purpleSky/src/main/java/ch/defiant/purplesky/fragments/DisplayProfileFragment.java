package ch.defiant.purplesky.fragments;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.DetailedUser;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.NullUser;
import ch.defiant.purplesky.broadcast.BroadcastTypes;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.dialogs.CreatePostitDialogFragment;
import ch.defiant.purplesky.dialogs.IAlertDialogFragmentResponder;
import ch.defiant.purplesky.enums.ProfileStatus;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;
import ch.defiant.purplesky.fragments.gallery.PictureFolderGridViewFragment;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.StringUtility;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class DisplayProfileFragment extends AbstractTabbedFragment
        implements IAlertDialogFragmentResponder, LoaderCallbacks<Holder<DetailedUser>> {

    private static final String FRAGMENT_TAG_PICTURES = "pictures";
    private static final String FRAGMENT_TAG_STATS = "stats";
    private static final String FRAGMENT_TAG_MESSAGES = "messages";
    private String m_userProfileId;
    private boolean m_isOwnProfile;
    private MinimalUser m_userBean;

    private static final String TAG = DisplayProfileFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getSherlockActivity().setTitle(R.string.Profile);
        m_userProfileId = getArguments().getString(ArgumentConstants.ARG_USERID);
        if (m_userProfileId == null) {
            Log.w(TAG, "Got no profileId");
            return;
        }
        if (CompareUtility.equals(m_userProfileId, PersistantModel.getInstance().getUserProfileId())) {
            m_isOwnProfile = true;
        }

        // setup action bar for tabs
        addTab(R.string.Stats, FRAGMENT_TAG_STATS, UserStatsFragment.class);
        addTab(R.string.Pictures, FRAGMENT_TAG_PICTURES, PictureFolderGridViewFragment.class);
        if (!m_isOwnProfile) {
            addTab(R.string.Messages, FRAGMENT_TAG_MESSAGES, ConversationFragment.class);
        }

        if (savedInstanceState != null) {
            m_userBean = (MinimalUser) savedInstanceState.getSerializable(ArgumentConstants.ARG_USER);
        }

        if (m_userBean == null) {
            getOrUpdateData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (m_userBean != null) {
            actionBar.setTitle(m_userBean.getUsername());
        }
        else {
            actionBar.setTitle(R.string.Profile);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (m_userBean != null) {
            outState.putSerializable(ArgumentConstants.ARG_USER, m_userBean);
        }
    }

    @Override
    protected Bundle createFragmentArgumentBundle(String tag) {
        Bundle bundle = new Bundle();
        bundle.putString(ArgumentConstants.ARG_USERID, m_userProfileId);
        if (m_userBean != null) {
            bundle.putSerializable(ArgumentConstants.ARG_USER, m_userBean);
        }
        return bundle;
    }

    @Override
    public void doPositiveAlertClick(int dialogId) {
    }

    @Override
    public void doNegativeAlertClick(int dialogId) {
        switch (dialogId) {
            case ConversationFragment.DIALOG_DISCARD_ON_EXIT:
                // TODO
                // finish();
                break;
        }
    }

    @Override
    public void doNeutralAlertClick(int dialogId) {
    }

    // TODO
    // @Override
    // public void onBackPressed() {
    // Fragment frag = getPagerAdapter().findFragmentByTag(FRAGMENT_TAG_MESSAGES);
    // if (frag instanceof ConversationFragment) {
    // ConversationFragment convFrag = (ConversationFragment) frag;
    // if (convFrag.checkDiscardOnExit()) {
    // finish();
    // }
    // } else if (frag == null) {
    // finish();
    // }
    // }

    private void getOrUpdateData() {
        getSherlockActivity().getSupportLoaderManager().restartLoader(R.id.loader_profile_main, null, this);
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
        LocalBroadcastManager.getInstance(getSherlockActivity()).sendBroadcast(i);

        final String username = userbean.getUsername();
        if (userbean instanceof DetailedUser && StringUtility.hasText(username)) {
            getSherlockActivity().setTitle(username);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.displayprofile_menu, menu);
        if (m_isOwnProfile) {
            // Remove post it menu
            menu.removeItem(R.id.postit_menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.postit_menu: {
                CreatePostitDialogFragment fragment = new CreatePostitDialogFragment();
                Bundle args = new Bundle();
                args.putString(CreatePostitDialogFragment.ARGUMENT_RECIPIENT_PROFILEID_URI, m_userProfileId);
                fragment.setArguments(args);
                fragment.show(getSherlockActivity().getSupportFragmentManager(), "GIVEPOSTIT");
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Holder<DetailedUser>> onCreateLoader(int arg0, Bundle params) {
        getSherlockActivity().setProgressBarIndeterminateVisibility(true);

        return new SimpleAsyncLoader<Holder<DetailedUser>>(getSherlockActivity(), R.id.loader_profile_main) {

            @Override
            public Holder<DetailedUser> loadInBackground() {
                if (m_userProfileId == null) {
                    Log.e(TAG, "Got no user id to load");
                    return new Holder<DetailedUser>(new Exception("Got no user id to load"));
                }

                try {
                    DetailedUser user = PurpleSkyApplication.get().getUserService().getDetailedUser(m_userProfileId);
                    return new Holder<DetailedUser>(user);
                } catch (Exception e) {
                    return new Holder<DetailedUser>(e);
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Holder<DetailedUser>> arg0, Holder<DetailedUser> result) {
        if (getSherlockActivity() != null) {
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
            getSherlockActivity().setProgressBarIndeterminateVisibility(false);
        }

    }

    @Override
    public void onLoaderReset(Loader<Holder<DetailedUser>> arg0) {
    }

}
