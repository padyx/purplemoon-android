package ch.defiant.purplesky.util;

public class StringUtility {

    private static final String WHITESPACE_STRING = " ";
    public static final String EMPTY_STRING = "";
    public static final String WHITE_SPACE = " ";
    
    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (EMPTY_STRING.equals(s));
    }

    public static boolean isNotNullOrEmpty(String s) {
        return (s != null) && (EMPTY_STRING.equals(s) == false);
    }

    public static boolean hasText(String s) {
        return isNotNullOrEmpty(s) && !s.trim().equals(EMPTY_STRING);
    }

    /**
     * Will remove the prefix from the string, optionally only if the full prefix is found. If the prefix is null, the original string is returned.
     * 
     * @param s
     * @param prefix
     * @param requiresFullMatch
     *            If <code>true</code>, will return original string unless the full prefix is contained in the string. Otherwise also a partial prefix
     *            will be removed.
     * @return
     */
    public static String removePrefix(String s, String prefix, boolean requiresFullMatch) {
        if (isNotNullOrEmpty(s)) {
            if (isNullOrEmpty(prefix)) {
                return s;
            } else {
                int idx = 0;
                int smallerLength = (s.length() > prefix.length()) ? prefix.length() : s.length();
                while (idx < smallerLength && s.charAt(idx) == prefix.charAt(idx)) {
                    idx++;
                }
                if (requiresFullMatch) {
                    if (idx == prefix.length()) {
                        return s.substring(idx);
                    } else {
                        return s;
                    }
                } else {
                    return s.substring(idx);
                }
            }
        } else {
            return EMPTY_STRING;
        }
    }

    public static String removePostfix(String s, String postfix, boolean requiresFullMatch) {
        if (isNotNullOrEmpty(s)) {
            if (isNullOrEmpty(postfix)) {
                return s;
            } else {
                int endIndex = 0;
                int smallerLength = (s.length() > postfix.length()) ? postfix.length() : s.length();

                while (endIndex < smallerLength && s.charAt(s.length() - endIndex - 1) == postfix.charAt(postfix.length() - endIndex - 1)) {
                    endIndex++;
                }
                if (requiresFullMatch) {
                    if (endIndex == postfix.length()) {
                        return s.substring(0, s.length() - endIndex);
                    } else {
                        return s;
                    }
                } else {
                    return s.substring(0, s.length() - endIndex);
                }
            }
        } else {
            return EMPTY_STRING;
        }
    }

    public static String parse(Integer i) {
        if (i == null)
            return EMPTY_STRING;
        else
            return i.toString();
    }

    // TODO Unittest
    public static String truncate(String s, int maxLength) {
        if (s == null) {
            return EMPTY_STRING;
        }
        // Short enough
        if (s.length() <= maxLength) {
            return s;
        }
        // Return the substring
        return s.substring(0, maxLength);
    }

    /**
     * Returns null, if the string is equal to the empty string. Otherwise: Returns the input string.
     * 
     * @param s
     * @return
     */
    public static String toNullIfEmpty(String s) {
        if (s == null || EMPTY_STRING.equals(s)) {
            return null;
        } else {
            return s;
        }
    }

    /**
     * Replaces all occurrences of the search string by the replacement string in the string builder.
     * 
     * @param sb
     * @param search
     * @param replace
     * @return Number of replacements
     */
    public static int replace(StringBuilder sb, String search, String replace) {
        if (sb == null) {
            return 0;
        }
        if (search == null) {
            return 0;
        }
        if (replace == null) {
            replace = EMPTY_STRING;
        }

        final int sLength = search.length();
        final int rLength = replace.length();

        int replacements = 0;
        int idx;
        while ((idx = sb.indexOf(search)) != -1) {
            replacements++;
            if (sLength == rLength) {
                sb.replace(idx, idx + sLength, replace);
            } else if (sLength > rLength) {
                // We can insert all, remove rest
                sb.replace(idx, idx + rLength, replace);
                sb.delete(idx + rLength, idx + sLength);
            } else {
                // The replacement string is longer
                int charactsNeeded = rLength - sLength;
                // Insert empty characters after occurence
                for (int i = 0; i < charactsNeeded; i++) {
                    sb.insert(idx + sLength, WHITESPACE_STRING);
                }
                // Replace
                sb.replace(idx, idx + rLength, replace);
            }
        }
        return replacements;
    }

    /**
     * Tries to parse an integer from a string. If it fails, it will return the default value.
     * 
     * @param s
     *            String to parse
     * @param def
     *            The default to returned if s is <tt>null</tt> or not an integer.
     * @return Parsed integer
     */
    public static int permissiveInt(String s, int def) {
        if (s == null) {
            return def;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return def;
        }
    }

    /**
     * Tries to parse a long from a string. If it fails, it will return the default value.
     * 
     * @param s
     *            String to parse
     * @param def
     *            The default to returned if s is <tt>null</tt> or not an long.
     * @return Parsed integer
     */
    public static long permissiveLong(String s, long def) {
        if (s == null) {
            return def;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException nfe) {
            return def;
        }
    }
    
    public static String join(String delimiter, String... values){
        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            if(isNotNullOrEmpty(s)){
                if(sb.length() != 0 ){
                    sb.append(delimiter);
                }
                sb.append(s);
            }
        }
        return sb.toString();
    }
}
