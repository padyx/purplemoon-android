package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum TargetGender {

    MEN_ONLY(R.string.profile_target_friends_menonly),
    MEN_PREFERRED(R.string.profile_target_friends_menpreferred),
    WOMEN_PREFERRED(R.string.profile_target_friends_womenpreferred),
    WOMEN_ONLY(R.string.profile_target_friends_womenonly);

    @StringRes
    private final int m_stringResource;

    TargetGender(@StringRes int stringResource) {
        m_stringResource = stringResource;
    }

    public int getStringResource(){
        return  m_stringResource;
    }

}
