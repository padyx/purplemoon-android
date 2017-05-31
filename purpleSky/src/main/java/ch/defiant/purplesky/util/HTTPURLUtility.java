package ch.defiant.purplesky.util;

import android.util.Log;
import android.util.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;

import ch.defiant.purplesky.BuildConfig;

public class HTTPURLUtility {
    private static final String EQUALS = "=";
    private static final String AMPERSAND = "&";
    private static final String TAG = HTTPURLUtility.class.getSimpleName();

    public static String createGetQueryString(Collection<Pair<String, String>> pairs) {
        if (pairs == null)
            return "";

        StringBuilder builder = new StringBuilder();
        builder.append("?");

        Iterator<Pair<String,String>> iterator = pairs.iterator();
        while (iterator.hasNext()) {
            Pair<String,String> p = iterator.next();
            if (p.first == null) {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "Missing name value for parameter string. Skipping.");
                    if (!iterator.hasNext()) {
                        // If we are the last one, the previous iteration added an ampersand that is now unneeded.
                        // Erase!
                        builder.deleteCharAt(builder.length() - 1);
                    }
                    continue;
                }
            }

            builder.append(p.first);
            builder.append(EQUALS);
            try {
                builder.append(URLEncoder.encode(p.second, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "Query string could not be created. Unsupported encoding.");
            }

            if (iterator.hasNext()) {
                builder.append(AMPERSAND);
            }
        }
        return builder.toString();
    }
    
}
