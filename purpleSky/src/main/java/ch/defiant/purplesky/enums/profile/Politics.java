package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum Politics {

    NO_INTEREST(R.string.profile_politics_nointerest),
    RIGHT(R.string.profile_politics_right),
    MIDDLE_RIGHT(R.string.profile_politics_middleright),
    MIDDLE(R.string.profile_politics_middle),
    MIDDLE_LEFT(R.string.profile_politics_middleleft),
    LEFT(R.string.profile_politics_left);

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
