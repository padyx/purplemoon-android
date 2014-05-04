package ch.defiant.purplesky.dialogs;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.widget.CheckBox;

public class CheckboxDialogFragment extends ResultDialogFragment<Boolean> {

    private int checkboxLblRes;
    private CheckBox checkBox;

    @Override
    protected boolean createContent(Builder builder) {
        checkBox = new CheckBox(getActivity());
        checkBox.setGravity(Gravity.CENTER_HORIZONTAL);
        checkBox.setText(getCheckboxLblRes());

        builder.setView(checkBox);

        return true;
    }

    @Override
    protected void createButtons(Builder builder) {
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean result = checkBox.isChecked();

                setResult(result);
                deliverResult(result);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        super.createButtons(builder);
    }

    public int getCheckboxLblRes() {
        return checkboxLblRes;
    }

    public void setCheckboxLblRes(int checkboxLblRes) {
        this.checkboxLblRes = checkboxLblRes;
    }

}
