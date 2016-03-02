package ch.defiant.purplesky.ch.defiant.purplesky.util;

import junit.framework.Assert;

import org.junit.Test;

import ch.defiant.purplesky.enums.UserPictureSize;
import ch.defiant.purplesky.util.PictureUrlUtility;

/**
 * @author Patrick Bänziger
 */
public class PictureUrlUtilityTest {

    @Test
    public void testGetPictureUrl(){
        String baseUrl = "http://example.com/abcde/";
        UserPictureSize size = UserPictureSize.LARGE;

        Assert.assertEquals(baseUrl+size.getAPIValue()+".jpg", PictureUrlUtility.getPictureUrl(baseUrl, size));
    }

}
