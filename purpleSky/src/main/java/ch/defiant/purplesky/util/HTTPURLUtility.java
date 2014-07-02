package ch.defiant.purplesky.util;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;

import ch.defiant.purplesky.BuildConfig;

public class HTTPURLUtility {
    private static final String EQUALS = "=";
    private static final String AMPERSAND = "&";
    private static final String TAG = HTTPURLUtility.class.getSimpleName();

    public static String createGetQueryString(Collection<NameValuePair> pairs) {
        if (pairs == null)
            return "";

        StringBuilder builder = new StringBuilder();
        builder.append("?");

        Iterator<NameValuePair> iterator = pairs.iterator();
        while (iterator.hasNext()) {
            NameValuePair p = iterator.next();
            if (p.getName() == null) {
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

            builder.append(p.getName());
            builder.append(EQUALS);
            try {
                builder.append(URLEncoder.encode(p.getValue(), HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "Query string could not be created. Unsupported encoding.");
            }

            if (iterator.hasNext()) {
                builder.append(AMPERSAND);
            }
        }
        return builder.toString();
    }
    
    public static void close(Closeable s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Stream already closed.");
                }
            }
        }
    }

}
