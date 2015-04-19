package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum ChatFrequency {

    ALL_THE_TIME(R.string.profile_chat_frequency_allthetime),
    OFTEN(R.string.profile_chat_frequency_often),
    REGULARLY(R.string.profile_chat_frequency_regularly),
    NOW_AND_THEN(R.string.profile_chat_frequency_nowandthen),
    SELDOM(R.string.profile_chat_frequency_seldom),
    NEVER(R.string.profile_chat_frequency_never);

    @StringRes
    private final int m_stringRes;

    ChatFrequency(int stringRes) {
        m_stringRes = stringRes;
    }

    public int getStringResource(){
        return m_stringRes;
    }
}
