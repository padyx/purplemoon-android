package ch.defiant.purplesky.enums;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PurpleSkyApplication;

public enum Gender {

    MALE("male", PurpleSkyApplication.getContext().getString(R.string.GenderMale)),
    FEMALE("female", PurpleSkyApplication.getContext().getString(R.string.GenderFemale)),
    UNKNOWN(null, PurpleSkyApplication.getContext().getString(R.string.Unknown));

    Gender(String APIValue, String l10n) {
        m_APIValue = APIValue;
        m_localizationString = l10n;
    }

    private String m_localizationString;
    private String m_APIValue;

    @Override
    public String toString() {
        return m_localizationString;
    }

    public String getAPIValue() {
        return m_APIValue;
    }

    public String getLocalizedString() {
        return m_localizationString;
    }

    public static Gender getStatusByAPIValue(String value) {
        if (MALE.getAPIValue().equals(value)) {
            return MALE;
        }
        else if (FEMALE.getAPIValue().equals(value)) {
            return FEMALE;
        }
        else {
            return UNKNOWN;
        }
    }

}
