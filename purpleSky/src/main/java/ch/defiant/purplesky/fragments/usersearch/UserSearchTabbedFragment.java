package ch.defiant.purplesky.fragments.usersearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.fragments.AbstractTabbedFragment;
import ch.defiant.purplesky.fragments.SimpleUserSearchFragment;

public class UserSearchTabbedFragment extends AbstractTabbedFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addTab(R.string.Search, "Simple", SimpleUserSearchFragment.class);
        addTab(R.string.Username, "Name", UsernameSearchFragment.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.Search);
        getActivity().getActionBar().setIcon(R.drawable.ic_launcher);
    }

    @Override
    protected Bundle createFragmentArgumentBundle(String tag) {
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_menu_search:
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(Intent.ACTION_SEARCH));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
