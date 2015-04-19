package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum HairColor {

    LIGHT_BROWN(R.string.profile_hair_color_lightbrown),
    DARK_BROWN(R.string.profile_hair_color_darkbrown),
    BROWN(R.string.profile_hair_color_brown),
    LIGHT_BLONDE(R.string.profile_hair_color_lightblonde),
    DARK_BLONDE(R.string.profile_hair_color_darkblonde),
    BLONDE(R.string.profile_hair_color_blonde),
    BLACK(R.string.profile_hair_color_black),
    RED(R.string.profile_hair_color_red),
    LIGHT_GREY(R.string.profile_hair_color_lightgrey),
    DARK_GREY(R.string.profile_hair_color_darkgrey),
    DYED_RED(R.string.profile_hair_color_dyedred),
    DYED_BLACK(R.string.profile_hair_color_dyedblack),
    DYED_BLUE(R.string.profile_hair_color_dyedblue),
    DYED_GREEN(R.string.profile_hair_color_dyedgreen),
    DYED_BLONDE(R.string.profile_hair_color_dyedblonde),
    DYED_PURPLE(R.string.profile_hair_color_dyedpurple);

    @StringRes
    private final int m_stringRes;

    HairColor(int stringRes) {
        m_stringRes = stringRes;
    }

    @StringRes
    public int getStringResource(){
        return m_stringRes;
    }
}
