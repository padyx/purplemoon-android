package ch.defiant.purplesky.exceptions;

import android.content.Context;
import ch.defiant.purplesky.R;

/**
 * A special subclass of PurpleSkyException to indicate that the action is not permitted because of the missing poweruser status of the application
 * user.
 * 
 * @author padyx
 * 
 */
public class PoweruserException extends PurpleSkyException {

    private static final long serialVersionUID = 4026127355498178931L;

    /**
     * Creates an exception with the default title and your message.
     * 
     * @param c
     * @param message
     *            The message to display
     */
    public PoweruserException(Context c, String message) {
        super(c.getString(R.string.Error_NoPowerUserTitle), message);
    }

    /**
     * Creates an exception with the default title and your string resource.
     * 
     * @param c
     * @param message
     *            Resource ID to a string which will be displayed as title
     */
    public PoweruserException(Context c, int messageRes) {
        super(c.getString(R.string.Error_NoPowerUserTitle), c.getString(messageRes));
    }

    public PoweruserException(String string, Exception e1) {
        super(string, e1);
    }

    public PoweruserException(String title, String message) {
        super(title, message);
    }

}
