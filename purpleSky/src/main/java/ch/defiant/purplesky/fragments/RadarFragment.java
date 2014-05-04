package ch.defiant.purplesky.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.adapters.ErrorAdapter;
import ch.defiant.purplesky.adapters.UserSearchResultListAdapter;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PreviewUser;
import ch.defiant.purplesky.beans.util.Pair;
import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1.UserSearchOrder;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.PurplemoonAPIAdapter;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.customwidgets.ProgressFragmentDialog;
import ch.defiant.purplesky.enums.SearchCriteria;
import ch.defiant.purplesky.fragments.usersearch.UserSearchFilterFragment;
import ch.defiant.purplesky.listeners.OpenUserProfileListener;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.translators.SearchCriteriaTranslator;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.LocationUtility;

import com.actionbarsherlock.app.SherlockFragment;

public class RadarFragment extends SherlockFragment implements LoaderCallbacks<Holder<List<MinimalUser>>> {

    private static final String SAVEINST_LISTDATA = "listdata";
    private static final String SAVEINST_FILTERVALUES = "filtervalues";
    private static final String SAVEINST_LOCATION = "location";

    private static final String LOCATION_DIALOG_TAG = "LOCATION_DIALOG";
    private static final float ACCURACY_LIMIT = 2001.0f;
    private static final int MAX_UPDATES = 5;

    // Make sure it is serializable!
    private static final ArrayList<SearchCriteria> s_availableFilters = new ArrayList<SearchCriteria>(Arrays.asList(
            new SearchCriteria[] {
                    SearchCriteria.AGE,
                    SearchCriteria.GENDER_SEXUALITY,
            }
            ));

    private Location m_location;
    private LocationManager m_locationManager;
    private AtomicReference<LocationListener> m_listener = new AtomicReference<LocationListener>();
    private ListView m_resultListView;
    private View m_filterButton;
    private ToggleButton m_currentLocationBtn;
    private Adapter m_resultAdapter;
    private HashMap<SearchCriteria, Object> m_filterValues;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_filterValues = new HashMap<SearchCriteria, Object>();
        m_locationManager = (LocationManager) getSherlockActivity().getSystemService(Context.LOCATION_SERVICE);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVEINST_FILTERVALUES)) {
                @SuppressWarnings("unchecked")
                HashMap<SearchCriteria, Object> values = (HashMap<SearchCriteria, Object>) savedInstanceState.getSerializable(SAVEINST_FILTERVALUES);
                m_filterValues = values;
            }
            if (savedInstanceState.containsKey(SAVEINST_LOCATION)) {
                m_location = (Location) savedInstanceState.getParcelable(SAVEINST_LOCATION);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVEINST_LOCATION, m_location);
        outState.putSerializable(SAVEINST_FILTERVALUES, m_filterValues);

        if (m_resultAdapter != null) {
            ArrayList<MinimalUser> l = new ArrayList<MinimalUser>();
            for (int i = 0; i < m_resultAdapter.getCount(); i++) {
                MinimalUser m = (MinimalUser) m_resultAdapter.getItem(i);
                l.add(m);
            }
            outState.putSerializable(SAVEINST_LISTDATA, l);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.radar_fragment, container, false);
        m_resultListView = (ListView) inflated.findViewById(R.id.radar_fragment_list);
        m_filterButton = inflated.findViewById(R.id.radar_fragment_filterBtn);
        m_currentLocationBtn = (ToggleButton) inflated.findViewById(R.id.radar_fragment_locationBtn);

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVEINST_LISTDATA)) {
            @SuppressWarnings("unchecked")
            List<MinimalUser> m = (List<MinimalUser>) savedInstanceState.getSerializable(SAVEINST_LISTDATA);
            m_resultListView.setAdapter(new UserSearchResultListAdapter(getActivity(), m));
        }

        m_filterButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UserSearchFilterFragment f = new UserSearchFilterFragment();
                Bundle args = new Bundle();
                args.putSerializable(UserSearchFilterFragment.EXTRA_AVAILABLEFILTERS, s_availableFilters);
                args.putSerializable(UserSearchFilterFragment.EXTRA_FILTERVALUES, m_filterValues);
                f.setArguments(args);
                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container_frame, f).addToBackStack(null).commit();
            }
        });

        final boolean cachedPU = UserService.isCachedPowerUser();
        m_currentLocationBtn.setEnabled(cachedPU);

        m_currentLocationBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isPressedNow = m_currentLocationBtn.isChecked();
                // m_currentLocationBtn.setPressed(isPressedNow);

                if (isPressedNow && m_location == null) {
                    initiateLocation(); // Also initiates the search
                } else if (!isPressedNow) {
                    abortLocation();
                    m_location = null; // Clear the location
                    getLoaderManager().restartLoader(R.id.loader_radar_main, null, RadarFragment.this);
                }
            }

        });
        m_resultListView.setOnItemClickListener(new OpenUserProfileListener(getSherlockActivity()));
        return inflated;
    }

    @Override
    public void onPause() {
        super.onPause();
        
        if(getSherlockActivity() != null){
            getSherlockActivity().setProgressBarIndeterminateVisibility(false);
        }

        if (m_listener.get() != null) {
            m_locationManager.removeUpdates(m_listener.get());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check if got a transfer from another fragment
        boolean needReload = acceptAdditionalFilters();

        if (m_resultAdapter != null && m_resultAdapter.isEmpty()) {
            // Already loaded, no action at this point
        } else {
            if (m_currentLocationBtn.isChecked()) {
                if (m_location == null) {
                    // Start obtaining location
                    initiateLocation();
                }
            } else {
                needReload = true;
            }
        }
        if (needReload) {
            getLoaderManager().restartLoader(R.id.loader_radar_main, null, this);
        }

    }

    /**
     * Accepts the filter values passed by other fragments.
     * 
     * @return Whether there were filter values processed
     */
    private boolean acceptAdditionalFilters() {
        final Map<SearchCriteria, Object> filterVals = PurpleSkyApplication.getContext().getFragmentTransferInstance().m_searchFilterValues;
        // Reset it
        PurpleSkyApplication.getContext().getFragmentTransferInstance().m_searchFilterValues = null;

        if (filterVals != null && !filterVals.isEmpty()) {
            for (Entry<SearchCriteria, Object> p : filterVals.entrySet()) {
                if (p.getValue() == null) {
                    // Was deleted
                    m_filterValues.remove(p.getKey());
                } else {
                    m_filterValues.put(p.getKey(), p.getValue());
                }
            }
            // Retrigger search
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Loader<Holder<List<MinimalUser>>> onCreateLoader(int arg0, Bundle arg1) {
        getSherlockActivity().setProgressBarIndeterminateVisibility(true);

        return new SimpleAsyncLoader<Holder<List<MinimalUser>>>(getSherlockActivity(), R.id.loader_radar_main) {

            @Override
            public Holder<List<MinimalUser>> loadInBackground() {
                UserSearchOptions opts = new UserSearchOptions();
                if (m_location != null) {
                    opts.setLocation(new Pair<Double, Double>(m_location.getLatitude(), m_location.getLongitude()));
                }
                opts.setUserClass(PreviewUser.class);

                SearchCriteriaTranslator.setSearchCriteria(opts, m_filterValues);
                opts.setSearchOrder(UserSearchOrder.DISTANCE);

                try {
                    List<MinimalUser> result = PurplemoonAPIAdapter.getInstance().searchUser(opts);
                    return new Holder<List<MinimalUser>>(result);
                } catch (Exception e) {
                    return new Holder<List<MinimalUser>>(e);
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Holder<List<MinimalUser>>> loader, Holder<List<MinimalUser>> result) {
        if (getSherlockActivity() == null) {
            return;
        }

        getSherlockActivity().setProgressBarIndeterminateVisibility(false);

        if (result != null && result.getContainedObject() != null) {
            List<MinimalUser> list = result.getContainedObject();
            UserSearchResultListAdapter adapter = new UserSearchResultListAdapter(getSherlockActivity(), list);
            if (m_location != null) {
                adapter.setOwnLocation(LocationUtility.translateLocationToLocationBean(m_location));
            } else {
                adapter.setOwnLocation(null);
            }
            m_resultAdapter = adapter;
            m_resultListView.setAdapter(adapter);
        } else if (result != null && result.getException() != null) {
            m_resultAdapter = null;
            if (result.getException() instanceof IOException) {
                m_resultListView.setAdapter(new ErrorAdapter(getSherlockActivity()));
            } else {
                m_resultListView.setAdapter(new ErrorAdapter(R.string.UnknownErrorOccured, getSherlockActivity()));
            }
        } else {
            m_resultAdapter = null;
            m_resultListView.setAdapter(new ErrorAdapter(R.string.UnknownErrorOccured, getSherlockActivity()));
        }

    }

    @Override
    public void onLoaderReset(Loader<Holder<List<MinimalUser>>> arg0) {
    }

    private void initiateLocation() {
        ProgressFragmentDialog fragDialog = (ProgressFragmentDialog) getFragmentManager().findFragmentByTag(LOCATION_DIALOG_TAG);
        if (fragDialog == null) {
            fragDialog = new ProgressFragmentDialog();
            fragDialog.setMessageResource(R.string.ObtainingLocation);
            fragDialog.show(getFragmentManager(), LOCATION_DIALOG_TAG);
        }
        fragDialog.setDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                abortLocation();
            }
        });

        // Retrieve a list of location providers that have fine accuracy, no monetary cost, etc
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setCostAllowed(false);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);

        String providerName = m_locationManager.getBestProvider(criteria, true);
        LocationProvider provider = null;
        // If no suitable provider is found, null is returned.
        if (providerName != null && m_locationManager.isProviderEnabled(providerName)) {
            provider = m_locationManager.getProvider(providerName);
        }

        if (provider == null) {
            Toast.makeText(getSherlockActivity(), R.string.NoLocationDevices, Toast.LENGTH_LONG).show();
            locationFailed(R.string.ErrorUnsupportedDeviceGeneric);
            return;
        }

        LocationListener listener = new LocationListener() {

            private int locationCount = 0;

            @Override
            public void onStatusChanged(String prv, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String prv) {
            }

            @Override
            public void onProviderDisabled(String prv) {
            }

            @Override
            public void onLocationChanged(Location location) {
                locationCount++;
                if (location.getAccuracy() < ACCURACY_LIMIT) {
                    m_locationManager.removeUpdates(this);
                    m_listener.compareAndSet(this, null);
                    locationObtained(location);
                } else if (locationCount == MAX_UPDATES) {
                    // We have not enough accuracy
                    m_locationManager.removeUpdates(this);
                    m_listener.compareAndSet(this, null);
                    locationFailed(R.string.LocationTimeout);
                }
            }

        };
        m_listener.set(listener);
        m_locationManager.requestLocationUpdates(providerName, 5000, 250, listener);

    }

    private void abortLocation() {
        LocationListener listener = m_listener.get();
        if (listener != null) {
            m_locationManager.removeUpdates(listener);
        }
        ProgressFragmentDialog frag = (ProgressFragmentDialog) getFragmentManager().findFragmentByTag(LOCATION_DIALOG_TAG);
        if (frag != null) {
            frag.dismiss();
        }
    }

    private void locationFailed(int errorunsupporteddevicegeneric) {
        ProgressFragmentDialog frag = (ProgressFragmentDialog) getFragmentManager().findFragmentByTag(LOCATION_DIALOG_TAG);
        if (frag != null) {
            frag.dismiss();
        }
    }

    private void locationObtained(Location location) {
        ProgressFragmentDialog frag = (ProgressFragmentDialog) getFragmentManager().findFragmentByTag(LOCATION_DIALOG_TAG);
        if (frag != null) {
            frag.dismiss();
        }
        m_location = location;

        getSherlockActivity().getSupportLoaderManager().restartLoader(R.id.loader_radar_main, null, this);
    }

}
