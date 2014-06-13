package ch.defiant.purplesky.util;

import android.os.Bundle;

/**
 * @author Patrick Bänziger
 */
public class BundleUtil {

    public static void safePut(Bundle b, String key, Integer i){
        if(i != null){
            b.putInt(key, i);
        }
    }

    public static Integer getIntWithNull(Bundle b, String key){
        if( b.containsKey(key) ){
            return b.getInt(key);
        } else {
            return null;
        }
    }

    // Workaround for Android < API Level 12
    public static String getStringWithNull(Bundle b, String key){
        if( b.containsKey(key) ){
            return b.getString(key);
        } else {
            return null;
        }
    }

}
