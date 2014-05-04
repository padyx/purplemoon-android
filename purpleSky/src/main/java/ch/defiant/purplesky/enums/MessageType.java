package ch.defiant.purplesky.enums;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.util.CompareUtility;

public enum MessageType {

    ALL(PurplemoonAPIConstantsV1.JSON_MESSAGE_TYPE_ALL, PurpleSkyApplication.getContext().getString(R.string.All)),
    RECEIVED(PurplemoonAPIConstantsV1.JSON_MESSAGE_TYPE_RECEIVED, PurpleSkyApplication.getContext().getString(R.string.ReceivedMessage)),
    SENT(PurplemoonAPIConstantsV1.JSON_MESSAGE_TYPE_SENT, PurpleSkyApplication.getContext().getString(R.string.Sent)),
    UNKNOWN(null, PurpleSkyApplication.getContext().getString(R.string.Unknown));

    MessageType(String APIValue, String l10n) {
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

    public static MessageType getStatusByAPIValue(String value) {
        for (MessageType m : MessageType.values()) {
            if (CompareUtility.equals(m.getAPIValue(), value)) {
                return m;
            }
        }
        return UNKNOWN;
    }

}
