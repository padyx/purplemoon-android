package ch.defiant.purplesky.dialogs;

/**
 * Callbacks that are called by {@link AlertDialogFragment} after dismissal.
 * 
 * @author padyx
 * @since 0.3.2
 */
public interface IAlertDialogFragmentResponder {

    /**
     * Callback when the dialog is dismissed with the positive option.
     * 
     * @param dialogId
     *            Identifier of the dialog.
     */
    public void doPositiveAlertClick(int dialogId);

    /**
     * Callback when the dialog is dismissed with the negative option.
     * 
     * @param dialogId
     *            Identifier of the dialog.
     */
    public void doNegativeAlertClick(int dialogId);

    /**
     * Callback when the dialog is dismissed with the neutral option.
     * 
     * @param dialogId
     *            Identifier of the dialog.
     */
    public void doNeutralAlertClick(int dialogId);

}
