package ch.defiant.purplesky.enums;

import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;

/**
 * Restriction enum for restricting or reordering the retrieved chats.
 */
public enum MessageRetrievalRestrictionType {
    /**
     *
     */
    UNREAD_FIRST(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_UNREADFIRST),
    /**
     *
     */
    UNOPENED_ONLY(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_UNREADONLY),
    /**
     *
     */
    LAST_CONTACT(PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_LASTCONTACT);
    private final String m_apiValue;

    MessageRetrievalRestrictionType(String apiValue) {
        m_apiValue = apiValue;
    }

    public String getApiValue() {
        return m_apiValue;
    }
}
