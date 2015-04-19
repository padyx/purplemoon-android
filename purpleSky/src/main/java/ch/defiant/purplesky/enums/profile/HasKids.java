package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum HasKids {

    NONE(R.string.profile_kids_have_none),
    ONE(R.string.profile_kids_have_one),
    MULTIPLE(R.string.profile_kids_have_multiple);

    @StringRes
    private final int m_stringResource;

    HasKids(@StringRes int stringResource) {
        m_stringResource = stringResource;
    }

    public int getStringResource(){
        return  m_stringResource;
    }

}
