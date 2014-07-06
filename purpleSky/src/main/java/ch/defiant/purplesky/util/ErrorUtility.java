package ch.defiant.purplesky.util;

/**
 * @author Patrick Bänziger
 * @since  1.1.0
 */
public class ErrorUtility {

    /**
     * Retrieve the line number where the throwable was created.
     * @param t
     * @return
     */
    public static int getLineNumber(Throwable t){
        return t.getStackTrace()[0].getLineNumber();
    }

    public static CharSequence getErrorId(Throwable t){
        StackTraceElement element = t.getStackTrace()[0];
        String className = element.getClassName();

        StringBuilder sb = new StringBuilder();
        sb.append(encodeClassCaps(className));
        sb.append(":");
        sb.append(getLineNumber(t));

        return sb;
    }

    public static CharSequence encodeClassCaps(String className){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<className.length(); i++){
            char c = className.charAt(i);
            if(i==0){
                sb.append(c);
            } else if (Character.isUpperCase(c)){
                sb.append(c);
            }
        }
        return sb;
    }
}
