package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum OccupationType {

    SCHOOL(R.string.profile_occupation_type_school),
    STUDIES(R.string.profile_occupation_type_studies),
    TRAINING(R.string.profile_occupation_type_training),
    LOOKING(R.string.profile_occupation_type_looking),
    EMPLOYED(R.string.profile_occupation_type_employed),
    SELF_EMPLOYED(R.string.profile_occupation_type_self_employed),
    RAISING_KIDS(R.string.profile_occupation_type_raising_kids),
    RETIRED(R.string.profile_occupation_type_retired);

    @StringRes
    private final int m_stringRes;

    OccupationType(@StringRes int stringRes){
        m_stringRes = stringRes;
    }

    @StringRes
    public int getStringRes(){
        return m_stringRes;
    }
}
