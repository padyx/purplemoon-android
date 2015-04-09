package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum HairLength {
    BALD(R.string.profile_hair_length_bald),
    SHORT(R.string.profile_hair_length_short),
    MEDIUM(R.string.profile_hair_length_medium),
    LONG(R.string.profile_hair_length_long);

    @StringRes
    private final int m_stringRes;

    HairLength(int stringRes) {
        m_stringRes = stringRes;
    }

    @StringRes
    public int getStringResource(){
        return m_stringRes;
    }
}
