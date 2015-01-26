package ch.defiant.purplesky.customwidgets;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;

public class ProgressFragmentDialog extends DialogFragment {
    private String m_title;
    private int m_titleResource = 0;
    private String m_message;
    private int m_messageResource = 0;
    private AsyncTask<?, ?, ?> m_asyncTask;
    private OnDismissListener m_dismissListener;
    private OnCancelListener m_cancelListener;

    public String getTitle() {
        return m_title;
    }

    public void setTitle(String title) {
        m_title = title;
        m_titleResource = 0;
    }

    public String getMessage() {
        return m_message;
    }

    public void setMessage(String message) {
        m_message = message;
        m_messageResource = 0;
    }

    public AsyncTask<?, ?, ?> getAsyncTask() {
        return m_asyncTask;
    }

    public void setAsyncTask(AsyncTask<?, ?, ?> asyncTask) {
        m_asyncTask = asyncTask;
    }

    public int getTitleResource() {
        return m_titleResource;
    }

    public void setTitleResource(int titleResource) {
        m_titleResource = titleResource;
        m_title = null;
    }

    public int getMessageResource() {
        return m_messageResource;
    }

    public void setMessageResource(int messageResource) {
        m_messageResource = messageResource;
        m_message = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());

        if (getTitleResource() != 0) {
            dialog.setTitle(getTitleResource());
        } else {
            dialog.setTitle(getTitle());
        }
        if (getMessageResource() != 0) {
            dialog.setMessage(getString(getMessageResource()));
        } else {
            dialog.setMessage(getMessage());
        }
        dialog.setIndeterminate(true);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            if (getDismissListener() != null) {
                getDismissListener().onDismiss(dialog); // TODO Rewrite to use custom listener w/o param
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (isCancelable() && getAsyncTask() != null) {
            getAsyncTask().cancel(true);
        }
        try {
            if (getCancelListener() != null) {
                getCancelListener().onCancel(dialog); // TODO Rewrite to use custom listener w/o param
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroyView() {
        // WORKAROUND http://code.google.com/p/android/issues/detail?id=17423
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public OnDismissListener getDismissListener() {
        return m_dismissListener;
    }

    public void setDismissListener(OnDismissListener dismissListener) {
        m_dismissListener = dismissListener;
    }

    public OnCancelListener getCancelListener() {
        return m_cancelListener;
    }

    public void setCancelListener(OnCancelListener cancelListener) {
        m_cancelListener = cancelListener;
    }

}
