package ch.defiant.purplesky.fragments;

import android.app.Activity;
import android.app.DialogFragment;

import java.util.Set;

import javax.inject.Inject;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.dialogs.IAlertDialogFragmentResponder;

/**
 *
 * @author Patrick Bänziger
 */
public class BaseDialogFragment extends DialogFragment implements IAlertDialogFragmentResponder {

    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PurpleSkyApplication.get().inject(this);
    }

    @Override
    public void doPositiveAlertClick(int dialogId) {
    }

    @Override
    public void doNegativeAlertClick(int dialogId) {
    }

    @Override
    public void doNeutralAlertClick(int dialogId) {
    }

    @Override
    public void doListSelectResult(int dialogId, Set<Integer> data) {
    }
}
