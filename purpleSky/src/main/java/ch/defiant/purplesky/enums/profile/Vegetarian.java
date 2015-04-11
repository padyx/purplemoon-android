package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum Vegetarian {

    YES(R.string.profile_vegetarian_yes),
    NO(R.string.profile_vegetarian_no),
    VEGAN(R.string.profile_vegetarian_vegan);

    @StringRes
    private final int m_stringResource;

    Vegetarian(@StringRes int stringResource) {
        m_stringResource = stringResource;
    }

    public int getStringResource(){
        return  m_stringResource;
    }

}
