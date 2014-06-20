package ch.defiant.purplesky.enums;

import static ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HETEROSEXUAL_VALUE;
import static ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HOMOSEXUAL_VALUE;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.core.PurpleSkyApplication;

// TODO pbn Remove the API Constants use here
// TODO pbn Merge hetero/homo values
public enum Sexuality {

    /**
     * Sexuality 'heterosexual' for a male. Is indistinguishable by its API value from the female counterpart.
     */
    HETEROSEXUAL_MALE(JSON_USER_SEXUALITY_HETEROSEXUAL_VALUE, PurpleSkyApplication.get().getString(R.string.SexualityHetero)),
    /**
     * Sexuality 'heterosexual' for a female. Is indistinguishable by its API value from the male counterpart.
     */
    HETEROSEXUAL_FEMALE(JSON_USER_SEXUALITY_HETEROSEXUAL_VALUE, PurpleSkyApplication.get().getString(R.string.SexualityHetero)),
    /**
     * Sexuality 'homosexual' for a male. Is indistinguishable by its API value from the female counterpart.
     */
    GAY(JSON_USER_SEXUALITY_HOMOSEXUAL_VALUE, PurpleSkyApplication.get().getString(R.string.SexualityGay)),
    /**
     * Sexuality 'homosexual' for a female. Is indistinguishable by its API value from the male counterpart.
     */
    LESBIAN(JSON_USER_SEXUALITY_HOMOSEXUAL_VALUE, PurpleSkyApplication.get().getString(R.string.SexualityLesbian)),
    /**
     * Sexuality 'bisexual'
     */
    BISEXUAL("bi", PurpleSkyApplication.get().getString(R.string.SexualityBisexual)),
    UNKNOWN(null, PurpleSkyApplication.get().getString(R.string.Unknown));

    Sexuality(String APIValue, String l10n) {
        m_APIValue = APIValue;
        m_localizationString = l10n;
    }

    private String m_localizationString;
    private String m_APIValue;

    public String getLocalizedString() {
        return m_localizationString;
    }

    public String getAPIValue() {
        return m_APIValue;
    }

    @Override
    public String toString() {
        return m_localizationString;
    }

    public static Sexuality getStatusByAPIValue(String value, Gender gender) {
        if (PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HOMOSEXUAL_VALUE.equals(value)) {
            // Check gender - homo is not enough
            if (Gender.MALE == gender) {
                return GAY;
            } else if (Gender.FEMALE == gender) {
                return LESBIAN;
            } else {
                return UNKNOWN;
            }
        } else if (BISEXUAL.getAPIValue().equals(value)) {
            return BISEXUAL;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HETEROSEXUAL_VALUE.equals(value)) {
            // Check gender...
            if (Gender.MALE == gender) {
                return HETEROSEXUAL_MALE;
            } else if (Gender.FEMALE == gender) {
                return HETEROSEXUAL_FEMALE;
            } else {
                return UNKNOWN;
            }
        } else {
            return UNKNOWN;
        }
    }
}
