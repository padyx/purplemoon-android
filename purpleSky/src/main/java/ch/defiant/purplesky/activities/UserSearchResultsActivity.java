package ch.defiant.purplesky.activities;

import android.os.Bundle;
import android.util.Log;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.fragments.usersearch.UserSearchResultsFragment;

/**
 * @author Patrick Bänziger
 */
public class UserSearchResultsActivity extends BaseFragmentActivity {

    private static final String TAG = UserSearchResultsActivity.class.getSimpleName();

    public static final String EXTRA_SEARCHOBJ = "searchobj";
    public static final String EXTRA_SEARCHNAME = "searchname";
    private UserSearchOptions m_options;
    private String m_usernameSearch;

    @Override
    public int getSelfNavigationIndex() {
        return NAVIGATION_INDEX_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.Results), null);
        setContentView(R.layout.layout_usersearchresult);
        if (!hasOptions()) {
            Log.e(TAG, "Did not get user search objects, or username to search!");
            return;
        } else {
            m_options = (UserSearchOptions) getIntent().getSerializableExtra(EXTRA_SEARCHOBJ);
            m_usernameSearch = getIntent().getStringExtra(EXTRA_SEARCHNAME);
        }
        UserSearchResultsFragment f = (UserSearchResultsFragment) getFragmentManager().findFragmentById(R.id.fragment);
        if(m_usernameSearch != null) {
            f.startUsernameSearch(m_usernameSearch);
        } else {
            f.startSearch(m_options);
        }
    }

    private boolean hasOptions(){
        return getIntent().hasExtra(EXTRA_SEARCHOBJ) || getIntent().hasExtra(EXTRA_SEARCHNAME);
    }
}
