package ch.defiant.purplesky.fragments.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Bänziger
 */
public class IndeterminateProgressTaskFragment<Result> extends TaskFragment<Result> {

    @StringRes
    private int m_progressResource;

    public void setProgressTextResource(@StringRes int resource){
        m_progressResource = resource;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(m_progressResource));
        dialog.setIndeterminate(true);
        return dialog;
    }
}
