package ch.defiant.purplesky.fragments.visits;

import android.os.Bundle;
import android.view.View;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.fragments.AbstractTabbedFragment;
import ch.defiant.purplesky.util.CompareUtility;

public class VisitorTabbedFragment extends AbstractTabbedFragment {

    static final String FRAGMENT_TAG_VISITORS = "tab_visitors";
    static final String FRAGMENT_TAG_MYVISITS = "tab_myvisits";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addTab(R.string.Visitors, FRAGMENT_TAG_VISITORS, VisitorFragment.class);
        addTab(R.string.MyVisits, FRAGMENT_TAG_MYVISITS, VisitorFragment.class);
    }

    @Override
    protected Bundle createFragmentArgumentBundle(String tag) {
        Bundle b = new Bundle();
        b.putBoolean(VisitorFragment.ARGUMENT_BOOLEAN_SHOWMYVISITS, false);

        if (CompareUtility.equals(tag, FRAGMENT_TAG_MYVISITS)) {
            b.putBoolean(VisitorFragment.ARGUMENT_BOOLEAN_SHOWMYVISITS, true);
        }

        return b;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSherlockActivity().getSupportActionBar().setTitle(R.string.ProfileVisits);
    }

}
