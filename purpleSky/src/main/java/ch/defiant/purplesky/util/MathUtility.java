package ch.defiant.purplesky.util;

public class MathUtility {

    public static long max(long... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("No values!");
        }

        long max = values[0];
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }

        return max;
    }

}
