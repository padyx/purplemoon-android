package ch.defiant.purplesky.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.constants.ResultConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.db.IBundleDao;
import ch.defiant.purplesky.dialogs.AlertDialogFragment;
import ch.defiant.purplesky.dialogs.RadarOptionsDialogFragment;
import ch.defiant.purplesky.listeners.OpenUserProfileListener;
import ch.defiant.purplesky.loaders.radar.GetAndUpdateProfilePositionLoader;
import ch.defiant.purplesky.loaders.radar.GetCurrentPurplemoonAddress;
import ch.defiant.purplesky.loaders.radar.RadarResultLoader;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.StringUtility;

/**
 * Radar fragment
 * @author Patrick Bänziger
 * @since 1.1.0
 */
public class RadarGridFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Holder<List<MinimalUser>>>,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener
{

    // TODO Handling that google play services are not available?

    private class GeocoderLoaderCallback implements  LoaderManager.LoaderCallbacks<Address>{

        @Override
        public Loader<Address> onCreateLoader(int id, Bundle args) {
            getSherlockActivity().setProgressBarIndeterminateVisibility(true);

            return new GetAndUpdateProfilePositionLoader(getActivity(), apiAdapter, currentLocation);
        }

        @Override
        public void onLoadFinished(Loader<Address> loader, Address data) {
            if(getSherlockActivity() != null) {
                getSherlockActivity().setProgressBarIndeterminateVisibility(false);

                if (data == null) {
                    // TODO Handle error
                    addressTextView.setText(getString(R.string.Unknown));
                } else {
                    SharedPreferences.Editor edit = PreferenceUtility.getPreferences().edit();
                    edit.putLong(PreferenceConstants.radarLastLocationUpdate, System.currentTimeMillis());
                    edit.commit();

                    currentAddress = data;
                    updateLocationDisplay();
                }
                getLoaderManager().destroyLoader(R.id.loader_profilePositionUpdate);
                getLoaderManager().restartLoader(R.id.loader_radar_main, null, RadarGridFragment.this);
            }
        }

        @Override
        public void onLoaderReset(Loader<Address> loader) {  }
    }

    private class PurplemoonLocationLoaderCallback implements LoaderManager.LoaderCallbacks<Address>{

        @Override
        public Loader<Address> onCreateLoader(int i, Bundle bundle) {
            return new GetCurrentPurplemoonAddress(getSherlockActivity(), apiAdapter);
        }

        @Override
        public void onLoadFinished(Loader<Address> loader, Address address) {
            if(address != null) {
                currentAddress = address;
            }
            if(getSherlockActivity() != null){
                String s;
                if(address != null && address.getMaxAddressLineIndex()>=0){
                    s = address.getAddressLine(0);
                } else {
                    s=getString(R.string.Unknown);
                }
                addressTextView.setText(s);
                getLoaderManager().destroyLoader(R.id.loader_profilePositionRetrieval);
            }
        }

        @Override
        public void onLoaderReset(Loader<Address> loader) { }
    }

    // Saving instance values keys
    private static final String STATE_ADDRESS = "address";
    private static final String STATE_LOCATION = "location";
    private static final String STATE_DATA = "data";

    // Constants
    private static final String TAG = RadarGridFragment.class.getSimpleName();
    private static final long OUTDATE_THRESHOLD_MS = 2*60*1000; // 2 Minutes
    private static final int REQUIRED_ACCURACY = 250;
    private static final String BUNDLESTORE_OWNER = "radargridfragment";
    private static final int REQUESTCODE_SEARCHOPTIONS_DIALOG = 0;

    @Inject
    protected IBundleDao dao;

    private TextView addressTextView;
    private GridAdapter adapter;

    // Logic
    private UserSearchOptions options;
    private boolean useLocation;
    private LocationClient locationClient;
    private Location currentLocation;
    private Address currentAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.radar_grid_fragment, container, false);
        addressTextView = (TextView) view.findViewById(R.id.radar_grid_fragment_addressText);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(this.adapter);
        gridview.setOnItemClickListener(new OpenUserProfileListener(getSherlockActivity()));
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveSearchSelections();
        removeLocationListener();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_ADDRESS, currentAddress);
        outState.putParcelable(STATE_LOCATION, currentLocation);
        ArrayList<MinimalUser> data = new ArrayList<MinimalUser>();
        for(int i=0; i<adapter.getCount(); i++){
            data.add(adapter.getItem(i));
        }
        outState.putSerializable(STATE_DATA, data);
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
        updateLocationDisplay();
        checkLocationPermission();
        // If we have no data - or it is outdated
        if(adapter.getCount() == 0 || (useLocation && isLocationOutdated())) {
            reload();
        }
    }

    private void updateLocationDisplay() {
        if (currentAddress != null && getActivity() != null) {
            List<String> parts = new ArrayList<String>();
            for(int i=0; i<=currentAddress.getMaxAddressLineIndex();  i++){
                parts.add(currentAddress.getAddressLine(i));
            }

            addressTextView.setText(StringUtility.join(", ", parts));
        }
        else {
            addressTextView.setText(getString(R.string.Unknown));
        }
    }

    private boolean isLocationOutdated() {
        if(currentLocation == null){
            if(BuildConfig.DEBUG){
                Log.d(TAG, "Outdated: No location yet.");
            }
            return true;
        }
        return (System.currentTimeMillis() - currentLocation.getTime()) > OUTDATE_THRESHOLD_MS;
    }

    private void attachLocationListener() {
        if(getSherlockActivity() != null){
            getSherlockActivity().setProgressBarIndeterminateVisibility(true);
        }
        if(locationClient == null) {
            locationClient = new LocationClient(getActivity(), this, this);
        }
        locationClient.connect();
    }

    private void removeLocationListener() {
        if(locationClient != null && locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
            locationClient.disconnect();
        }
        if(getSherlockActivity() != null){
            getSherlockActivity().setProgressBarIndeterminateVisibility(false);
        }
    }

    private void checkLocationPermission() {
        SharedPreferences prefs = PreferenceUtility.getPreferences();
        if(!prefs.contains(PreferenceConstants.radarLocationUpdateDialogShown)){
            AlertDialogFragment frag = AlertDialogFragment.newYesNoDialog(R.string.AutomaticLocationUpdates_Dialog_Title, R.string.AutomaticLocationUpdates_Dialog_Question, 0);
            frag.setTargetFragment(this, 0);
            frag.show(getFragmentManager(), "question");
        } else {
            useLocation = prefs.getBoolean(PreferenceConstants.radarAutomaticLocationUpdateEnabled, false);
        }
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
        this.adapter = new GridAdapter(getActivity(), 0);
        if (savedInstanceState != null) {
            this.currentAddress = savedInstanceState.getParcelable(STATE_ADDRESS);
            this.currentLocation = savedInstanceState.getParcelable(STATE_LOCATION);
            List<MinimalUser> data = (List<MinimalUser>) savedInstanceState.getSerializable(STATE_DATA);
            if(data != null){
                for(MinimalUser u:data ){
                   adapter.add(u);
                }
            }
        }
    }

    private void reload() {
        SharedPreferences prefs = PreferenceUtility.getPreferences();
        long lastOnlineUpdate = prefs.getLong(PreferenceConstants.radarLastLocationUpdate, 0L);
        long ageOnline = System.currentTimeMillis() - lastOnlineUpdate;

        if(useLocation && (isLocationOutdated() && ageOnline > OUTDATE_THRESHOLD_MS)){
            if(BuildConfig.DEBUG){
                Log.d(TAG, "Location is outdated. Starting retrieval");
            }
            attachLocationListener();
        } else {
            if(BuildConfig.DEBUG){
                Log.d(TAG, "Location still up-to-date or not configured to be used.");
            }
            if(currentAddress == null) {
                getLoaderManager().restartLoader(R.id.loader_profilePositionRetrieval, null, new PurplemoonLocationLoaderCallback());
            }
            getLoaderManager().restartLoader(R.id.loader_radar_main, null, this);
        }
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
        newFragment.setTargetFragment(this, REQUESTCODE_SEARCHOPTIONS_DIALOG);
        newFragment.show(ft, "radarOptionsDialog");
    }

    @Override
    public Loader<Holder<List<MinimalUser>>> onCreateLoader(int id, Bundle arg1) {
        getSherlockActivity().setProgressBarIndeterminateVisibility(true);
        return new RadarResultLoader(getSherlockActivity(), apiAdapter, options);
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

        getLoaderManager().destroyLoader(R.id.loader_radar_main);
    }

    @Override
    public void onLoaderReset(Loader<Holder<List<MinimalUser>>> loader) {  }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create();
        // Use low power accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        // Set the update interval to 0.5 seconds
        locationRequest.setInterval(500);
        // Set the fastest update interval to 0.25 second
        locationRequest.setFastestInterval(250);

        locationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {
        locationClient = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "Could not connect to retrieve location. Result "+connectionResult);
        Toast.makeText(getSherlockActivity(), getString(R.string.LocationNotFound), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if(location.getAccuracy() < REQUIRED_ACCURACY){
            if(BuildConfig.DEBUG){
                Log.d(TAG, "Location obtained "+location);
            }
            // Accurate enough
            removeLocationListener();
            getLoaderManager().restartLoader(R.id.loader_profilePositionUpdate, null, new GeocoderLoaderCallback());
        }
    }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUESTCODE_SEARCHOPTIONS_DIALOG:
                options = (UserSearchOptions) data.getSerializableExtra(ResultConstants.GENERIC);
                getLoaderManager().restartLoader(R.id.loader_radar_main,null,this);
                break;
            default:
                throw new IllegalArgumentException("Unknown result request code "+requestCode);
        }
    }

    @Override
    public void doPositiveAlertClick(int dialogId) {
        SharedPreferences.Editor editor = PreferenceUtility.getPreferences().edit();
        editor.putBoolean(PreferenceConstants.radarAutomaticLocationUpdateEnabled, true);
        editor.putBoolean(PreferenceConstants.radarLocationUpdateDialogShown, true);
        editor.commit();
        useLocation = true;
        reload();
    }

    @Override
    public void doNegativeAlertClick(int dialogId) {
        SharedPreferences.Editor editor = PreferenceUtility.getPreferences().edit();
        editor.putBoolean(PreferenceConstants.radarAutomaticLocationUpdateEnabled, false);
        editor.putBoolean(PreferenceConstants.radarLocationUpdateDialogShown, true);
        editor.commit();
    }

}
