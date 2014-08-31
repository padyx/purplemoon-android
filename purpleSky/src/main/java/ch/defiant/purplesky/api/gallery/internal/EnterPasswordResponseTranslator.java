package ch.defiant.purplesky.api.gallery.internal;

import ch.defiant.purplesky.api.common.ITranslator;
import ch.defiant.purplesky.api.gallery.EnterPasswordResponse;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class EnterPasswordResponseTranslator implements ITranslator<String, EnterPasswordResponse> {

    @Override
    public EnterPasswordResponse translate(String response){
        if(GalleryAPIConstants.ENTERPASSWORD_RESPONSE_FOLDER_UNAVAILABLE.equals(response)){
            return EnterPasswordResponse.FOLDER_UNAVAILABLE;
        } else if (GalleryAPIConstants.ENTERPASSWORD_RESPONSE_PROFILE_NOT_FOUND.equals(response)){
            return EnterPasswordResponse.PROFILE_NOT_FOUND;
        } else if (GalleryAPIConstants.ENTERPASSWORD_RESPONSE_TOO_MANY_ATTEMPTS.equals(response)){
            return EnterPasswordResponse.TOO_MANY;
        } else if (GalleryAPIConstants.ENTERPASSWORD_RESPONSE_FOLDER_UNAVAILABLE.equals(response)){
            return EnterPasswordResponse.FOLDER_UNAVAILABLE;
        } else if (GalleryAPIConstants.ENTERPASSWORD_RESPONSE_WRONG_PASSWORD.equals(response)){
            return EnterPasswordResponse.WRONG_PASSWORD;
        } else if (GalleryAPIConstants.ENTERPASSWORD_RESPONSE_UNKNOWN.equals(response)) {
            return EnterPasswordResponse.ERROR;
        } else {
            throw new IllegalArgumentException("Unknown value: "+response);
        }
    }

}
