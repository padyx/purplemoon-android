package ch.defiant.purplesky.activities;

import android.os.Bundle;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;

/**
 * @author Patrick Bänziger
 */
public class ReportActivity extends BaseFragmentActivity {
    @Override
    public int getSelfNavigationIndex() {
        return NAVIGATION_INDEX_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_reportuser);
    }
}
