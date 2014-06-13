package ch.defiant.purplesky.util;

/**
 * @author Patrick Bänziger
 */
public class EnumUtility {

    public static String getName(Enum<?> e) {
        if(e==null){
            return null;
        } else {
            return e.name();
        }
    }

    public static <T extends Enum<T>> T fromName(String name, Class<T> clazz){
        if(name == null || clazz == null){
            return null;
        }
        try {
            return T.valueOf(clazz, name);
        } catch (IllegalArgumentException iea){
            return null;
        }
    }
}
