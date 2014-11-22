package ch.defiant.purplesky.beans;

import android.net.Uri;

import org.apache.http.NameValuePair;

import java.net.URL;
import java.util.Collection;

public class UploadBean {

    public static final long UNKNOWN_LENGTH = -1L;

    public enum State {
        PENDING,
        IN_PROGRESS,
        COMPLETE,
        ERROR;
    }

    private int m_progressPercentage;
    private final Uri m_fileUri;
    private final String m_formName;
    private final URL m_url;
    private final Collection<NameValuePair> m_formParams;
    private final Collection<NameValuePair> m_headerParams;
    private State m_state = State.PENDING;
    private String m_error;

    /**
     * Bean constructor
     * 
     * @param url
     *            The url to upload to
     * @param fileUri
     *            File indicating a local file. May be null to send no file.
     * @param fileFormName
     *            Form data name for the file.
     * @param additionalData
     *            Additional data to store in the form upload. The name parameter is used as the name in the form.
     * @param headers
     *            Additional headers to pass to the server.
     */
    public UploadBean(URL url, Uri fileUri, String fileFormName, Collection<NameValuePair> additionalData, Collection<NameValuePair> headers) {
        m_url = url;
        m_formParams = additionalData;
        m_headerParams = headers;
        m_fileUri = fileUri;
        m_formName = fileFormName;
    }

    public Collection<NameValuePair> getHeaderParams() {
        return m_headerParams;
    }

    public synchronized int getProgressPercentage() {
        return m_progressPercentage;
    }

    public synchronized void setProgressPercentage(int progressPercentage) {
        m_progressPercentage = progressPercentage;
    }

    public URL getUrl() {
        return m_url;
    }

    public synchronized State getState() {
        return m_state;
    }

    public synchronized void setState(State state) {
        m_state = state;
    }

    public String getError() {
        return m_error;
    }

    public void setError(String error) {
        m_error = error;
    }

    public Uri getFileUri() {
        return m_fileUri;
    }

    public String getFormName() {
        return m_formName;
    }

    public Collection<NameValuePair> getFormParams() {
        return m_formParams;
    }

}
