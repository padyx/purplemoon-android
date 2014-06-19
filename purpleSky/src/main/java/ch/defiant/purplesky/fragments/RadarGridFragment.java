package ch.defiant.purplesky.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.constants.ResultConstants;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.db.IBundleDao;
import ch.defiant.purplesky.dialogs.RadarOptionsDialogFragment;
import ch.defiant.purplesky.listeners.OpenUserProfileListener;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.NVLUtility;

/**
 * Radar fragment
 * @author Patrick Bänziger
 */
public class RadarGridFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Holder<List<MinimalUser>>>, ActionBar.OnNavigationListener {

    private static final String BUNDLESTORE_OWNER = "radargridfragment";
    private UserSearchOptions options;
    private GridView gridview;

    private enum SearchMode {
        LOCATION_PROFILE_CURRENT("Around profile location"),
        LOCATION_DEVICE("Around device location"); // FIXME I18N
        private final String string;

        private SearchMode (String localizedString){
            this.string = localizedString;
//            this.stringRes = localizedString;
        }
    }

    @Inject
    protected IBundleDao dao;

    private GridAdapter adapter;
    private SearchMode searchMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

//        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//        actionBar.setDisplayShowTitleEnabled(false);
//        ModeAdapter adapter = new ModeAdapter(getSherlockActivity(), android.R.layout.two_line_list_item);
//        for (SearchMode m: SearchMode.values()){
//            adapter.add(m);
//        }
//        actionBar.setListNavigationCallbacks(adapter, this);

        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.radar_grid_fragment, container, false);

        this.adapter = new GridAdapter(getActivity(), 0);
        gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(this.adapter);
        gridview.setOnItemClickListener(new OpenUserProfileListener(getSherlockActivity()));
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveSearchSelections();
    }

    private void saveSearchSelections() {
        if (options != null){
            Bundle b = options.toBundle();
            dao.store(b, BUNDLESTORE_OWNER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreSearchSelections();
    }

    private void restoreSearchSelections() {
        Bundle bundle = dao.restore(BUNDLESTORE_OWNER);
        options = UserSearchOptions.from(bundle);
    }

    @Override
    public void onDetach() {
        getSherlockActivity().getSupportActionBar().removeAllTabs();
        getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getSherlockActivity().setProgressBarIndeterminateVisibility(false);
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreSearchSelections();
        getLoaderManager().initLoader(R.id.loader_radar_main, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.radar_grid_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.radar_grid_menu_filter){
            showDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = new RadarOptionsDialogFragment();
        Bundle b = new Bundle();
        b.putSerializable(ArgumentConstants.ARG_SERIALIZABLEOBJECT, options);
        newFragment.setArguments(b);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(ft, "radarOptionsDialog");
    }

    @Override
    public Loader<Holder<List<MinimalUser>>> onCreateLoader(int arg0, Bundle arg1) {
        getSherlockActivity().setProgressBarIndeterminateVisibility(true);

        return new SimpleAsyncLoader<Holder<List<MinimalUser>>>(getSherlockActivity(), R.id.loader_radar_main) {

            @Override
            public Holder<List<MinimalUser>> loadInBackground() {
                UserSearchOptions opts = options;
                if(opts == null){
                    opts = new UserSearchOptions();
                }
//                if (m_location != null) {
//                    opts.setLocation(new Pair<Double, Double>(m_location.getLatitude(), m_location.getLongitude()));
//                }
                opts.setUserClass(MinimalUser.class);
                opts.setNumber(100);
//                SearchCriteriaTranslator.setSearchCriteria(opts, m_filterValues);
                // If there is no filter set, require them to be online within last month...
                opts.setLastOnline(NVLUtility.nvl(opts.getLastOnline(), UserSearchOptions.LastOnline.PAST_MONTH));
                opts.setSearchOrder(PurplemoonAPIConstantsV1.UserSearchOrder.DISTANCE);

                try {
                    List<MinimalUser> result = apiAdapter.searchUser(opts);
                    return new Holder<List<MinimalUser>>(result);
                } catch (Exception e) {
                    return new Holder<List<MinimalUser>>(e);
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Holder<List<MinimalUser>>> loader, Holder<List<MinimalUser>> data) {
        SherlockFragmentActivity sherlockActivity = getSherlockActivity();
        if(sherlockActivity != null) {
            sherlockActivity.setProgressBarIndeterminateVisibility(false);
        }
        adapter.clear();
        for (MinimalUser user : data.getContainedObject()) {
            adapter.add(user);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Holder<List<MinimalUser>>> loader) {  }

    private class GridAdapter extends ArrayAdapter<MinimalUser> {

        private class ViewHolder {
            ImageView imgView;
            TextView usernameView;
            TextView ageView;
            View statusView;
        }

        public GridAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder ;
            View view;
            if(convertView != null){
                holder = (ViewHolder) convertView.getTag();
                view = convertView;
            } else {
                view = getActivity().getLayoutInflater().inflate(R.layout.user_avatar, parent, false);
                holder = new ViewHolder();
                view.setTag(holder);
                holder.ageView = (TextView)view.findViewById(R.id.age);
                holder.usernameView = ((TextView)view.findViewById(R.id.username));
                holder.imgView = (ImageView) view.findViewById(R.id.imageView);
                holder.statusView = view.findViewById(R.id.user_avatar_onlineStatus);
            }

            final MinimalUser user = getItem(position);

            holder.statusView.
                    setBackgroundColor(getResources().getColor(
                            user.getOnlineStatus().getColor()
                    ));

            holder.usernameView.setText(user.getUsername());
            holder.ageView.setText(user.getAge().toString());

            URL url = UserService.getUserPreviewPictureUrl(user, UserService.UserPreviewPictureSize.getPictureSizeForDpi(100, getResources()));

            if(url == null) {
                Picasso.with(getActivity()).load(R.drawable.person).into(holder.imgView);
            } else {
                Picasso.with(getActivity()).load(url.toString()).into(holder.imgView);
            }

            return view;
        }
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        return false;
    }

    private class ModeAdapter extends ArrayAdapter<SearchMode> {

        public ModeAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SearchMode mode = getItem(position);

            View view = getSherlockActivity().getLayoutInflater().inflate(android.R.layout.two_line_list_item, parent, false);
            ((TextView)view.findViewById(android.R.id.text1)).setText(mode.string);

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        UserSearchOptions opts = (UserSearchOptions) data.getSerializableExtra(ResultConstants.GENERIC);
        options = opts;
        getLoaderManager().restartLoader(R.id.loader_radar_main,null,this);
    }

}
