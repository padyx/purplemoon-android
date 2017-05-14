package ch.defiant.purplesky.activities;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.AbstractTabbedActivity;
import ch.defiant.purplesky.broadcast.BroadcastTypes;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteFragment;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteListFragment;

/**
 * @author Patrick Bänziger
 */
public class PhotoVoteTabbedActivity extends AbstractTabbedActivity {

    private static final int GIVEN_POSITION = 1;

    private class PhotoVoteListener extends BroadcastReceiver {

        private final AtomicBoolean m_hasReceived = new AtomicBoolean();

        /**
         * Returns whether a photovote was given by the user in the meantime since the last check.
         *
         * @return Whether a photovote was given in the meantime.
         */
        public boolean getAndReset(){
            return m_hasReceived.getAndSet(false);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            m_hasReceived.set(true);
        }
    }

    private PhotoVoteListener m_broadcastReceiver;

    private final int[] titleRes = {
            R.string.Vote_Action,
            R.string.Given,
            R.string.Received
    };

    private WeakReference<PhotoVoteListFragment> m_givenFragment = new WeakReference<>(null);

    public boolean checkAndResetGivenPhotoVote(){
        return m_broadcastReceiver == null || m_broadcastReceiver.getAndReset();
    }

    @Override
    public void onResume() {
        super.onResume();
            m_broadcastReceiver = new PhotoVoteListener();
            LocalBroadcastManager.getInstance(this).
                    registerReceiver(m_broadcastReceiver, new IntentFilter(BroadcastTypes.BROADCAST_PHOTOVOTE));
    }

    @Override
    public void onPause() {
        super.onPause();
            LocalBroadcastManager.getInstance(this).unregisterReceiver(m_broadcastReceiver);
            m_broadcastReceiver = null;
    }

    @Override
    protected Fragment createItemAtPosition(int i) {
        if(i==0){
            return new PhotoVoteFragment();
        } else if (i==GIVEN_POSITION) {
            PhotoVoteListFragment fragment = new PhotoVoteListFragment();
            Bundle args = new Bundle();
            args.putBoolean(PhotoVoteListFragment.EXTRA_BOOL_SHOWGIVEN,true);
            fragment.setArguments(args);
            m_givenFragment = new WeakReference<>(fragment);
            return fragment;
        } else {
            return new PhotoVoteListFragment();
        }
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
        return 6;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.PhotoVotes), null);
    }

    @Override
    protected void tabChanging(int position) {
        if (position == GIVEN_POSITION){
            if(m_givenFragment != null){
                PhotoVoteListFragment fragment = m_givenFragment.get();
                if(fragment != null && fragment.isResumed() && checkAndResetGivenPhotoVote()){
                    //fragment.reloadData();
                }
            }
        }
    }
}
