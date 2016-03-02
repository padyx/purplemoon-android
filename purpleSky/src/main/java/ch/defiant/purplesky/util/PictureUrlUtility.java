package ch.defiant.purplesky.util;

import ch.defiant.purplesky.enums.UserPictureSize;

/**
 * @author Patrick Bänziger
 */
public class PictureUrlUtility {

    public static String getPictureUrl(String baseUrl, UserPictureSize size){
        return baseUrl + size.getAPIValue() + ".jpg";
    }

}
