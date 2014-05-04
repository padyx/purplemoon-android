package ch.defiant.purplesky.dialogs;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PurplemoonAPIAdapter;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.Holder;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class OnlineStatusDialogFragment extends SherlockDialogFragment {

    public static final String TAG = OnlineStatusDialogFragment.class.getSimpleName();

    private StatusAdapter m_spinnerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.setonlinestatus_dialog_fragment, container, false);
        createUI(inflated);

        return inflated;
    }

    private void createUI(View inflated) {
        SherlockFragmentActivity activity = getSherlockActivity();

        // Title
        getDialog().setTitle(activity.getResources().getString(R.string.OnlineStatus));

        // Spinner
        Spinner spinner = (Spinner) inflated.findViewById(R.id.setonlinestatus_statusSpinner);
        m_spinnerAdapter = new StatusAdapter(activity);
        spinner.setAdapter(m_spinnerAdapter);

        // OK Button
        View button = inflated.findViewById(R.id.setonlinestatus_okButton);
        button.setOnClickListener(new PerformSubmitListener());
    }

    private class StatusAdapter extends ArrayAdapter<String> {

        public StatusAdapter(Context c) {
            super(c, android.R.layout.simple_dropdown_item_1line);

            OnlineStatus[] choosableStates = OnlineStatus.getValidChoosableStates();
            for (OnlineStatus stat : choosableStates) {
                add(stat.getLocalizedString(getSherlockActivity()));
            }
        }

        @Override
        public boolean isEnabled(int position) {
            return CompareUtility.notEquals(getItem(position), "");
        }

    }

    private class PerformSubmitListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            EditText editText = (EditText) getView().findViewById(R.id.setonlinestatus_customEditText);
            Spinner spinner = (Spinner) getView().findViewById(R.id.setonlinestatus_statusSpinner);

            // Prepare the data.
            OnlineStatus stat = getOnlineStatus((String) spinner.getSelectedItem());

            new SubmitTask().execute(new Pair<OnlineStatus, String>(stat, editText.getText().toString()));
        }

        private OnlineStatus getOnlineStatus(String selectedItem) {
            for (OnlineStatus s : OnlineStatus.getValidChoosableStates()) {
                if (CompareUtility.equals(s.getLocalizedString(getSherlockActivity()), selectedItem)) {
                    return s;
                }
            }
            return null;
        }
    }

    private class SubmitTask extends AsyncTask<Pair<OnlineStatus, String>, Object, Holder<Boolean>> {

        // TODO Handle rotation etc
        private ProgressDialog m_dialog;

        @Override
        protected void onPreExecute() {
            m_dialog = ProgressDialog.show(getSherlockActivity(), "Setting status", "Please wait...", true, false);
        }

        @Override
        protected Holder<Boolean> doInBackground(Pair<OnlineStatus, String>... params) {
            if (params == null || params.length == 0) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Got null/length zero data for setting online status!");
                }
                return new Holder<Boolean>(false);
            }
            try {
                boolean setOnlineStatus = PurplemoonAPIAdapter.getInstance().setOnlineStatus(params[0].first, params[0].second);
                return new Holder<Boolean>(setOnlineStatus);
            } catch (Exception e) {
                return new Holder<Boolean>(e);
            }
        }

        @Override
        protected void onPostExecute(Holder<Boolean> result) {
            onFinally();
            if (result != null && CompareUtility.equals(result.getContainedObject(), Boolean.TRUE)) {
                dismiss(); // Dismiss whole fragment
                // TODO Return value "ok"
                return;
            }

            if (result != null && result.getException() != null) {
                if (result.getException() instanceof IOException) {
                    Toast.makeText(getSherlockActivity(), getSherlockActivity().getString(R.string.ErrorOccurred_NoNetwork), Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                // Unknown error
                Toast.makeText(getSherlockActivity(), R.string.UnknownErrorOccured, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            onFinally();
        }

        private void onFinally() {
            m_dialog.dismiss();
        }

    }
}
