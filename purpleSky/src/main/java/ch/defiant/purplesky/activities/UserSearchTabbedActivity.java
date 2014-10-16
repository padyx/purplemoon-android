package ch.defiant.purplesky.activities;

import android.app.Fragment;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.AbstractTabbedActivity;
import ch.defiant.purplesky.fragments.SimpleUserSearchFragment;
import ch.defiant.purplesky.fragments.usersearch.UsernameSearchFragment;

/**
 * @author Chakotay
 */
public class UserSearchTabbedActivity extends AbstractTabbedActivity {

    private final int[] titleRes = {
            R.string.Search,
            R.string.Username
    };

    @Override
    protected Fragment createItemAtPosition(int i) {
        if(i==0){
            return new SimpleUserSearchFragment();
        } else {
            return new UsernameSearchFragment();
        }
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
        return 5;
    }
}
