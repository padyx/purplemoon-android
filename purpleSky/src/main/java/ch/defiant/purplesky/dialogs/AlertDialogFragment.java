package ch.defiant.purplesky.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import ch.defiant.purplesky.R;

/**
 * Fragment for alert dialogs. Use the static methods to create instances. If you wish a notification about which button the user clicked, use
 * {@link #setResponder(IAlertDialogFragmentResponder)}.
 * 
 * @author padyx
 * @since 0.3.2
 */
public class AlertDialogFragment extends DialogFragment {

    protected final class NegativeClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (m_responder != null) {
                m_responder.doNegativeAlertClick(getDialogIdentifier());
            }
        }
    }

    protected final class PositiveClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (m_responder != null) {
                m_responder.doPositiveAlertClick(getDialogIdentifier());
            }
        }
    }

    protected final class NeutralClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (m_responder != null) {
                m_responder.doNeutralAlertClick(getDialogIdentifier());
            }
        }
    }

    private static final int NONE = -1;
    protected static final String EXTRA_TITLE_RES = "titleRes";
    protected static final String EXTRA_MESSAGE_RES = "messageRes";
    protected static final String EXTRA_TITLE = "title";
    protected static final String EXTRA_MESSAGE = "message";
    protected static final String EXTRA_NEGATIVE_TITLE_RES = "negativeRes";
    protected static final String EXTRA_POSITIVE_TITLE_RES = "positiveREs";
    protected static final String EXTRA_NEUTRAL_TITLE_RES = "neutralRes";

    private int m_dialogIdentifier;
    private IAlertDialogFragmentResponder m_responder;

    /**
     * Creates an alert dialog with an OK button (mapped to neutral callback).
     * 
     * @param title
     *            Title resource to use
     * @param message
     *            Message resource to use
     * @param dialogIdentifier
     *            Identifier for the dialog.
     * @return The created fragment.
     */
    public static AlertDialogFragment newOKDialog(int title, int message, int dialogIdentifier) {
        AlertDialogFragment frag = new AlertDialogFragment();
        frag.setDialogIdentifier(dialogIdentifier);
        Bundle args = new Bundle();
        args.putInt(EXTRA_TITLE_RES, title);
        args.putInt(EXTRA_MESSAGE_RES, message);
        args.putInt(EXTRA_NEUTRAL_TITLE_RES, R.string.alert_dialog_ok);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Creates an alert dialog with an OK button (mapped to neutral callback).
     * 
     * @param title
     *            Title resource to use
     * @param message
     *            Message resource to use
     * @param dialogIdentifier
     *            Identifier for the dialog.
     * @return The created fragment.
     */
    public static AlertDialogFragment newOKDialog(String title, String message, int dialogIdentifier) {
        AlertDialogFragment frag = new AlertDialogFragment();
        frag.setDialogIdentifier(dialogIdentifier);
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_MESSAGE, message);
        args.putInt(EXTRA_NEUTRAL_TITLE_RES, R.string.alert_dialog_ok);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Creates an alert dialog with options OK, Cancel. The OK button is mapped to the positive callback and the Cancel button to negative.
     * 
     * @param title
     *            Title resource to use
     * @param message
     *            Message resource to use
     * @param dialogIdentifier
     *            Identifier for the dialog.
     * @return The created fragment.
     */
    public static AlertDialogFragment newOKCancelDialog(int title, int message, int dialogIdentifier) {
        AlertDialogFragment frag = new AlertDialogFragment();
        frag.setDialogIdentifier(dialogIdentifier);
        Bundle args = new Bundle();
        args.putInt(EXTRA_TITLE_RES, title);
        args.putInt(EXTRA_MESSAGE_RES, message);
        args.putInt(EXTRA_POSITIVE_TITLE_RES, R.string.alert_dialog_ok);
        args.putInt(EXTRA_NEGATIVE_TITLE_RES, R.string.alert_dialog_cancel);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Creates an alert dialog with options Yes, No, Cancel. The Yes button is mapped to the positive, The No button to negative and the Cancel button
     * to neutral callback.
     * 
     * @param title
     *            Title resource to use
     * @param message
     *            Message resource to use
     * @param dialogIdentifier
     *            Identifier for the dialog.
     * @return The created fragment.
     */
    public static AlertDialogFragment newYesNoCancelDialog(int title, int message, int dialogIdentifier) {
        AlertDialogFragment frag = new AlertDialogFragment();
        frag.setDialogIdentifier(dialogIdentifier);
        Bundle args = new Bundle();
        args.putInt(EXTRA_TITLE_RES, title);
        args.putInt(EXTRA_MESSAGE_RES, message);
        args.putInt(EXTRA_POSITIVE_TITLE_RES, R.string.alert_dialog_yes);
        args.putInt(EXTRA_NEGATIVE_TITLE_RES, R.string.alert_dialog_no);
        args.putInt(EXTRA_NEUTRAL_TITLE_RES, R.string.alert_dialog_cancel);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Creates an alert dialog with options Discard, Cancel. The Discard button is mapped to the positive, The Cancel button to negative.
     * 
     * @param title
     *            Title resource to use
     * @param message
     *            Message resource to use
     * @param dialogIdentifier
     *            Identifier for the dialog.
     * @return The created fragment.
     */
    public static AlertDialogFragment newDiscardCancelDialog(int title, int message, int dialogIdentifier) {
        AlertDialogFragment frag = new AlertDialogFragment();
        frag.setDialogIdentifier(dialogIdentifier);
        Bundle args = new Bundle();
        args.putInt(EXTRA_TITLE_RES, title);
        args.putInt(EXTRA_MESSAGE_RES, message);
        args.putInt(EXTRA_POSITIVE_TITLE_RES, R.string.alert_dialog_discard);
        args.putInt(EXTRA_NEGATIVE_TITLE_RES, R.string.alert_dialog_cancel);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        createTitle(builder);
        createButtons(builder);

        boolean hasContent = createContent(builder);

        if (!hasContent) {
            createContentTexts(builder);
        }

        return builder.create();
    }
    
    /**
     * Create custom content views.
     * 
     * @param builder
     * @return Whether content has been set. If <tt>true</tt>, no message will be set.
     */
    protected boolean createContent(Builder builder) {
        return false;
    }

    protected void createButtons(AlertDialog.Builder builder) {
        int negative = getArguments().getInt(EXTRA_NEGATIVE_TITLE_RES, NONE);
        if (negative != NONE) {
            builder.setNegativeButton(negative, new NegativeClickListener());
        }
        int positive = getArguments().getInt(EXTRA_POSITIVE_TITLE_RES, NONE);
        if (positive != NONE) {
            builder.setPositiveButton(positive, new PositiveClickListener());
        }
        int neutral = getArguments().getInt(EXTRA_NEUTRAL_TITLE_RES, NONE);
        if (neutral != NONE) {
            builder.setNeutralButton(neutral, new NeutralClickListener());
        }
    }

    protected void createTitle(AlertDialog.Builder builder) {
        if (getArguments() != null) {
            int titleRes = getArguments().getInt(EXTRA_TITLE_RES, NONE);
            String title = getArguments().getString(EXTRA_TITLE);
            if (titleRes != NONE) {
                builder.setTitle(titleRes);
            } else if (title != null) {
                builder.setTitle(title);
            }
        }
    }

    protected void createContentTexts(AlertDialog.Builder builder) {
        if (getArguments() != null) {
            int messageRes = getArguments().getInt(EXTRA_MESSAGE_RES, NONE);
            String message = getArguments().getString(EXTRA_MESSAGE);
            if (messageRes != NONE) {
                builder.setMessage(messageRes);
            } else if (message != null) {
                builder.setMessage(message);
            }
        }
    }

    public int getDialogIdentifier() {
        return m_dialogIdentifier;
    }

    public void setDialogIdentifier(int dialogIdentifier) {
        m_dialogIdentifier = dialogIdentifier;
    }

}
