package ch.defiant.purplesky.activities;

import android.app.Fragment;
import android.os.Bundle;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.AbstractTabbedActivity;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteFragment;
import ch.defiant.purplesky.fragments.photovote.PhotoVoteListFragment;

/**
 * @author Chakotay
 */
public class PhotoVoteTabbedActivity extends AbstractTabbedActivity {

    private final int[] titleRes = {
            R.string.Vote_Action,
            R.string.Given,
            R.string.Received
    };

    @Override
    protected Fragment createItemAtPosition(int i) {
        if(i==0){
            return new PhotoVoteFragment();
        } else if (i==1) {
            return new PhotoVoteListFragment();
        } else {
            PhotoVoteListFragment fragment = new PhotoVoteListFragment();
            Bundle args = new Bundle();
            args.putBoolean(PhotoVoteListFragment.EXTRA_BOOL_SHOWGIVEN,true);
            fragment.setArguments(args);
            return fragment;
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
}
