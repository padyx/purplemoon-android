package ch.defiant.purplesky.activities;

import android.os.Bundle;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;

/**
 * @author Chakotay
 */
public class RadarActivity extends BaseFragmentActivity {

    @Override
    public int getSelfNavigationIndex() {
        return 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_radar);
    }
}
