package ch.defiant.purplesky.beans;

import ch.defiant.purplesky.core.INullObject;
import ch.defiant.purplesky.interfaces.IErrorContainer;

/**
 * Null user object
 * 
 * @author padyx
 * @since 0.3.2
 */
public class NullUser extends MinimalUser implements IErrorContainer, INullObject {

    public static final String NULL_USER_ID = "";

    private String m_error;
    private static final long serialVersionUID = -5678939999036776540L;

    @Override
    public void setUserId(String userId) {
        throw new UnsupportedOperationException("Setting user id is unsupported on a NullUser object");
    }

    @Override
    public String getUserId() {
        return NULL_USER_ID;
    }

    @Override
    public String getErrorString() {
        return m_error;
    }

    public void setError(String error) {
        m_error = error;
    }

}
