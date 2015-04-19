package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum DrinkerFrequency {
    NEVER(R.string.profile_drinker_never),
    SELDOM(R.string.profile_drinker_seldom),
    SOMETIMES(R.string.profile_drinker_sometimes),
    REGULARLY(R.string.profile_drinker_regularly),
    A_LOT(R.string.profile_drinker_alot),
    WINE_ONLY(R.string.profile_drinker_wineonly);

    @StringRes
    private final int m_stringResource;

    DrinkerFrequency(@StringRes int stringResource) {
        m_stringResource = stringResource;
    }

    public int getStringResource(){
        return  m_stringResource;
    }
}
