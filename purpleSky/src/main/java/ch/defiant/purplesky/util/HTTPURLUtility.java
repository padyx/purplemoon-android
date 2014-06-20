package ch.defiant.purplesky.util;

import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.util.Pair;

import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.core.PersistantModel;

public class HTTPURLUtility {
    private static final String EQUALS = "=";
    private static final String AMPERSAND = "&";
    private static final String TAG = HTTPURLUtility.class.getSimpleName();
    private static final int CONNECT_TIMEOUT = 15000; // 15sec
    private static final int READ_TIMEOUT = 15000; // 15sec

    /**
     * Encodes a list of Name-Value-Pairs into a string, suitable for the body of a HTTP Post
     * 
     * @param params
     *            The NVPs
     * @param charset
     *            Which charset shall be used. UTF-8 will be used if null
     * @return Encoded String
     * @throws UnsupportedEncodingException
     */
    public static String encodeForPOST(List<NameValuePair> params, String charset) throws UnsupportedEncodingException {
        if (charset == null)
            charset = HTTP.UTF_8;

        StringBuilder sb = new StringBuilder();
        if (params != null) {
            for (NameValuePair pair : params) {
                if (sb.length() != 0)
                    sb.append(AMPERSAND);

                sb.append(URLEncoder.encode(pair.getName(), charset));
                sb.append(EQUALS);
                sb.append(URLEncoder.encode(pair.getValue(), charset));
            }
        }

        return sb.toString();
    }

    /**
     * Will perform a blocking HTTP Post request
     * 
     * @param url
     *            URL to post to
     * @param headers
     *            Headers to add to the request
     * @param body
     *            Content to send in message body.
     * @param charset
     *            Charset for both params and response
     * @param followRedirect
     *            Whether a redirection shall be followed (at most once)
     * @param context
     *            An SSLContext, for providing additional trusted certificates. Only applicable for SSL/TLS Connections
     * @return bean containing the response code and output or error
     * @throws IOException
     *             If the connection fails for some reason
     */
    public static HTTPURLResponseHolder doHTTPPost(URL url, List<NameValuePair> headers, String body, String charset, boolean followRedirect,
            SSLContext context) throws IOException {
        Log.d(TAG, "Performing POST on URL '"+url+"'");
        HttpURLConnection connection = getConfiguredConnection(url, headers, charset, context, true);
        
        // WORKAROUND for recycling issue...
        // http://stackoverflow.com/questions/19258518/
        connection.setRequestProperty("Connection", "close");
   
        connection.setRequestMethod("POST");
        int contentLength = 0;
        if (body != null) {
            contentLength = body.getBytes().length;
        }

        try {
            connection.setFixedLengthStreamingMode(contentLength);
            BufferedOutputStream stream = null;
            try {
                OutputStream outputStream = null;
                if (body != null) {
                    outputStream = connection.getOutputStream();
                    stream = new BufferedOutputStream(outputStream);
                    stream.write(body.getBytes());
                    stream.flush();
                }
            } finally {
                close(stream);
            }

            InputStream inputStream = connection.getInputStream();
            HTTPURLResponseHolder holder = new HTTPURLResponseHolder();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "POST on URL '"+url+"' returned http code "+responseCode);
            holder.setResponseCode(responseCode);

            // Normal case:
            String o = StreamUtility.inputStreamToString(inputStream, charset);
            close(inputStream);
            holder.setOutput(o);
            return holder;
        } catch (IOException e) {
            return handleIOException(url, headers, charset, false, context, connection);
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Performs a post request. Specially suited for large uploads as it can handle InputStreams .
     * 
     * @param url
     *            The url to post to
     * @param headers
     *            HTTP Headers to send
     * @param unencodedBody
     *            List of Name-Value Pairs. Value pairs can be either: Strings or {@link InputStream}s
     * @param charset
     * @param followRedirect
     *            Whether to follow a redirect
     * @param context
     *            An SSL Context to set
     * @return ResponseHolder
     * @throws IOException
     *             If an error occurs.
     * @throws IllegalArgumentException
     *             If a value in the unencodedBody parameter is neither string nor Inputstream, or if the name is empty or null.
     */
    public static HTTPURLResponseHolder doHTTPPost(URL url, List<NameValuePair> headers, List<Pair<String, Object>> unencodedBody, String charset,
            boolean followRedirect, SSLContext context) throws IOException {
        HttpURLConnection connection = getConfiguredConnection(url, headers, charset, context, true);
        
        // WORKAROUND for recycling issue...
        // http://stackoverflow.com/questions/19258518/
        connection.setRequestProperty("Connection", "close");
        
        Log.d(TAG, "Performing POST on URL '"+url+"'");

        connection.setChunkedStreamingMode(0); // Don't know in advance
        BufferedOutputStream stream = null;
        try {
            if (unencodedBody != null) {
                OutputStream outputStream = connection.getOutputStream();
                stream = new BufferedOutputStream(outputStream);

                // Go though the parameters
                for (int i = 0, size = unencodedBody.size(); i < size; i++) {
                    Pair<String, Object> pair = unencodedBody.get(i);
                    if (pair == null || StringUtility.isNullOrEmpty(pair.first)) {
                        throw new IllegalArgumentException("Non-empty name or null parameter pair when using POST!");
                    }

                    if (pair.second == null) {
                        // Just upload it as empty
                        stream.write((URLEncoder.encode(pair.first, charset) + EQUALS + URLEncoder.encode("", charset))
                                .getBytes());
                    } else if (pair.second instanceof String) {
                        stream.write((URLEncoder.encode(pair.first, charset) + EQUALS + URLEncoder.encode(
                                (String) pair.second, charset)).getBytes());
                    } else if (pair.second instanceof InputStream) {
                        InputStream is = (InputStream) pair.second;
                        stream.write((URLEncoder.encode(pair.first, charset) + EQUALS).getBytes());

                        Base64OutputStream basestream = new Base64OutputStream(stream, Base64.NO_WRAP);
                        try {
                            byte[] read = new byte[1024];
                            while ((is.read(read, 0, 1024)) != -1) {
                                basestream.write(read); // Write byte for byte...
                            }
                        } finally {
                            basestream.close();
                        }
                    } else {
                        throw new IllegalArgumentException("Value of pair was neither String nor Inputstream!");
                    }

                    if (i != size - 1) {
                        stream.write(AMPERSAND.getBytes());
                    }
                }

                stream.flush();
            }
        } finally {
            close(stream); // Closes target stream too
        }

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            HTTPURLResponseHolder holder = new HTTPURLResponseHolder();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "POST on URL '"+url+"' returned http code "+responseCode);
            holder.setResponseCode(responseCode);

            // Normal case:
            String o = StreamUtility.inputStreamToString(inputStream, charset);
            holder.setOutput(o);
            return holder;
        } catch (IOException e) {
            return handleIOException(url, headers, charset, false, context, connection);
        } finally {
            close(inputStream);
            connection.disconnect();
        }
    }

    /**
     * Will perform a blocking HTTP Get request, passing along the requested additional headers.
     * 
     * @param url
     *            URL to get
     * @param charset
     *            Charset for both params and response
     * @param followRedirect
     *            Whether a redirection shall be followed (at most once)
     * @param httpHeaders
     *            List of headers to attach to the request
     * @param context
     *            An SSLContext, for providing additional trusted certificates. Only applicable for SSL/TLS Connections
     * @return bean containing the response code and output or error
     * @throws IOException
     *             If the connection fails for some reason
     */
    public static HTTPURLResponseHolder doHTTPGet(URL url, List<NameValuePair> httpHeaders, String charset, boolean followRedirect, SSLContext context)
            throws IOException {
        HttpURLConnection connection = getConfiguredConnection(url, httpHeaders, charset, context, false);
        Log.d(TAG, "Performing GET on URL '"+url+"'");

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();

            HTTPURLResponseHolder holder = new HTTPURLResponseHolder();
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "GET on URL '"+url+"' returned http code "+responseCode);
            holder.setResponseCode(responseCode);

            String o = StreamUtility.inputStreamToString(inputStream, charset);
            holder.setOutput(o);

            return holder;
        } catch (IOException e) {
            return handleIOException(url, httpHeaders, charset, followRedirect, context, connection);
        } finally {
            close(inputStream);
            connection.disconnect();
        }
    }

    private static HTTPURLResponseHolder handleIOException(URL url, List<NameValuePair> httpHeaders, String charset, boolean followRedirect,
            SSLContext context, HttpURLConnection connection) throws IOException {
        // Exception! Check Error stream and the response code

        HTTPURLResponseHolder holder = new HTTPURLResponseHolder();

        String error = StreamUtility.inputStreamToString(connection.getErrorStream(), charset);
        holder.setError(error);

        int responseCode = connection.getResponseCode();
        holder.setResponseCode(responseCode);

        switch (responseCode) {
        // Case(s) moved
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_MOVED_PERM: {
                if (!followRedirect) {
                    throw new IOException("Unexpected/unallowed redirect when at resource '" + url + "'");
                }

                String location = connection.getHeaderField("Location");
                if (location == null) {
                    Log.w(TAG, "Could not get redirect header for response code " + responseCode + " for url " + url);
                    throw new IOException("Network error: Invalid redirection");
                }
                Log.d(TAG, "Redirection of request from '" + url);
                holder = HTTPURLUtility.doHTTPGet(url, httpHeaders, charset, false, context);
                break;
            }
            default: {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "URL request failed with response code " + responseCode);
                    Log.d(TAG, "Status line was: " + error);
                }

            }
        }
        return holder;
    }

    /**
     * Returns a unopened {@link HttpURLConnection} already configured with the specified headers, context and charset.
     * 
     * @param url
     * @param httpHeaders
     * @param charset
     * @param context
     * @param doOutput
     * @return
     * @throws IOException
     */
    static HttpURLConnection getConfiguredConnection(URL url, List<NameValuePair> httpHeaders, String charset, SSLContext context, boolean doOutput)
            throws IOException {
        if (url == null || url.getProtocol() == null || !url.getProtocol().toLowerCase(Locale.US).startsWith("http")) {
            throw new IllegalArgumentException("Invalid HTTP URL: " + url);
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setDoInput(true); // Read
        if (doOutput) {
            connection.setDoOutput(doOutput);
        }

        // Workaround for older Android versions who do not do that automatically (2.3.5 for example)
        connection.setRequestProperty(HTTP.TARGET_HOST, url.getHost());
        connection.setRequestProperty("Accept-Encoding", charset);

        // Langauge
        String language = Locale.getDefault().getLanguage();
        if (StringUtility.isNullOrEmpty(language)) {
            language = Locale.US.getLanguage();
        }
        connection.setRequestProperty("Accept-Language", language);

        // Only send token if url is purplemoon...
        if (url.toString().startsWith(PurplemoonAPIConstantsV1.BASE_URL)) {
            String token = PersistantModel.getInstance().getOAuthAccessToken();
            if (StringUtility.isNullOrEmpty(token)) {
                if (BuildConfig.DEBUG)
                    Log.w(TAG, "Requesting URL " + url + " without token");
            } else {
                connection.setRequestProperty(PurplemoonAPIConstantsV1.AUTH_HEADER_NAME, PurplemoonAPIConstantsV1.AUTH_HEADER_VALUEPREFIX + token);
            }
        }

        // Set SSL Context
        if (context != null && connection instanceof HttpsURLConnection) {
            HttpsURLConnection conn = (HttpsURLConnection) connection;
            conn.setSSLSocketFactory(context.getSocketFactory());
        }

        // Add the headers, if they exist
        if (httpHeaders != null) {
            for (NameValuePair nvp : httpHeaders) {
                if (nvp != null) {
                    connection.setRequestProperty(nvp.getName(), nvp.getValue());
                }
            }
        }
        return connection;
    }

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
