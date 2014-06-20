package ch.defiant.purplesky.core;

import ch.defiant.purplesky.enums.SearchCriteria;

/**
 * Singleton to pass data between fragments
 * 
 * @author padyx
 * 
 */
public class FragmentTransfer {

    /**
     * Package private. {@link PurpleSkyApplication} will provide the instance publicly
     */
    static final FragmentTransfer INSTANCE = new FragmentTransfer();

    /**
     * Private constructor. Use {@link PurpleSkyApplication} to obtain the singleton
     */
    private FragmentTransfer() {
    }

    public SearchCriteria m_chosenCriterium;
}
