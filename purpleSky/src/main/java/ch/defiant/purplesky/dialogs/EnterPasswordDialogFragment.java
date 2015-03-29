package ch.defiant.purplesky.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.fragments.BaseDialogFragment;
import ch.defiant.purplesky.interfaces.IDialogResult;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public class EnterPasswordDialogFragment extends BaseDialogFragment {

    public static interface PasswordResult extends IDialogResult<String> {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Password");
        builder.setView(view);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(getTargetFragment() instanceof PasswordResult){
                    String result = ((EditText) view.findViewById(R.id.dialog_password_password)).getText().toString();
                    ((PasswordResult)getTargetFragment()).onResult(result);
                }
            }
        });
        return builder.create();
    }
}
