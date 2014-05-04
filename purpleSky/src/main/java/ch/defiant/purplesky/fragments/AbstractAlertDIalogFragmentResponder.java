package ch.defiant.purplesky.fragments;

import ch.defiant.purplesky.dialogs.IAlertDialogFragmentResponder;

/**
 * Base implementation without behaviour for {@link IAlertDialogFragmentResponder}.
 * 
 * @author Patrick Bänziger
 */
public class AbstractAlertDIalogFragmentResponder implements IAlertDialogFragmentResponder {

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
