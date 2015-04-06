package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum Physique {


    SLIM(R.string.profile_physique_slim),
    NORMAL(R.string.profile_physique_normal),
    ATHLETIC(R.string.profile_physique_athletic),
    BODYBUILDER(R.string.profile_physique_bodybuilder),
    STURDY(R.string.profile_physique_sturdy),
    LITTLE_TUMMY(R.string.profile_physique_littletummy),
    CHUBBY(R.string.profile_physique_chubby);

    @StringRes
    private final int m_stringRes;

    Physique(@StringRes int stringRes){
        m_stringRes = stringRes;
    }

    @StringRes
    public int getStringRes(){
        return m_stringRes;
    }

}
