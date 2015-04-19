package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum WantsKids {

    YES(R.string.profile_kids_want_yes),
    NO(R.string.profile_kids_want_no),
    UNSURE(R.string.profile_kids_want_unsure);

    @StringRes
    private final int m_stringResource;

    WantsKids(@StringRes int stringResource) {
        m_stringResource = stringResource;
    }

    public int getStringResource(){
        return  m_stringResource;
    }
}
