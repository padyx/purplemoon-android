package ch.defiant.purplesky.api.gallery.internal;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class GalleryAPIConstants {

    public static final String PICTUREFOLDER_FOLDERSONLY_URL = "/users/folders/";
    public static final String PICTUREFOLDER_FOLDERSONLY_ME_URL = "/me/folders";

    public static final String PICTUREFOLDER_WITHPICTURES_URL = "/users/pictures/";
    public static final String ENTERPASSWORD_URL =  "/users/enter_folder_password/";

    public static final String ENTERPASSWORD_PASSWORD_PARAM = "password";

    public static final String ENTERPASSWORD_RESPONSE_WRONG_PASSWORD = "wrong_password";
    public static final String ENTERPASSWORD_RESPONSE_FOLDER_UNAVAILABLE = "folder_unavailable";
    public static final String ENTERPASSWORD_RESPONSE_PROFILE_NOT_FOUND= "not_found";
    public static final String ENTERPASSWORD_RESPONSE_TOO_MANY_ATTEMPTS = "too_many_attempts";
    public static final String ENTERPASSWORD_RESPONSE_UNKNOWN = "unknown";

}
