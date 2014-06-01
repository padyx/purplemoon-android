package ch.defiant.purplesky.enums;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PurpleSkyApplication;

public enum ProfileStatus {
    OK("ok", PurpleSkyApplication.get().getString(android.R.string.ok)),
    TMP_OFFLINE_ADMIN("temp_offline_by_admin", PurpleSkyApplication.get().getString(R.string.ProfileTmpOfflineByAdmin_Short)),
    TMP_OFFLINE_USER("temp_offline_by_user", PurpleSkyApplication.get().getString(R.string.ProfileTmpOfflineByUser_Short)),
    TMP_OFFLINE_TIMEOUT("temp_offline_by_timeout", PurpleSkyApplication.get().getString(R.string.ProfileTmpOfflineByTimeout_Short)),
    DELETED("deleted", PurpleSkyApplication.get().getString(R.string.ProfileDeleted_Short)),
    BLOCKED("blocked", PurpleSkyApplication.get().getString(R.string.ProfileOfBlockedUser_Short)),
    BLOCKING("blocking", PurpleSkyApplication.get().getString(R.string.ProfileOfBlockingUser_Short)),

    NOTFOUND("notfound", null),

    // No state from API
    UNKNOWN(null, PurpleSkyApplication.get().getString(R.string.Unknown));

    private ProfileStatus(String APIValue, String l10n) {
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

    public static ProfileStatus getStatusByAPIValue(String value) {
        if (OK.getAPIValue().equals(value)) {
            return OK;
        }
        else if (TMP_OFFLINE_ADMIN.getAPIValue().equals(value)) {
            return TMP_OFFLINE_ADMIN;
        }
        else if (TMP_OFFLINE_USER.getAPIValue().equals(value)) {
            return TMP_OFFLINE_USER;
        }
        else if (TMP_OFFLINE_TIMEOUT.getAPIValue().equals(value)) {
            return TMP_OFFLINE_TIMEOUT;
        }
        else if (DELETED.getAPIValue().equals(value)) {
            return DELETED;
        }
        else if (BLOCKED.getAPIValue().equals(value)) {
            return BLOCKED;
        }
        else if (BLOCKING.getAPIValue().equals(value)) {
            return BLOCKING;
        }
        else if (NOTFOUND.getAPIValue().equals(value)) {
            return NOTFOUND;
        }
        else {
            return UNKNOWN;
        }
    }

}
