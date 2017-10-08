package ch.defiant.purplesky.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.UserSearchResultsActivity;
import ch.defiant.purplesky.activities.UserSearchTabbedActivity;
import ch.defiant.purplesky.adapters.SpinnerStateElement;
import ch.defiant.purplesky.beans.util.Pair;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.core.UserSearchOptions.SearchType;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.customwidgets.IntegerSpinner;
import ch.defiant.purplesky.customwidgets.ProgressFragmentDialog;
import ch.defiant.purplesky.dialogs.ChooseLocationDialogFragment;
import ch.defiant.purplesky.enums.UserSearchOrder;
import ch.defiant.purplesky.enums.profile.Gender;
import ch.defiant.purplesky.enums.profile.Sexuality;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.listeners.IResultDeliveryReceiver;
import ch.defiant.purplesky.util.CompareUtility;

public class SimpleUserSearchFragment extends Fragment implements UserSearchTabbedActivity.ISearchUserFragment {

    private static final String TAG_PROGRESS_DIALOG = "locationProgressDialog";

    private enum TARGET {
        RELATIONSHIP,
        FRIENDSHIP
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.layout_usersearch_simple, container, false);
        createGUI(inflated);
        return inflated;
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreViewSelections();
    }

    @Override
    public void onPause() {
        super.onPause();
        View v = getView();
        saveViewSelections(v);
    }

    private void saveViewSelections(View v) {
        Editor pref = PreferenceUtility.getPreferences().edit();
        pref.putInt(PreferenceConstants.searchTarget, ((Spinner) v.findViewById(R.id.usersearch_simple_targetSpinner)).getSelectedItemPosition());
        pref.putInt(PreferenceConstants.searchTargetPerson,
                ((Spinner) v.findViewById(R.id.usersearch_simple_targetpersonSpinner)).getSelectedItemPosition());
        pref.putInt(PreferenceConstants.searchAgeMin, ((Spinner) v.findViewById(R.id.usersearch_simple_ageFromSpinner)).getSelectedItemPosition());
        pref.putInt(PreferenceConstants.searchAgeMax, ((Spinner) v.findViewById(R.id.usersearch_simple_ageToSpinner)).getSelectedItemPosition());
        pref.putInt(PreferenceConstants.searchCountry, ((Spinner) v.findViewById(R.id.usersearch_simple_countrySpinner)).getSelectedItemPosition());
        pref.putBoolean(PreferenceConstants.searchUseDistance, ((CheckBox) v.findViewById(R.id.usersearch_simple_distance)).isChecked());
        pref.putBoolean(PreferenceConstants.searchOnlineOnly, ((CheckBox) v.findViewById(R.id.usersearch_simple_onlineOnly)).isChecked());
        pref.apply();
    }

    private void restoreViewSelections() {
        View v = getView();
        SharedPreferences pref = PreferenceUtility.getPreferences();

        ((Spinner) v.findViewById(R.id.usersearch_simple_targetSpinner)).setSelection(pref.getInt(PreferenceConstants.searchTarget, 0));
        ((Spinner) v.findViewById(R.id.usersearch_simple_targetpersonSpinner)).setSelection(pref.getInt(PreferenceConstants.searchTargetPerson, 0));
        ((Spinner) v.findViewById(R.id.usersearch_simple_ageFromSpinner)).setSelection(pref.getInt(PreferenceConstants.searchAgeMin, 0));
        ((Spinner) v.findViewById(R.id.usersearch_simple_ageToSpinner)).setSelection(pref.getInt(PreferenceConstants.searchAgeMax, 0));
        ((Spinner) v.findViewById(R.id.usersearch_simple_countrySpinner)).setSelection(pref.getInt(PreferenceConstants.searchCountry, 0));
        ((CheckBox) v.findViewById(R.id.usersearch_simple_distance)).setChecked(pref.getBoolean(PreferenceConstants.searchUseDistance, false));
        ((CheckBox) v.findViewById(R.id.usersearch_simple_onlineOnly)).setChecked(pref.getBoolean(PreferenceConstants.searchOnlineOnly, false));
    }

    @Override
    public void startSearch() {
        // Synchronized to ensure thread sync
        UserSearchOptions bean = createSearchBean();
        try {
            verify(bean);
        } catch (PurpleSkyException e) {
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (bean.getSearchOrder() == UserSearchOrder.DISTANCE) {
            // Start search
            startLocationSearch(bean);
        } else {
            startSearch(bean);
        }

    }

    private void startSearch(UserSearchOptions bean) {
        Intent intent = new Intent(getActivity(), UserSearchResultsActivity.class);
        intent.putExtra(UserSearchResultsActivity.EXTRA_SEARCHOBJ, bean);
        getActivity().startActivity(intent);
    }

    private void createGUI(View inflated) {
        createTargetSpinner(inflated);
        createTargetPersonSpinner(inflated);
        updateChooserLbls(inflated);

        if (!UserService.isCachedPowerUser()) {
            inflated.findViewById(R.id.usersearch_simple_distance).setEnabled(false);
        }
    }

    private void createTargetPersonSpinner(View inflated) {
        ArrayList<SpinnerStateElement<Pair<Gender, Sexuality>>> list = new ArrayList<>();
        list.add(new SpinnerStateElement<>(new Pair<Gender, Sexuality>(null, null), getString(R.string.targetperson_any)));
        list.add(new SpinnerStateElement<>(new Pair<Gender, Sexuality>(Gender.MALE, null),
                getString(R.string.targetperson_man)));
        list.add(new SpinnerStateElement<>(new Pair<>(Gender.MALE, Sexuality.HOMOSEXUAL),
                getString(R.string.targetperson_gayman)));
        list.add(new SpinnerStateElement<>(new Pair<>(Gender.MALE, Sexuality.BISEXUAL),
                getString(R.string.targetperson_bisexualman)));
        list.add(new SpinnerStateElement<>(new Pair<>(Gender.MALE, Sexuality.HETEROSEXUAL),
                getString(R.string.targetperson_heteroman)));
        list.add(new SpinnerStateElement<>(new Pair<Gender, Sexuality>(Gender.FEMALE, null),
                getString(R.string.targetperson_woman)));
        list.add(new SpinnerStateElement<>(new Pair<>(Gender.FEMALE, Sexuality.HOMOSEXUAL),
                getString(R.string.targetperson_lesbianwoman)));
        list.add(new SpinnerStateElement<>(new Pair<>(Gender.FEMALE, Sexuality.BISEXUAL),
                getString(R.string.targetperson_bisexualwoman)));
        list.add(new SpinnerStateElement<>(new Pair<>(Gender.FEMALE, Sexuality.HETEROSEXUAL),
                getString(R.string.targetperson_heterowoman)));

        ArrayAdapter<SpinnerStateElement<Pair<Gender, Sexuality>>> adapter = new ArrayAdapter<>(
                this.getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
        ((Spinner) inflated.findViewById(R.id.usersearch_simple_targetpersonSpinner)).setAdapter(adapter);
    }

    private void createTargetSpinner(View inflated) {
        ArrayList<SpinnerStateElement<TARGET>> list = new ArrayList<>();
        list.add(new SpinnerStateElement<>(TARGET.FRIENDSHIP, getString(R.string.target_friendship)));
        list.add(new SpinnerStateElement<>(TARGET.RELATIONSHIP, getString(R.string.target_relationship)));

        ArrayAdapter<SpinnerStateElement<TARGET>> adapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list);
        ((Spinner) inflated.findViewById(R.id.usersearch_simple_targetSpinner)).setAdapter(adapter);
    }

    private void updateChooserLbls(View inflated) {
    }

    @SuppressWarnings("unchecked")
    private UserSearchOptions createSearchBean() {
        View v = getView();
        Spinner targetSpinner = (Spinner) v.findViewById(R.id.usersearch_simple_targetSpinner);
        Spinner targetPersonSpinner = (Spinner) v.findViewById(R.id.usersearch_simple_targetpersonSpinner);
        IntegerSpinner ageFromSpinner = (IntegerSpinner) v.findViewById(R.id.usersearch_simple_ageFromSpinner);
        IntegerSpinner ageToSpinner = (IntegerSpinner) v.findViewById(R.id.usersearch_simple_ageToSpinner);
        Spinner countrySpinner = (Spinner) v.findViewById(R.id.usersearch_simple_countrySpinner);
        CheckBox onlyOnline = (CheckBox) v.findViewById(R.id.usersearch_simple_onlineOnly);
        CheckBox byLocation = (CheckBox) v.findViewById(R.id.usersearch_simple_distance);

        UserSearchOptions bean = new UserSearchOptions();

        bean.setMinAge((Integer) ageFromSpinner.getSelectedItem());
        bean.setMaxAge((Integer) ageToSpinner.getSelectedItem());

        Pair<Gender, Sexuality> genderSexuality = ((SpinnerStateElement<Pair<Gender, Sexuality>>) targetPersonSpinner.getSelectedItem()).getValue();
        bean.setAttractions(createGenderSexList(genderSexuality));

        // Get the value
        int pos = countrySpinner.getSelectedItemPosition();
        String[] array = getResources().getStringArray(R.array.countryIds);
        if (pos >= 0 && pos < array.length) {
            bean.setCountryId(array[pos]);
        }

        bean.setLastOnline(onlyOnline.isChecked() ? UserSearchOptions.LastOnline.NOW : null);

        SpinnerStateElement<TARGET> target = (SpinnerStateElement<TARGET>) targetSpinner.getSelectedItem();
        bean.setSearchType(target.getValue() == TARGET.RELATIONSHIP ? SearchType.PARTNER : SearchType.FRIENDS);

        bean.setSearchOrder(byLocation.isChecked() ? UserSearchOrder.DISTANCE : UserSearchOrder.LAST_ONLINE);
        return bean;
    }

    private List<Pair<Gender, Sexuality>> createGenderSexList(Pair<Gender, Sexuality> pair) {
        if (pair == null) {
            return Collections.emptyList();
        }
        List<Gender> genders = new ArrayList<>();
        if (pair.getFirst() == null) {
            genders.add(Gender.MALE);
            genders.add(Gender.FEMALE);
        } else {
            genders.add(pair.getFirst());
        }

        List<Sexuality> sexualities = new ArrayList<>();
        if (pair.getSecond() == null) {
            sexualities.add(Sexuality.HETEROSEXUAL);
            sexualities.add(Sexuality.BISEXUAL);
            sexualities.add(Sexuality.HOMOSEXUAL);
        } else {
            sexualities.add(pair.getSecond());
        }

        List<Pair<Gender, Sexuality>> res = new ArrayList<>();
        for (Gender g : genders) {
            for (Sexuality s : sexualities) {
                res.add(new Pair<>(g, s));
            }
        }
        return res;
    }

    private void verify(UserSearchOptions options) throws PurpleSkyException {
        if (options.getMaxAge() != null && options.getMinAge() != null && (CompareUtility.compare(options.getMinAge(), options.getMaxAge()) == 1)) {
            throw new PurpleSkyException(getResources().getString(R.string.MinMaxAgeError));
        }
    }

    private void startLocationSearch(final UserSearchOptions bean) {
        ChooseLocationDialogFragment f = new ChooseLocationDialogFragment();
        f.setResultReceiver(new IResultDeliveryReceiver<Location>() {
            private static final long serialVersionUID = 3708852154514123159L;

            @Override
            public void deliverResult(Location result) {
                locationObtained(result, bean);

            }

            @Override
            public void noResult() {
            }
        });
        f.show(getActivity().getFragmentManager(), TAG_PROGRESS_DIALOG);
    }

    private void locationObtained(Location l, UserSearchOptions bean) {
        dismissProgressLocation();
        bean.setLocation(new Pair<>(l.getLatitude(), l.getLongitude()));
        startSearch(bean);
    }

    private void dismissProgressLocation() {
        ChooseLocationDialogFragment fragm = (ChooseLocationDialogFragment) getFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG);
        if (fragm != null) {
            fragm.dismiss();
        }
    }

}
