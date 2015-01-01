package ch.defiant.purplesky.api.promotions;

import ch.defiant.purplesky.api.common.IApiResult;

/**
 * @author Patrick Bänziger
 */
public enum EventRegistrationResult implements IApiResult {

    SUCCESS(false),
    ERROR_NOT_FOUND(true),
    ERROR_GENERIC(true),
    ERROR_PRELIMINARY(true),
    ERROR_TOO_YOUNG(true),
    ERROR_TOO_OLD(true),
    ERROR_WRONG_GENDER(true);

    private boolean m_error;

    EventRegistrationResult(boolean isError){
        m_error = isError;
    }

    @Override
    public boolean isError() {
        return m_error;
    }
}
