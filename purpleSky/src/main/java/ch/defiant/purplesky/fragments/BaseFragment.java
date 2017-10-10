package ch.defiant.purplesky.fragments;

import android.app.Activity;
import android.app.Fragment;

import java.util.Set;

import javax.inject.Inject;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.dialogs.IAlertDialogFragmentResponder;
import dagger.android.AndroidInjection;
import dagger.android.DaggerFragment;

/**
 * Base fragment class, containing the injection call.
 * @author Patrick Bänziger
 * @since 1.1.0
 */
public class BaseFragment extends DaggerFragment implements IAlertDialogFragmentResponder {

    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;

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
    public void doListSelectResult(int dialogId, Set<Integer> selected) {
    }
}
