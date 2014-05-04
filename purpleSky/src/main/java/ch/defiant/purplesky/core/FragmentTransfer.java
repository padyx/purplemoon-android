package ch.defiant.purplesky.core;

import java.util.Map;

import ch.defiant.purplesky.enums.SearchCriteria;
import ch.defiant.purplesky.fragments.RadarFragment;

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

    /**
     * Used for transfer from/to {@link RadarFragment}
     */
    public Map<SearchCriteria, Object> m_searchFilterValues;

    public SearchCriteria m_chosenCriterium;
}
