package ch.defiant.purplesky.dialogs;

import java.io.Serializable;
import java.util.ArrayList;

import ch.defiant.purplesky.constants.ArgumentConstants;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;

/**
 * DialogFragment that shows a dialog with a list out of which the user can choose elements.
 * 
 * @author Patrick Baenziger
 * 
 * @param <T>
 *            Type of values that serve as the key of the elements presented in the list.
 */
public class ListDialogFragment<T> extends ResultDialogFragment<ArrayList<T>> {

    /**
     * Argument to provide the keys which will be returned as a result. Must be provided as an array of T. Must be the same length as the array in
     * {@link #ARG_STRINGS}. Must be a subtype of {@link Serializable}.
     */
    public static final String ARG_KEYS = "keys";
    /**
     * Argument to provide the strings used to label the keys Must be provided as an array of T. Must be the same length as the array in
     * {@link #ARG_KEYS}
     */
    public static final String ARG_STRINGS = "strings";
    /**
     * Optional. Provides the initialization of the selection. If the dialog is configured for multiselect, then this must be a boolean array.
     * Otherwise it must be an int, providing the position!
     */
    public static final String ARG_INITIALCHECKED = "initialchecked";
    /**
     * Optional. Sets the dialog to mutiselect. Argument must be a boolean. Default is false.
     */
    public static final String ARG_MULTISELECT = "multiselect";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        m_isMultiSelect = args.getBoolean(ARG_MULTISELECT, false);
        m_keys = (T[]) args.getSerializable(ARG_KEYS);
        m_enumOrdinal = args.getInt(ArgumentConstants.ARG_ENUMORDINAL, -1);
        if (m_keys == null) {
            throw new IllegalArgumentException("Need a set of keys!");
        }
        m_strings = (String[]) args.getStringArray(ARG_STRINGS);
        if (m_strings == null) {
            throw new IllegalArgumentException("Need a set of label strings!");
        }

        if (m_strings.length != m_keys.length) {
            throw new IllegalArgumentException("Strings and keys need to have the same length!");
        }
        if (m_isMultiSelect) {
            m_multiChecked = args.getBooleanArray(ARG_INITIALCHECKED);
            if (m_multiChecked == null) {
                m_multiChecked = new boolean[m_keys.length];
            }
        } else {
            m_singleCheckPosition = args.getInt(ARG_INITIALCHECKED, -1);
        }
        
    }

    private T[] m_keys;
    private String[] m_strings;
    private boolean[] m_multiChecked;
    private int m_singleCheckPosition;

    private boolean m_isMultiSelect;

    private int m_enumOrdinal;

    @Override
    protected void createButtons(Builder builder) {
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Collect every selected element and return it

                ArrayList<T> l = new ArrayList<T>();

                if (m_isMultiSelect) {
                    int i = 0;
                    for (boolean b : m_multiChecked) {
                        if (b) {
                            l.add(m_keys[i]);
                        }
                        i++;
                    }
                } else {
                    if (m_singleCheckPosition >= 0 && m_singleCheckPosition < m_keys.length) {
                        l.add(m_keys[m_singleCheckPosition]);
                    }
                }

                setResult(l);
                deliverResult(l);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
    }

    @Override
    protected boolean createContent(Builder builder) {
        if (m_isMultiSelect) {
            builder.setMultiChoiceItems(m_strings, m_multiChecked, new OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    m_multiChecked[which] = isChecked;
                }
            });
        }
        else {
            builder.setSingleChoiceItems(m_strings, m_singleCheckPosition, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_singleCheckPosition = which;
                }
            });
        }
        return super.createContent(builder);
    }
    
    @Override
    protected void addResultElements(Intent i){
        if(m_enumOrdinal >= 0){
            i.putExtra(ArgumentConstants.ARG_ENUMORDINAL, m_enumOrdinal);
        }
    }
}
