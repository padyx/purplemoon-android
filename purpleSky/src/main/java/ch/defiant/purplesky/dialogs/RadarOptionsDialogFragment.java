package ch.defiant.purplesky.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.constants.ResultConstants;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.customwidgets.IntegerSpinner;
import ch.defiant.purplesky.enums.Gender;
import ch.defiant.purplesky.enums.Sexuality;
import ch.defiant.purplesky.fragments.BaseDialogFragment;
import ch.defiant.purplesky.listeners.IResultDeliveryReceiver;
import ch.defiant.purplesky.util.CollectionUtil;

/**
 * Option dialog for radar fragment
 * @author Patrick Bänziger
 */
public class RadarOptionsDialogFragment extends BaseDialogFragment {


    final private UserSearchOptions options = new UserSearchOptions();
    private Spinner genderspinner;
    private Spinner attractionSpinner;
    private CheckBox onlyOnlineCheckbox;
    private IntegerSpinner fromAgeSpinner;
    private IntegerSpinner toAgeSpinner;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getSherlockActivity().getLayoutInflater().inflate(R.layout.dialog_radar_options, null);

        createView(view);

        return new AlertDialog.Builder(getSherlockActivity()).
                setTitle("Show only").
                setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult();
                        dialogInterface.dismiss();
                    }
                }).
                setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).
                setView(view).
                create();
    }

    private void sendResult() {
        updateGenderAttractionOption();
        options.setShowOnlyOnline(onlyOnlineCheckbox.isChecked());
        // TODO pbn validation min/max
        options.setMinAge((Integer) fromAgeSpinner.getSelectedItem());
        options.setMaxAge((Integer) toAgeSpinner.getSelectedItem());

        getTargetFragment().onActivityResult(getTargetRequestCode(), 0,
                new Intent().putExtra(ResultConstants.GENERIC, options));
    }

    private void createView(View view) {
        genderspinner = (Spinner) view.findViewById(R.id.dialog_radar_options_genderspinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getSherlockActivity(),
                R.array.radar_sex_attraction, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderspinner.setAdapter(adapter);

        attractionSpinner = (Spinner) view.findViewById(R.id.dialog_radar_options_attraction);
        ArrayAdapter<CharSequence> attraction = ArrayAdapter.createFromResource(getSherlockActivity(),
                R.array.radar_sex_attraction, android.R.layout.simple_spinner_item);
        attraction.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attractionSpinner.setAdapter(attraction);

        onlyOnlineCheckbox = (CheckBox) view.findViewById(R.id.dialog_radar_options_showOnlyOnlineChkBox);
        fromAgeSpinner = (IntegerSpinner) view.findViewById(R.id.dialog_radar_options_ageFromSpinner);
        toAgeSpinner = (IntegerSpinner) view.findViewById(R.id.dialog_radar_options_ageToSpinner);
    }

    private void updateGenderAttractionOption() {
        int selectedGender = genderspinner.getSelectedItemPosition();
        int selectedAttraction = attractionSpinner.getSelectedItemPosition();

        // Both, Men >only<, Women >only<

        List<Gender> genders = new ArrayList<Gender>();
        switch(selectedGender){
            case 0:
                genders = Arrays.asList(Gender.MALE, Gender.FEMALE);
                break;
            case 1:
                genders = Arrays.asList(Gender.MALE);
                break;
            case 2:
                genders = Arrays.asList(Gender.FEMALE);
                break;
        }
        List<Gender> attractedto = new ArrayList<Gender>();
        switch(selectedAttraction){
            case 0:
                attractedto = Arrays.asList(Gender.MALE, Gender.FEMALE);
                break;
            case 1:
                attractedto = Arrays.asList(Gender.MALE);
                break;
            case 2:
                attractedto = Arrays.asList(Gender.FEMALE);
                break;
        }

        if(genders.size() == 2 && genders.size() == 2) {
            options.setAttractions(null); // No filter
            return;
        }

        ArrayList<Pair<Gender, Sexuality>> attractions = new ArrayList<Pair<Gender, Sexuality>>();
        for(Gender g: genders){
            if(attractedto.size() == 2){
                attractions.add(new Pair<Gender, Sexuality>(g, Sexuality.BISEXUAL));
            } else {
                Gender otherGender = CollectionUtil.firstElement(attractedto);
                if(g==otherGender) {
                    attractions.add(new Pair<Gender, Sexuality>(g, Sexuality.GAY));
                } else {
                    attractions.add(new Pair<Gender, Sexuality>(g, Sexuality.HETEROSEXUAL_FEMALE));
                }
            }
        }

        options.setAttractions(attractions);
    }

}
