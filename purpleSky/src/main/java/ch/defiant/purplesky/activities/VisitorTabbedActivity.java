package ch.defiant.purplesky.activities;

import android.app.Fragment;
import android.os.Bundle;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.AbstractTabbedActivity;
import ch.defiant.purplesky.fragments.visits.VisitorFragment;

/**
 * @author Chakotay
 */
public class VisitorTabbedActivity extends AbstractTabbedActivity {

    private int[] titleRes = {
            R.string.Visitors,
            R.string.MyVisits
    };

    @Override
    protected Fragment createItemAtPosition(int i) {
        VisitorFragment fragment = new VisitorFragment();
        if(i==1){
            Bundle bundle = new Bundle();
            bundle.putBoolean(VisitorFragment.ARGUMENT_BOOLEAN_SHOWMYVISITS, true);
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
        return 3;
    }
}
