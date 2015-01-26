package ch.defiant.purplesky.api.common;

/**
 * @author Patrick Bänziger
 */
public class ApiResponse<T> {

    private final boolean m_isError;

    private final int m_responseCode;
    private final String m_error;
    private final String m_errorDescription;
    private final T m_result;

    /**
     * Constructs a normal api response.
     * @param responseCode Response code (HTTP) returned by the API
     * @param result The resulting data returned from the API
     */
    public ApiResponse(int responseCode, T result){
        m_responseCode = responseCode;
        m_result = result;
        m_error = null;
        m_errorDescription = null;
        m_isError = false;
    }

    /**
     * Constructs an error response.
     * @param responseCode Response code (HTTP) returned by the API
     * @param error Error identifier returned by the API
     * @param errorDescription Human readable description of the error
     */
    public ApiResponse(int responseCode, String error, String errorDescription){
        m_responseCode = responseCode;
        m_error = error;
        m_errorDescription = errorDescription;
        m_result = null;
        m_isError = true;
    }

    public String getErrorDescription() {
        return m_errorDescription;
    }

    public String getError() {
        return m_error;
    }

    /**
     * The data returned from the API
     * @return the data returned from the API
     */
    public T getResult() {
        return m_result;
    }

    /**
     * Response code (HTTP) returned by the API
     * @return response code (HTTP) returned by the API
     */
    public int getResponseCode() {
        return m_responseCode;
    }

    /**
     * Returns whether this response constitutes an error (created using the 'error-constructor').
     * @return whether this response is an error (implies that {@link #getResult()} returns <code>null</code>)
     */
    public boolean isError(){
        return m_isError;
    }
}
