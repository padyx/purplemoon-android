package ch.defiant.purplesky.fragments.photovote;

import android.os.Bundle;
import android.view.View;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.fragments.AbstractTabbedFragment;

public class PhotoVoteTabbedFragment extends AbstractTabbedFragment {

    private static final String TAG_GIVEN = "Given";
    private static final String TAG_RECEIVED = "Received";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addTab(R.string.Vote_Action, "Vote", PhotoVoteFragment.class);
        addTab(R.string.Received, TAG_RECEIVED, PhotoVoteListFragment.class);
        addTab(R.string.Given, TAG_GIVEN, PhotoVoteListFragment.class);
    }

    @Override
    protected Bundle createFragmentArgumentBundle(String tag) {
        Bundle b = new Bundle();
        if (TAG_GIVEN.equals(tag)) {
            b.putBoolean(PhotoVoteListFragment.EXTRA_BOOL_SHOWGIVEN, true);
        }
        return b;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSherlockActivity().getSupportActionBar().setTitle(R.string.PhotoVotes);
    }
}
