package ch.defiant.purplesky.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.constants.ArgumentConstants;

/**
 * @author Patrick Bänziger
 * @since 1.1.0
 */
public class ListDialogFragment extends DialogFragment {

    private static final String STATE_SELECTED = "selected";

    private Set<Integer> selectedItems = new HashSet<>();
    boolean isMultiCheckable = true; // FIXME make multicheckable

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> elements = Collections.emptyList();
        List<Integer> checked = Collections.emptyList();
        if(getArguments() != null) {
            if(getArguments().getCharSequenceArrayList(ArgumentConstants.ARG_ITEMS) != null){
                elements = getArguments().getStringArrayList(ArgumentConstants.ARG_ITEMS);
            }
            if(getArguments().getIntegerArrayList(ArgumentConstants.ARG_SELECTION) != null) {
                checked = getArguments().getIntegerArrayList(ArgumentConstants.ARG_SELECTION);
            }
        }

        boolean[] checkedState;
        if(savedInstanceState != null){
            ArrayList<Integer> selected = savedInstanceState.getIntegerArrayList(STATE_SELECTED);
            this.selectedItems = new HashSet<>(selected);
        } else {
            this.selectedItems.addAll(checked);
        }

        checkedState = toBooleanIndexArray(checked, elements.size());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getTitle());
        if (isMultiCheckable) {
            builder.setMultiChoiceItems(
                elements.toArray(new String[elements.size()]),
                checkedState,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it
                            selectedItems.add(indexSelected);
                        } else if (selectedItems.contains(indexSelected)) {
                            // Otherwise, remove if present
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
            });
        } else {
            builder.setSingleChoiceItems(
                elements.toArray(new String[elements.size()]),
                -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedItems.clear();
                        selectedItems.add(i);
                    }
                });
        }
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //  Your code when user clicked on OK
                //  You can write the code  to save the selected item here
                if (getTargetFragment() instanceof IAlertDialogFragmentResponder){
                    ((IAlertDialogFragmentResponder)getTargetFragment()).doListSelectResult(getId(), selectedItems);
                }
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), null);

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(STATE_SELECTED, new ArrayList<>(selectedItems));
    }

    private String getTitle() {
        if(getArguments().containsKey(ArgumentConstants.STRING_1)){
            return getArguments().getString(ArgumentConstants.STRING_1);
        } else {
            return getString(R.string.PleaseChoose);
        }
    }

    private boolean[] toBooleanIndexArray(Collection<Integer> selectedItems, int size) {
        boolean[] array = new boolean[size];
        for(Integer i: selectedItems){
            array[i] = true;
        }
        return array;
    }

}
