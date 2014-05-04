package ch.defiant.purplesky.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurplemoonAPIAdapter;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.exceptions.PoweruserException;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.exceptions.WrongCredentialsException;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.LayoutUtility;
import ch.defiant.purplesky.util.StringUtility;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class CreatePostitDialogFragment extends SherlockDialogFragment implements LoaderCallbacks<Holder<List<Pair<Integer, String>>>> {

    public static final String TAG = CreatePostitDialogFragment.class.getSimpleName();
    public static final String ARGUMENT_RECIPIENT_PROFILEID_URI = "profileId";
    private static final int ERRORDIALOG = 2;

    private static final int CUSTOM_POSTIT = -2;

    private String m_profileId;
    private List<Pair<Integer, String>> m_postits;

    private Spinner m_spinner;
    private boolean m_cachedPowerUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_cachedPowerUser = UserService.isCachedPowerUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null || getArguments().get(ARGUMENT_RECIPIENT_PROFILEID_URI) == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Creating postit: No recipient specified (arguments)!");
            }
            dismiss();
            return null;
        }
        m_profileId = (String) getArguments().get(ARGUMENT_RECIPIENT_PROFILEID_URI);

        getDialog().setTitle(R.string.CreatePostit);

        View inflatedView = inflater.inflate(R.layout.create_postit, container, false);

        // Spinner
        m_spinner = (Spinner) inflatedView.findViewById(R.id.create_postit_postitSpinner);
        m_spinner.setOnItemSelectedListener(new SelectionChangedListener());

        // Button
        Button btn = (Button) inflatedView.findViewById(R.id.create_postit_sendButton);
        btn.setOnClickListener(new CreatePostitListener());

        Button btnCancel = (Button) inflatedView.findViewById(R.id.create_postit_cancelButton);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (m_postits == null) {
            getLoaderManager().initLoader(R.id.loader_createpostit_loadpostits, null, this);
        }
    }

    private class CreatePostitListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            PostitAdapter adapter = (PostitAdapter) m_spinner.getAdapter();
            EditText text = (EditText) getView().findViewById(R.id.create_postit_customEditText);
            Integer postitId = null;

            if (m_spinner != null) {
                int selectedItem = m_spinner.getSelectedItemPosition();
                if (selectedItem == Spinner.INVALID_POSITION
                        || (adapter.getItem(selectedItem) != null && adapter.getItem(selectedItem).first == Spinner.INVALID_POSITION)) {
                    Toast.makeText(getSherlockActivity(), "Must select a valid post-it", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Pair<Integer, String> item = adapter.getItem(selectedItem);
                    if (item != null) {
                        if (CompareUtility.equals(CUSTOM_POSTIT, item.first)) {
                            if (StringUtility.isNullOrEmpty(text.getText().toString())) {
                                Toast.makeText(getSherlockActivity(), R.string.ErrorNoTextCustomPostit, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            // Not custom, ok
                            postitId = item.first;
                        }

                        new SendPostItTask(m_profileId, text.getText().toString(), postitId).execute();
                    }
                }
            }
        }
    }

    private class PostitAdapter extends ArrayAdapter<Pair<Integer, String>> {

        public PostitAdapter(Context context, int resource, int textViewResourceId, List<Pair<Integer, String>> objects) {
            super(context, resource, textViewResourceId, objects);
            m_resource = resource;
        }

        private class ViewHolder {
            TextView m_label;
        }

        final private int m_resource;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder = null;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) LayoutInflater.from(getContext());
                v = vi.inflate(m_resource, null);

                holder = new ViewHolder();
                holder.m_label = (TextView) v.findViewById(android.R.id.text1);
                v.setTag(holder);
                if (isEnabled(position)) {
                    holder.m_label.setTextColor(Color.parseColor("#000000"));
                } else {
                    holder.m_label.setTextColor(Color.parseColor("#CCCCCC"));
                }
            } else {
                holder = (ViewHolder) v.getTag();
            }
            v.setMinimumHeight(LayoutUtility.dpToPx(getResources(), 48));

            Pair<Integer, String> item = getItem(position);
            holder.m_label.setText(item.second);

            return v;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        @Override
        public boolean isEnabled(int position) {
            Pair<Integer, String> item = getItem(position);
            if (item == null) {
                return false;
            }
            return item.first != Spinner.INVALID_POSITION;
        }
    }

    private class SelectionChangedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            PostitAdapter adapter = (PostitAdapter) parent.getAdapter();
            Pair<Integer, String> item = adapter.getItem(position);

            getView().findViewById(R.id.create_postit_customEditText).setVisibility(
                    CompareUtility.equals(item.first, CUSTOM_POSTIT) ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            getView().findViewById(R.id.create_postit_customEditText).setVisibility(View.INVISIBLE);
        }

    }

    private class SendPostItTask extends AsyncTask<Object, Object, Holder<Boolean>> {

        private String m_customText;
        private String m_id;
        private Integer m_value;

        public SendPostItTask(String profileId, String customText, Integer value) {
            m_id = profileId;
            m_customText = customText;
            m_value = value;
        }

        private ProgressDialog m_uploadDialog;

        @Override
        protected void onPreExecute() {
            m_uploadDialog = ProgressDialog.show(getSherlockActivity(), getString(R.string.CreatingPostit), getString(R.string.PleaseWait), true,
                    false);
        }

        @Override
        protected Holder<Boolean> doInBackground(Object... params) {
            try {
                boolean success = PurplemoonAPIAdapter.getInstance().createPostit(m_id, m_value, m_customText);
                return new Holder<Boolean>(success);
            } catch (Exception e) {
                return new Holder<Boolean>(e);
            }
        }

        @Override
        protected void onPostExecute(Holder<Boolean> result) {
            m_uploadDialog.dismiss();

            if (result == null // No result
                    // Fail result but no exception
                    || (result.getException() == null && CompareUtility.equals(false, result.getContainedObject()))) {
                Toast.makeText(getSherlockActivity(), getString(R.string.UnknownErrorOccured), Toast.LENGTH_SHORT).show();
                // Unknown error

            } else if (CompareUtility.equals(true, result.getContainedObject())) {
                // All ok, dismiss fragment
                Toast.makeText(getSherlockActivity(), getString(R.string.PostitCreated), Toast.LENGTH_SHORT).show();
                CreatePostitDialogFragment.this.dismiss();
            } else {
                // Must have exception
                Exception e = result.getException();
                if (e instanceof IOException) {
                    Toast.makeText(getSherlockActivity(), getString(R.string.ErrorOccurred_NoNetwork), Toast.LENGTH_SHORT).show();
                } else if (e instanceof WrongCredentialsException) {
                    PersistantModel.getInstance().handleWrongCredentials(getSherlockActivity());
                } else if (e instanceof PoweruserException) {
                    AlertDialogFragment f = AlertDialogFragment.newOKDialog(R.string.Error_NoPowerUserTitle, R.string.ErrorPoweruserCustomPostits,
                            ERRORDIALOG);
                    f.setRetainInstance(true);
                    f.show(getFragmentManager(), "errorPostit");
                } else if (e instanceof PurpleSkyException) {
                    AlertDialogFragment f = AlertDialogFragment.newOKDialog(((PurpleSkyException) e).getErrorTitle(), e.getMessage(), ERRORDIALOG);
                    f.setRetainInstance(true);
                    f.show(getFragmentManager(), "errorPostit");
                } else {
                    // Unknown
                    Toast.makeText(getSherlockActivity(), getString(R.string.UnknownErrorOccured), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public Loader<Holder<List<Pair<Integer, String>>>> onCreateLoader(int arg0, Bundle arg1) {
        return new SimpleAsyncLoader<Holder<List<Pair<Integer, String>>>>(getSherlockActivity()) {

            @Override
            public Holder<List<Pair<Integer, String>>> loadInBackground() {
                try {
                    List<Pair<Integer, String>> list = PurplemoonAPIAdapter.getInstance().getPostitOptions(m_profileId);
                    return new Holder<List<Pair<Integer, String>>>(list);
                } catch (IOException e) {
                    return new Holder<List<Pair<Integer, String>>>(e);
                } catch (WrongCredentialsException e) {
                    return new Holder<List<Pair<Integer, String>>>(e);
                } catch (PurpleSkyException e) {
                    return new Holder<List<Pair<Integer, String>>>(e);
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader<Holder<List<Pair<Integer, String>>>> arg0, Holder<List<Pair<Integer, String>>> result) {
        Spinner spinner = (Spinner) getView().findViewById(R.id.create_postit_postitSpinner);

        if (result != null) {
            Exception excp = result.getException();
            if (excp != null) {
                List<Pair<Integer, String>> invalid;
                if (excp instanceof IOException) {
                    invalid = Arrays.asList(Pair.create(Spinner.INVALID_POSITION, getResources().getString(R.string.ErrorNoNetworkGenericShort)));
                } else if (excp instanceof WrongCredentialsException) {
                    PersistantModel.getInstance().handleWrongCredentials(getSherlockActivity());
                    invalid = Arrays.asList(Pair.create(Spinner.INVALID_POSITION, getResources().getString(R.string.ErrorWrongCredentials)));
                } else if (excp instanceof PurpleSkyException) {
                    invalid = Arrays.asList(Pair.create(Spinner.INVALID_POSITION, excp.getMessage()));
                } else {
                    invalid = Arrays.asList(Pair.create(Spinner.INVALID_POSITION, getResources().getString(R.string.ErrorGeneric)));
                }
                spinner.setAdapter(new PostitAdapter(getSherlockActivity(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1, invalid));
            } else {
                ArrayList<Pair<Integer, String>> list = new ArrayList<Pair<Integer, String>>();

                if (result.getContainedObject() != null) {
                    list.addAll(result.getContainedObject());
                }
                // If the user is a poweruser, show this
                if (m_cachedPowerUser) {
                    list.add(Pair.create(CUSTOM_POSTIT, getResources().getString(R.string.CustomPostit)));
                }
                spinner.setAdapter(new PostitAdapter(getSherlockActivity(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1, list));
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Holder<List<Pair<Integer, String>>>> arg0) {
    }

}
