package ch.defiant.purplesky.activities;

import android.app.Fragment;
import android.os.Bundle;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.AbstractTabbedActivity;
import ch.defiant.purplesky.fragments.postit.PostitFragment;

/**
 * @author Chakotay
 */
public class PostitTabbedActivity extends AbstractTabbedActivity {

    private final int[] titleRes = {
        R.string.Received,
        R.string.Given
    };

    @Override
    protected Fragment createItemAtPosition(int i) {
        PostitFragment fragment = new PostitFragment();
        if(i==1){
            Bundle bundle = new Bundle();
            bundle.putBoolean(PostitFragment.ARGUMENT_BOOLEAN_SHOW_GIVEN, true);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected CharSequence getTitleAtPosition(int i) {
        return getString(titleRes[i]);
    }

    @Override
    public int getFragmentCount() {
        return 2;
    }

    @Override
    public int getSelfNavigationIndex() {
        return 2;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.Postits), null);
    }
}
