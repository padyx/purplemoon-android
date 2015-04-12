package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author  Patrick Baenziger
 */
public enum EyeColor {

    LIGHTBROWN(R.string.profile_eye_color_lightbrown),
    DARKBROWN(R.string.profile_eye_color_darkbrown),
    BROWN(R.string.profile_eye_color_brown),
    LIGHTBLUE(R.string.profile_eye_color_lightblue),
    DARKBLUE(R.string.profile_eye_color_darkblue),
    BLUE(R.string.profile_eye_color_blue),
    BLACK(R.string.profile_eye_color_black),
    GREEN(R.string.profile_eye_color_green),
    BLUEGREY(R.string.profile_eye_color_bluegrey),
    BLUEGREEN(R.string.profile_eye_color_bluegreen),
    GREENBROWN(R.string.profile_eye_color_greenbrown),
    GREENGREY(R.string.profile_eye_color_greengrey),
    GREY(R.string.profile_eye_color_grey);

    @StringRes
    private final int m_stringRes;

    EyeColor(@StringRes int stringRes){
        m_stringRes = stringRes;
    }

    @StringRes
    public int getStringResource() {
        return m_stringRes;
    }
}
