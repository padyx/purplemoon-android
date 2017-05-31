package ch.defiant.purplesky.beans;

import android.net.Uri;
import android.util.Pair;


import java.net.URL;
import java.util.Collection;

import ch.defiant.purplesky.network.ContentUriRequestBody;

public class UploadBean implements ContentUriRequestBody.ProgressListener {

    public static final long UNKNOWN_LENGTH = -1L;

    @Override
    public void transferred(long transferred, long total) {
        setProgressPercentage((int)Math.ceil(100.0d/total*transferred));
    }

    public enum State {
        PENDING,
        IN_PROGRESS,
        COMPLETE,
        ERROR
    }

    private int m_progressPercentage;
    private final Uri m_fileUri;
    private final String m_formName;
    private final URL m_url;
    private final Collection<Pair<String,String>> m_formParams;
    private final Collection<Pair<String,String>> m_headerParams;
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
    public UploadBean(URL url, Uri fileUri, String fileFormName, Collection<Pair<String,String>> additionalData, Collection<Pair<String,String>> headers) {
        m_url = url;
        m_formParams = additionalData;
        m_headerParams = headers;
        m_fileUri = fileUri;
        m_formName = fileFormName;
    }

    public Collection<Pair<String,String>> getHeaderParams() {
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

    public Collection<Pair<String,String>> getFormParams() {
        return m_formParams;
    }

}
