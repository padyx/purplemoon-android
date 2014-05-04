package ch.defiant.purplesky.exceptions;

import android.content.Context;

/*
 * Exception whose message can be shown directly to the user.
 * Also supports a 'title' for a general short error message.
 */
public class PurpleSkyException extends Exception {

    private static final long serialVersionUID = -6626451087019129928L;
    private String m_errorTitle;

    public PurpleSkyException() {
        super();
    }

    public PurpleSkyException(String message) {
        super(message);
    }

    public PurpleSkyException(String title, Exception e1) {
        super(title, e1);
    }

    public PurpleSkyException(String title, String message) {
        super(message);
        m_errorTitle = title;
    }

    public PurpleSkyException(Context c, int message) {
        super(c.getString(message));
    }

    public PurpleSkyException(Context c, int title, Exception e1) {
        super(c.getString(title), e1);
    }

    public PurpleSkyException(Context c, int title, int message) {
        this(c, message);
        setErrorTitle(c.getString(title));
    }

    public String getErrorTitle() {
        return m_errorTitle;
    }

    public void setErrorTitle(String errorTitle) {
        m_errorTitle = errorTitle;
    }

}
