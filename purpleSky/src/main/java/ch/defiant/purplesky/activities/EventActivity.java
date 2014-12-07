package ch.defiant.purplesky.activities;

import android.os.Bundle;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;

public class EventActivity extends BaseFragmentActivity {

    @Override
    public int getSelfNavigationIndex() {
        return NavigationDrawerEntries.LAUNCH_EVENTS.ordinal();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_event);
    }
}
