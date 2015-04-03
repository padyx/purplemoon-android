package ch.defiant.purplesky.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.AbstractTabbedActivity;
import ch.defiant.purplesky.fragments.SimpleUserSearchFragment;
import ch.defiant.purplesky.fragments.usersearch.UsernameSearchFragment;

/**
 * @author Patrick Bänziger
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_menu_search:
                Fragment f = getCurrentTabFragment();
                if(f instanceof ISearchUserFragment){
                    ((ISearchUserFragment) f).startSearch();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.Search), null);
    }

    @Override
    protected void tabChanging(int position) {
        super.tabChanging(position);
    }

    public static interface ISearchUserFragment {
        void startSearch();
    }
}
