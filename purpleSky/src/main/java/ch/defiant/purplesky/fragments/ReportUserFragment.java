package ch.defiant.purplesky.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.report.IReportAdapter;
import ch.defiant.purplesky.api.report.ReportResponse;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.enums.UserReportReason;
import ch.defiant.purplesky.loaders.report.ReportUserLoader;
import ch.defiant.purplesky.util.ErrorUtility;
import ch.defiant.purplesky.util.Holder;

/**
 * Dialog that lets the user report another user for TOS violations, harassment, etc.
 * @author Patrick Bänziger
 * @since 1.1.0
 */
public class ReportUserFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Holder<ReportResponse>>{

    private static String TAG =  ReportUserFragment.class.getSimpleName();
    private final String FRAGMENT_TAG_REPORT_PROGRESS = "reportingProgress";

    @Inject
    protected IReportAdapter reportAdapter;

    private ArrayAdapter<String> spinnerAdapter;
    private EditText explanationField;
    private Spinner reasonSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getSherlockActivity().setTitle(R.string.ReportUser);

        View view = inflater.inflate(R.layout.dialog_reportuser, null);
        reasonSpinner = (Spinner) view.findViewById(R.id.fragment_reportuser_reasonSpinner);
        explanationField = (EditText) view.findViewById(R.id.fragment_reportuser_explanation);

        String[] reportReasons = getResources().getStringArray(R.array.ReportReasons);
        spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, reportReasons);
        reasonSpinner.setAdapter(spinnerAdapter);

        Button btn = (Button) view.findViewById(R.id.fragment_reportuser_sendBtn);
        btn.setOnClickListener(new PositiveResponder());

        return view;
    }


    @Override
    public Loader<Holder<ReportResponse>> onCreateLoader(int i, Bundle bundle) {
        String userId = getArguments().getString(ArgumentConstants.ARG_USERID);

        getView().findViewById(R.id.fragment_reportuser_sendBtn).setVisibility(View.GONE);
        getView().findViewById(R.id.fragment_reportuser_resultText).setVisibility(View.GONE);
        getView().findViewById(R.id.fragment_reportuser_progress).setVisibility(View.VISIBLE);

        int position = reasonSpinner.getSelectedItemPosition();
        UserReportReason reason = UserReportReason.OTHER;
        if(position < UserReportReason.values().length){
            reason = UserReportReason.values()[position];
        } else {
            Log.e(TAG, "Unknown position to retrieve reason for");
        }

        return new ReportUserLoader(getActivity(), reportAdapter, userId, reason, explanationField.getText().toString());
    }

    @Override
    public void onLoadFinished(Loader<Holder<ReportResponse>> loader, Holder<ReportResponse> result) {
        if(getView() == null){
            return;
        }
        getLoaderManager().destroyLoader(R.id.loader_reportUser);

        getView().findViewById(R.id.fragment_reportuser_progress).setVisibility(View.GONE);

        View button = getView().findViewById(R.id.fragment_reportuser_sendBtn);
        TextView resultTextView = (TextView) getView().findViewById(R.id.fragment_reportuser_resultText);

        boolean canResend = false;
        String resultText = "";
        if(result != null && result.isObject()){
            ReportResponse response = result.getContainedObject();
            switch (response){
                case OK:
                    resultText = getString(R.string.ReportComplete);
                    break;
                case ERROR:
                    resultText = getString(R.string.UnknownErrorOccured);
                    break;
                case TEXT_TOO_LONG:
                    resultText = getString(R.string.ErrorGenericTextTooLong);
                    break;
                case INVALID_USER:
                    resultText = getString(R.string.ErrorGenericUserNotFound);
                    break;
                case TOO_MANY:
                    resultText = getString(R.string.ErrorTooManyReports);
                    break;
            }
        } else {
            canResend = true;
            Exception exception = (result != null ? result.getException() : null);
            if(exception instanceof IOException){
                resultText = getString(R.string.ErrorOccurred_NoNetwork);
            } else {
                resultText = getString(R.string.UnknownError_X, ErrorUtility.getErrorId(exception));
            }
        }

        resultTextView.setText(resultText);
        resultTextView.setVisibility(View.VISIBLE);
        if(canResend){
            button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Holder<ReportResponse>> holderLoader) { }

    private class PositiveResponder implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            getLoaderManager().restartLoader(R.id.loader_reportUser, null, ReportUserFragment.this);
        }
    }
}
