package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum FacialHair {
    NONE(R.string.profile_facial_hair_none),
    SHAVED(R.string.profile_facial_hair_shaved),
    THREE_DAY(R.string.profile_facial_hair_threeday),
    MUSTACHE(R.string.profile_facial_hair_mustache),
    PETIT_GOATEE(R.string.profile_facial_hair_petitgoatee),
    GOATEE(R.string.profile_facial_hair_goatee),
    MUTTON_CHOPS(R.string.profile_facial_hair_muttonchops),
    FULL_BEARD(R.string.profile_facial_hair_fullbeard);

    @StringRes
    private final int m_stringRes;

    FacialHair(int stringRes) {
        m_stringRes = stringRes;
    }

    public int getStringResource(){
        return m_stringRes;
    }
}
