package ch.defiant.purplesky.fragments;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockFragment;

import javax.inject.Inject;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.dialogs.IAlertDialogFragmentResponder;

/**
 * Base fragment class, containing the injection call.
 * @author Patrick Bänziger
 * @since 1.1.0
 */
public class BaseFragment extends SherlockFragment implements IAlertDialogFragmentResponder{

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
}
