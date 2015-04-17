package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
  */
public enum MessengerType {

    ICQ(R.string.profile_messengers_icq),
    AIM(R.string.profile_messengers_aim),
    MSN(R.string.profile_messengers_msn),
    YAHOO(R.string.profile_messengers_yahoo),
    SKYPE(R.string.profile_messengers_skype),
    JABBER(R.string.profile_messengers_jabber),
    GADU_GADU(R.string.profile_messengers_gadu_gadu),
    ICHAT(R.string.profile_messengers_ichat),
    GOOGLETALK(R.string.profile_messengers_googletalk);

    @StringRes
    private final int m_stringRes;

    MessengerType(@StringRes int stringRes){
        m_stringRes = stringRes;
    }

    @StringRes
    public int getStringRes(){
        return m_stringRes;
    }
}
