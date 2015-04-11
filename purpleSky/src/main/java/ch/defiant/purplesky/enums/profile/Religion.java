package ch.defiant.purplesky.enums.profile;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Baenziger
 */
public enum Religion {

    AGNOSTIC(R.string.profile_religion_agnostic),
    ATHEIST(R.string.profile_religion_atheist),
    ROMAN_CATHOLIC(R.string.profile_religion_romancatholic),
    OLD_CATHOLIC(R.string.profile_religion_oldcatholic),
    EVANGELICAL_REFORMED(R.string.profile_religion_evangelicalreformed),
    PROTESTANT(R.string.profile_religion_protestant),
    RUSSIAN_ORTHODOX(R.string.profile_religion_russianorthodox),
    GREEK_ORTHODOX(R.string.profile_religion_greekorthodox),
    JEWISH_ORTHODOX(R.string.profile_religion_jewishorthodox),
    ORTHODOX(R.string.profile_religion_orthodox),
    CHRISTIAN(R.string.profile_religion_christian),
    FREE_CHURCH(R.string.profile_religion_freechurch),
    FOLK_RELIGION(R.string.profile_religion_folkreligion),
    HINDU(R.string.profile_religion_hindu),
    SUNNITE(R.string.profile_religion_sunnite),
    SHIITE(R.string.profile_religion_shiite),
    JEWISH(R.string.profile_religion_jewish),
    BUDDHIST(R.string.profile_religion_buddhist),
    SHINTO(R.string.profile_religion_shinto),
    SPIRITUAL(R.string.profile_religion_spiritual),
    OTHER(R.string.profile_religion_other);

    @StringRes
    private final int m_stringRes;

    Religion(int stringRes) {
        m_stringRes = stringRes;
    }

    @StringRes
    public int getStringResource(){
        return m_stringRes;
    }
}
