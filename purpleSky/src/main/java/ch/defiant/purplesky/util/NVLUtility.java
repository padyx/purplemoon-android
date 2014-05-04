package ch.defiant.purplesky.util;

public class NVLUtility {

    public static <T> T nvl(T value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

}
