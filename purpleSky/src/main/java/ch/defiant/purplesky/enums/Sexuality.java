package ch.defiant.purplesky.enums;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PurpleSkyApplication;

// TODO pbn Merge hetero/homo values
public enum Sexuality {

    /**
     * Sexuality 'heterosexual' for a male. Is indistinguishable by its API value from the female counterpart.
     */
    HETEROSEXUAL_MALE(PurpleSkyApplication.get().getString(R.string.SexualityHetero)),
    /**
     * Sexuality 'heterosexual' for a female. Is indistinguishable by its API value from the male counterpart.
     */
    HETEROSEXUAL_FEMALE(PurpleSkyApplication.get().getString(R.string.SexualityHetero)),
    /**
     * Sexuality 'homosexual' for a male. Is indistinguishable by its API value from the female counterpart.
     */
    GAY(PurpleSkyApplication.get().getString(R.string.SexualityGay)),
    /**
     * Sexuality 'homosexual' for a female. Is indistinguishable by its API value from the male counterpart.
     */
    LESBIAN(PurpleSkyApplication.get().getString(R.string.SexualityLesbian)),
    /**
     * Sexuality 'bisexual'
     */
    BISEXUAL(PurpleSkyApplication.get().getString(R.string.SexualityBisexual));

    Sexuality(String l10n) {
        m_localizationString = l10n;
    }

    private String m_localizationString;

    public String getLocalizedString() {
        return m_localizationString;
    }

    @Override
    public String toString() {
        return m_localizationString;
    }

}
