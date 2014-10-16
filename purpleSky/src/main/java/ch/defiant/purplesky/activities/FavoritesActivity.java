package ch.defiant.purplesky.activities;

import android.os.Bundle;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;

/**
 * @author Chakotay
 */
public class FavoritesActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_favorite);
    }

    @Override
    public int getSelfNavigationIndex() {
        return 4;
    }
}
