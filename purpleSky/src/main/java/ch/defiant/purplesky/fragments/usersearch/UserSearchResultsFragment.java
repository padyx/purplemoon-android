package ch.defiant.purplesky.fragments.usersearch;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.commonsware.cwac.endless.EndlessAdapter;

import java.util.ArrayList;
import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.adapters.UserSearchResultListAdapter;
import ch.defiant.purplesky.beans.LocationBean;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PreviewUser;
import ch.defiant.purplesky.beans.util.Pair;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.fragments.BaseFragment;
import ch.defiant.purplesky.listeners.OpenUserProfileListener;

public class UserSearchResultsFragment extends BaseFragment {

    public static final String EXTRA_SEARCHOBJ = "searchobj";
    public static final String EXTRA_SEARCHNAME = "searchname";
    public static final String TAG = UserSearchResultsFragment.class.getSimpleName();

    private static final String SAVEINSTANCE_DATA = "data";
    private static final int RESULT_SIZE = 100;
    private String m_usernameSearch;
    private UserSearchOptions m_options;
    private UserSearchResultListAdapter m_adapter;

    private EndlessResultAdapter m_endlessAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!hasOptions()) {
            Log.e(TAG, "Did not get user search objects, or username to search!");
            return;
        } else {
            m_options = (UserSearchOptions) getArguments().getSerializable(EXTRA_SEARCHOBJ);
            m_usernameSearch = getArguments().getString(EXTRA_SEARCHNAME);
        }

        m_adapter = new UserSearchResultListAdapter(getSherlockActivity());
        if(savedInstanceState != null){
            @SuppressWarnings("unchecked")
            List<MinimalUser> data = (List<MinimalUser>) savedInstanceState.getSerializable(SAVEINSTANCE_DATA);
            if(data != null){
                for (MinimalUser m : data) {
                    m_adapter.add(m);
                }
            }
        }

        if (m_options != null && m_options.getLocation() != null) {
            // To show distance, need the preview user for location
            m_options.setUserClass(PreviewUser.class);
            LocationBean locationBean = new LocationBean();
            Pair<Double, Double> loc = m_options.getLocation();
            locationBean.setLatitude(loc.getFirst());
            locationBean.setLongitude(loc.getSecond());
            m_adapter.setOwnLocation(locationBean);
        }
        m_endlessAdapter = new EndlessResultAdapter(getSherlockActivity(), m_adapter, R.layout.loading_listitem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView v = (ListView) inflater.inflate(R.layout.list_plain, null);
        v.setAdapter(m_endlessAdapter);
        v.setOnItemClickListener(new OpenUserProfileListener(getSherlockActivity()));

        return v;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final int size = m_adapter.getCount();
        ArrayList<MinimalUser> storedData = new ArrayList<MinimalUser>();
        for (int i = 0; i < size; i++) {
            storedData.add(m_adapter.getItem(i));
        }
        
        outState.putSerializable(SAVEINSTANCE_DATA, storedData);
    }

    @Override
    public void onResume() {
        super.onResume();
        getSherlockActivity().getSupportActionBar().setTitle(R.string.Results);
    }

    private boolean hasOptions() {
        return getArguments() != null && (getArguments().containsKey(EXTRA_SEARCHNAME) || getArguments().containsKey(EXTRA_SEARCHOBJ));
    }

    private boolean isSearchByName() {
        return m_usernameSearch != null;
    }
    
    private class EndlessResultAdapter extends EndlessAdapter {

        public EndlessResultAdapter(Context context, UserSearchResultListAdapter wrapped, int pendingResource) {
            super(context, wrapped, pendingResource);
        }

        private List<MinimalUser> m_data;

        @Override
        protected boolean cacheInBackground() throws Exception {
            if(m_options == null){
                UserSearchOptions opts = new UserSearchOptions();
                opts.setNumber(RESULT_SIZE);
                m_options = opts;
            }
            
            List<MinimalUser> result;
            if(isSearchByName()){
                result = apiAdapter.searchUserByName(m_usernameSearch, m_options);
            } else {
                result = apiAdapter.searchUser(m_options);
            }
            m_data = result;
            
            return false;
        }

        @Override
        protected void appendCachedData() {
            if (m_data != null) {
                for (MinimalUser user : m_data) {
                    m_adapter.add(user);
                }
            }
        }
    }
}
