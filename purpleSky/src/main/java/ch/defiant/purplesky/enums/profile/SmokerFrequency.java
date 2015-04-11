package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum SmokerFrequency {

    JOINTS_ONLY(R.string.profile_smoker_jointsonly),
    REALlY_A_LOT(R.string.profile_smoker_reallyalot),
    A_LOT(R.string.profile_smoker_alot),
    MODERATELY(R.string.profile_smoker_moderately),
    ON_WEEKENDS(R.string.profile_smoker_onweekends),
    ALMOST_NEVER(R.string.profile_smoker_almostnever),
    NEVER(R.string.profile_smoker_never);

    @StringRes
    private final int m_stringResource;

    SmokerFrequency(@StringRes int stringResource) {
        m_stringResource = stringResource;
    }

    public int getStringResource(){
        return  m_stringResource;
    }
}
