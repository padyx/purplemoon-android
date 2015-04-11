package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum Politics {

    NO_INTEREST(R.string.profile_politics_nointerest),
    RIGHT(R.string.profile_politics_nointerest),
    MIDDLE_RIGHT(R.string.profile_politics_nointerest),
    MIDDLE(R.string.profile_politics_nointerest),
    MIDDLE_LEFT(R.string.profile_politics_nointerest),
    LEFT(R.string.profile_politics_nointerest);

    @StringRes
    private final int m_stringRes;

    Politics(int stringRes) {
        m_stringRes = stringRes;
    }

    @StringRes
    public int getStringResource(){
        return m_stringRes;
    }
}
