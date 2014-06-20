package ch.defiant.purplesky.enums;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PurpleSkyApplication;

/**
* @author Patrick Bänziger
*/
public enum UserSearchOrder {
    LAST_UPDATED("last_updated", PurpleSkyApplication.get().getString(R.string.LastUpdated)),
    LAST_ONLINE("last_online", PurpleSkyApplication.get().getString(R.string.LastOnline)),
    CREATED("created", PurpleSkyApplication.get().getString(R.string.Created)),
    DISTANCE("distance", PurpleSkyApplication.get().getString(R.string.Distance));

    private String m_apiValue;
    private String m_localizedString;

    UserSearchOrder(String apiValue, String localizedString) {
        setApiValue(apiValue);
        setLocalizedString(localizedString);
    }

    public String getLocalizedString() {
        return m_localizedString;
    }

    private void setLocalizedString(String localizedString) {
        m_localizedString = localizedString;
    }

    public String getApiValue() {
        return m_apiValue;
    }

    private void setApiValue(String apiValue) {
        m_apiValue = apiValue;
    }

}
