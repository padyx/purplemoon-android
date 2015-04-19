package ch.defiant.purplesky.enums.profile;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PurpleSkyApplication;

public enum Gender {

    MALE(PurpleSkyApplication.get().getString(R.string.GenderMale)),
    FEMALE(PurpleSkyApplication.get().getString(R.string.GenderFemale));

    Gender(String l10n) {
        m_localizationString = l10n;
    }

    private String m_localizationString;

    @Override
    public String toString() {
        return m_localizationString;
    }

    public String getLocalizedString() {
        return m_localizationString;
    }

}
