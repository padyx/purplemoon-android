package ch.defiant.purplesky.fragments;

import android.os.Bundle;
import android.view.View;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.util.CompareUtility;

public class PostitTabbedFragment extends AbstractTabbedFragment {

    private static final String FRAGMENT_TAG_GIVEN = "tab_given";
    private static final String FRAGMENT_TAG_RECEIVED = "tab_received";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addTab(R.string.Received, FRAGMENT_TAG_RECEIVED, PostitFragment.class);
        addTab(R.string.Given, FRAGMENT_TAG_GIVEN, PostitFragment.class);
    }

    // TODO Perhaps add refresh option?

    @Override
    protected Bundle createFragmentArgumentBundle(String tag) {
        Bundle b = new Bundle();
        b.putBoolean(PostitFragment.ARGUMENT_BOOLEAN_SHOW_GIVEN, false);

        if (CompareUtility.equals(tag, FRAGMENT_TAG_GIVEN)) {
            b.putBoolean(PostitFragment.ARGUMENT_BOOLEAN_SHOW_GIVEN, true);
        }

        return b;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSherlockActivity().getSupportActionBar().setTitle(R.string.Postits);
    }

}
