package ch.defiant.purplesky.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.util.Pair;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.constants.ResultConstants;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.customwidgets.IntegerSpinner;
import ch.defiant.purplesky.enums.Gender;
import ch.defiant.purplesky.enums.Sexuality;
import ch.defiant.purplesky.fragments.BaseDialogFragment;
import ch.defiant.purplesky.util.CollectionUtil;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.StringUtility;

/**
 * Option dialog for radar fragment
 * @author Patrick Bänziger
 */
public class RadarOptionsDialogFragment extends BaseDialogFragment {

    private static final String TAG = RadarOptionsDialogFragment.class.getSimpleName();
    private static final int GENDERSEXUALIT_REQUEST = 1;

    private IntegerSpinner fromAgeSpinner;
    private IntegerSpinner toAgeSpinner;
    private UserSearchOptions options;
    private TextView genderSexualityLabel;
    private ImageButton genderSexualityBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options = (UserSearchOptions) getArguments().getSerializable(ArgumentConstants.ARG_SERIALIZABLEOBJECT);
        if(savedInstanceState != null){
            // TODO Handle ?
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getSherlockActivity().getLayoutInflater().inflate(R.layout.dialog_radar_options, null);

        createView(view);
        if(savedInstanceState == null) {
            restoreSelections();
        }

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

    private void restoreSelections() {
        if (options != null) {
            fromAgeSpinner.selectValue(options.getMinAge());
            toAgeSpinner.selectValue(options.getMaxAge());

            if (options.getAttractions() != null) {
                ArrayList<String> attractions = new ArrayList<String>();
                for (Pair<Gender, Sexuality> p : options.getAttractions()) {

                    // FIXME Reimplement
                }
            }
        }
    }

    private void sendResult() {
        // TODO pbn validation min/max
        options.setMinAge((Integer) fromAgeSpinner.getSelectedItem());
        options.setMaxAge((Integer) toAgeSpinner.getSelectedItem());

        getTargetFragment().onActivityResult(getTargetRequestCode(), 0,
                new Intent().putExtra(ResultConstants.GENERIC, options));
    }

    private void createView(View view) {
        genderSexualityLabel = (TextView) view.findViewById(R.id.dialog_radar_options_genderattractionsLbl);
        genderSexualityBtn = (ImageButton) view.findViewById(R.id.dialog_radar_options_genderattractionsBtn);

        genderSexualityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSexualityOptions();
            }
        });

        fromAgeSpinner = (IntegerSpinner) view.findViewById(R.id.dialog_radar_options_ageFromSpinner);
        toAgeSpinner = (IntegerSpinner) view.findViewById(R.id.dialog_radar_options_ageToSpinner);
        updateAttractionUI();
    }

    private void showSexualityOptions() {
        ListDialogFragment fragment = new ListDialogFragment();
        fragment.setTargetFragment(this, GENDERSEXUALIT_REQUEST);

        List<String> texts = Arrays.asList(getResources().getStringArray(R.array.usersearch_opts_str_gendersexuality));

        Bundle args = new Bundle();
        args.putStringArrayList(ArgumentConstants.ARG_ITEMS, new ArrayList<String>(texts));
        args.putIntegerArrayList(ArgumentConstants.ARG_SELECTION, new ArrayList<Integer>(toIndexList(options.getAttractions())));
        fragment.setArguments(args);

        fragment.show(getFragmentManager(), "sexualityGenderDialog");
    }

    private List<Integer> toIndexList(List<Pair<Gender, Sexuality>> attractions) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        if(attractions == null){
            return list;
        }

        String[] values = getResources().getStringArray(R.array.radarsearch_options_attractions_values);
        StringBuilder sb = new StringBuilder();
        for (Pair<Gender, Sexuality> pair : attractions) {
            sb.setLength(0);
            sb.append(pair.getFirst().name());
            sb.append("_");
            sb.append(pair.getSecond().name());
            int index = find(sb.toString(), values);
            if(index != -1){
                list.add(index);
            }
        }

        return list;
    }

    private <T> int find(T search, T[] elements) {
        int i = 0;
        for (T element : elements) {
            if (CompareUtility.equals(element, search)) {
                return i;
            }
            i++;
        }
        return -1;
    }


    @Override
    public void doListSelectResult(int dialogId, Set<Integer> selected) {
        options.setAttractions(toAttractions(selected));
        updateAttractionUI();
    }

    private void updateAttractionUI() {
        List<Integer> indexes = toIndexList(options.getAttractions());
        List<String> strings = new ArrayList<String>();
        String[] values = getResources().getStringArray(R.array.radarsearch_options_attractions_strings);
        for (Integer index : indexes) {
            strings.add(values[index]);
        }

        String joined = StringUtility.join(", ", strings);

        int count = CollectionUtil.safeSize(options.getAttractions());
        if(count>0) {
            genderSexualityLabel.setText(getString(R.string.X_Selected_Y, count, joined));
        } else {
            genderSexualityLabel.setText(getString(R.string.NothingSelected));
        }
    }

    private List<Pair<Gender, Sexuality>> toAttractions(Set<Integer> selected) {
        if(selected == null){
            return Collections.emptyList();
        }
        List<Pair<Gender, Sexuality>> list = new ArrayList<Pair<Gender, Sexuality>>();
        String[] values = getResources().getStringArray(R.array.radarsearch_options_attractions_values);
        if(values == null){
            return Collections.emptyList();
        }
        for (Integer pos : selected) {
            if(pos >= values.length){
                Log.w(TAG, "");
            } else {
                String[] split = values[pos].split("_");
                if(split.length != 2){
                    Log.w(TAG, "");
                } else {
                    list.add(new Pair<Gender, Sexuality>(Gender.valueOf(split[0]), Sexuality.valueOf(split[1])));
                }
            }
        }
        return list;
    }

}
