package ch.defiant.purplesky.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.adapters.ErrorAdapter;
import ch.defiant.purplesky.listeners.IResultDeliveryReceiver;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;

public class ChooseLocationDialogFragment extends SherlockDialogFragment implements LoaderCallbacks<List<Address>>, Callback {

    private static final String SEARCHTXT = "searchText";

    private AtomicReference<LocationListener> m_listener = new AtomicReference<LocationListener>();
    private LocationManager m_locationManager;
    private Location m_location;
    private boolean m_locationFailed;
    private static final float ACCURACY_LIMIT = 2001.0f;
    private static final int MAX_UPDATES = 5;
    private IResultDeliveryReceiver<Location> m_resultReceiver;

    /**
     * Message handler for this one
     */
    private Handler m_handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        m_handler = new Handler(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationListener listener = m_listener.get();
        if (listener != null) {
            m_locationManager.removeUpdates(listener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (m_location == null && !m_locationFailed) {
            initiateLocation();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.location_choose_dialogfragment, container, false);

        EditText editTxt = (EditText) v.findViewById(R.id.location_choose_dialogfragment_editTxt);
        LocationTextWatcher textWatcher = new LocationTextWatcher();
        editTxt.addTextChangedListener(textWatcher);
        if (!Geocoder.isPresent()) {
            editTxt.setEnabled(false);
            editTxt.setHint(R.string.ErrorUnsupportedDeviceGeneric);
        }
        v.findViewById(R.id.location_choose_dialogfragment_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View clickedView) {
                returnResult(m_location);
            }

        });
        ((ListView) v.findViewById(R.id.location_choose_dialogfragment_list)).setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterview, View arg1, int pos, long arg3) {
                Address obj = (Address) adapterview.getItemAtPosition(pos);
                try {
                    Location loc = new Location("");
                    loc.setLatitude(obj.getLatitude());
                    loc.setLongitude(obj.getLongitude());
                    returnResult(loc);
                } catch (IllegalStateException e) {
                    // Nop
                }
            }
        });

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.setTitle(R.string.SelectLocation);
        return d;
    }

    private void initiateLocation() {
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
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
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

    private void locationFailed(int errorResource) {
        m_locationFailed = true;
        m_location = null;
        if (getDialog() != null) {
            TextView lbl = (TextView) getDialog().findViewById(R.id.location_choose_dialogfragment_locationStatLbl);
            lbl.setText(errorResource);
        }
    }

    private void locationObtained(Location location) {
        m_location = location;
        m_locationFailed = false;

        if (getDialog() != null) {
            TextView lbl = (TextView) getDialog().findViewById(R.id.location_choose_dialogfragment_locationStatLbl);
            lbl.setText(R.string.LocationObtained);
            getDialog().findViewById(R.id.location_choose_dialogfragment_button).setEnabled(true);
        }
    }

    @Override
    public Loader<List<Address>> onCreateLoader(int arg0, Bundle b) {
        final String s = b.getString(SEARCHTXT);

        if (getDialog() != null) {
            getDialog().findViewById(R.id.location_choose_dialogfragment_editProgress).setVisibility(View.VISIBLE);
        }

        return new SimpleAsyncLoader<List<Address>>(getSherlockActivity()) {

            @Override
            public List<Address> loadInBackground() {
                int retry = 3;
                while (retry > 0) {
                    try {
                        return new Geocoder(getContext(), Locale.getDefault()).getFromLocationName(s, 5);
                    } catch (IOException e) {
                    }
                    retry--;
                }
                return null;
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<List<Address>> arg0, List<Address> result) {
        if (getDialog() != null) {
            ListView l = (ListView) getDialog().findViewById(R.id.location_choose_dialogfragment_list);
            if (result != null) {
                l.setAdapter(new Adapter(getDialog().getContext(), 0, 0, result));
            } else {
                l.setAdapter(new ErrorAdapter(R.string.ErrorNoNetworkGenericShort, getSherlockActivity()));
            }

            getDialog().findViewById(R.id.location_choose_dialogfragment_editProgress).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Address>> arg0) {
    }

    private final class LocationTextWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            m_handler.removeMessages(0);
            m_handler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    private class Adapter extends ArrayAdapter<Address> {

        private class Holder {
            TextView textView;
        }

        public Adapter(Context context, int resource, int textViewResourceId, List<Address> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder h;
            View v;
            if (convertView == null) {
                v = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
                h = new Holder();
                v.setTag(h);
                h.textView = (TextView) v.findViewById(android.R.id.text1);
            } else {
                v = convertView;
                h = (Holder) v.getTag();
            }

            if (getItem(position) != null) {
                Address item = getItem(position);
                final int l = item.getMaxAddressLineIndex();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < l; i++) {
                    sb.append(item.getAddressLine(i));
                    if (i != (l - 1)) {
                        sb.append("\n");
                    }
                }
                h.textView.setText(sb.toString());
            }
            return v;
        }

    }

    @Override
    public boolean handleMessage(Message arg0) {
        if (getDialog() == null) {
            return true;
        }

        // All messages just are for the text watcher
        TextView text = (TextView) getDialog().findViewById(R.id.location_choose_dialogfragment_editTxt);
        if (text.getText().length() > 3) {
            Bundle b = new Bundle();
            b.putString(SEARCHTXT, text.getText().toString());
            getSherlockActivity().getSupportLoaderManager().restartLoader(R.id.loader_chooselocation_loadgeocoder, b, this);
        } else {
            getSherlockActivity().getSupportLoaderManager().destroyLoader(R.id.loader_chooselocation_loadgeocoder);
        }

        return true;
    }

    public IResultDeliveryReceiver<Location> getResultReceiver() {
        return m_resultReceiver;
    }

    public void setResultReceiver(IResultDeliveryReceiver<Location> resultReceiver) {
        m_resultReceiver = resultReceiver;
    }

    public void returnResult(Location r) {
        if (getResultReceiver() != null) {
            getResultReceiver().deliverResult(r);
        }
        dismiss();
    }

}
