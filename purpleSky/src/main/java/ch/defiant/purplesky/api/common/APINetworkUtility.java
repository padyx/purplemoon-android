package ch.defiant.purplesky.api.common;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.core.ErrorTranslator;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.HTTPURLResponseHolder;
import ch.defiant.purplesky.util.StringUtility;

/**
 * Utility that provides networking functions for the API. Not to be used outside of the api package.
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public class APINetworkUtility {

    public static final String TAG = APINetworkUtility.class.getSimpleName();

    public static HTTPURLResponseHolder postForResponseHolderNoThrow(URL resource, List<NameValuePair> postBody, List<NameValuePair> headrs)
            throws IOException, PurpleSkyException {
        Request.Builder builder = new Request.Builder();
        builder.url(resource);
        if(headrs != null){
            for(NameValuePair pair : headrs){
                builder.addHeader(pair.getName(), pair.getValue());
            }
        }

        addLanguageHeader(builder);
        addAuthenticationHeader(builder, resource);

        FormEncodingBuilder formBuilder = new FormEncodingBuilder();
        if(postBody != null){
            for(NameValuePair pair : postBody){
                formBuilder.add(pair.getName(), pair.getValue());
            }
        }
        builder.post(formBuilder.build());
        Response response = new OkHttpClient().newCall(builder.build()).execute();

        HTTPURLResponseHolder holder = new HTTPURLResponseHolder();
        holder.setResponseCode(response.code());
        holder.setSuccessful(response.isSuccessful());
        if(holder.isSuccessful()){
            holder.setOutput(response.body().string());
        } else {
            holder.setError(response.body().string());
        }

        return holder;
    }

    public static ApiResponse<String> postForString(URL resource, List<NameValuePair> postBody, List<NameValuePair> headrs)
            throws IOException, PurpleSkyException {

        Request.Builder builder = new Request.Builder();
        builder.url(resource);
        if(headrs != null){
            for(NameValuePair pair : headrs){
                builder.addHeader(pair.getName(), pair.getValue());
            }
        }

        addLanguageHeader(builder);
        addAuthenticationHeader(builder, resource);

        FormEncodingBuilder formBuilder = new FormEncodingBuilder();
        if(postBody != null){
            for(NameValuePair pair : postBody){
                formBuilder.add(pair.getName(), pair.getValue());
            }
        }
        builder.post(formBuilder.build());
        Response response = new OkHttpClient().newCall(builder.build()).execute();

        return createStringApiResponse(resource, response);
    }

    public static ApiResponse<JSONObject> postForJSONObject(
            URL resource, List<NameValuePair> postBody, List<NameValuePair> headers)
            throws IOException, PurpleSkyException {

        ApiResponse<String> response = postForString(resource, postBody, headers);
        try{
            if(response.isError()) {
                return new ApiResponse<JSONObject>(response.getResponseCode(), response.getError(), response.getErrorDescription());
            } else {
                JSONObject object = new JSONObject(response.getResult());
                return new ApiResponse<JSONObject>(response.getResponseCode(), object);
            }
        } catch(JSONException e){
            // Could not translate...
            return new ApiResponse<JSONObject>(response.getResponseCode(), new JSONObject());
        }
    }

    private static ApiResponse<String> createStringApiResponse(URL resource, Response response) throws IOException, PurpleSkyException {
        int responseCode = response.code();
        if(response.isSuccessful()){
            return new ApiResponse<String>(responseCode, response.body().string());
        } else {
            String body = response.body().string();
            ErrorTranslator.throwIfGenericException(PurpleSkyApplication.get(), responseCode, body, resource.toString());
            try{
                JSONObject obj = new JSONObject(body);
                return new ApiResponse<String>(
                        responseCode,
                        obj.optString(PurplemoonAPIConstantsV1.Errors.JSON_ERROR_TYPE),
                        obj.optString(PurplemoonAPIConstantsV1.Errors.JSON_ERROR_DESCRIPTION));
            } catch (JSONException e){
                return new ApiResponse<String>(responseCode, "", "");
            }
        }
    }

    public static JSONObject performGETRequestForJSONObject(URL resource) throws IOException, PurpleSkyException {
        String response = performGETRequestForString(resource);
        if (response == null)
            return null;
        try {
            return new JSONObject(response);
        } catch (JSONException e) {
            // Oops, we can't parse that
            Log.w(TAG, "Response from server is not a JSON parsable object.");
            return null;
        }
    }

    /**
     * Will perform a request for the given resource and return the output from the server. Will use HTTP Basic authentication if one of the
     * parameters (username, password) is non-empty
     *
     * @param resource
     *            The URL to query
     * @return String if successful. null otherwise.
     * @throws IOException
     *             If the connection failed.
     * @throws ch.defiant.purplesky.exceptions.WrongCredentialsException
     *             If the users credentials were rejected.
     */
    public static String performGETRequestForString(URL resource) throws IOException, PurpleSkyException {
        OkHttpClient httpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();

        addLanguageHeader(builder);
        addAuthenticationHeader(builder, resource);

        Request request = builder.url(resource).build();
        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful()){
            return response.body().string();
        } else {
            ErrorTranslator.translateHttpError(PurpleSkyApplication.get(), response.code(), response.body().string(), resource.toString());
        }
        // We should never get here - translator will throw above
        return StringUtility.EMPTY_STRING;
    }


    public static JSONArray performGETRequestForJSONArray(URL resource) throws IOException, PurpleSkyException {
        String response = performGETRequestForString(resource);
        if (response == null)
            return null;
        try {
            return new JSONArray(response);
        } catch (JSONException e) {
            // Oops, we can't parse that
            Log.w(TAG, "Response from server is not a JSON parsable array.");
            return null;
        }
    }

    public static ApiResponse<String> getString(URL resource) throws IOException, PurpleSkyException {
        OkHttpClient httpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();

        addLanguageHeader(builder);
        addAuthenticationHeader(builder, resource);

        Request request = builder.url(resource).build();
        Response response = httpClient.newCall(request).execute();
        return createStringApiResponse(resource, response);
    }

    public static ApiResponse<JSONArray> getJSONArray(URL resource) throws IOException, PurpleSkyException {
        ApiResponse<String> response = getString(resource);
        try{
            if(response.isError()) {
                return new ApiResponse<JSONArray>(response.getResponseCode(), response.getError(), response.getErrorDescription());
            } else {
                JSONArray array = new JSONArray(response.getResult());
                return new ApiResponse<JSONArray>(response.getResponseCode(), array);
            }
        } catch(JSONException e){
            // Could not translate...
            return new ApiResponse<JSONArray>(response.getResponseCode(), new JSONArray());
        }
    }

    public static ApiResponse<JSONObject> getJSONObject(URL resource) throws IOException, PurpleSkyException {
        ApiResponse<String> response = getString(resource);
        try{
            if(response.isError()) {
                return new ApiResponse<JSONObject>(response.getResponseCode(), response.getError(), response.getErrorDescription());
            } else {
                JSONObject array = new JSONObject(response.getResult());
                return new ApiResponse<JSONObject>(response.getResponseCode(), array);
            }
        } catch(JSONException e){
            // Could not translate...
            return new ApiResponse<JSONObject>(response.getResponseCode(), new JSONObject());
        }
    }

    public static HTTPURLResponseHolder performPOSTRequestForResponseHolder(URL resource, List<NameValuePair> postBody, List<NameValuePair> headrs)
            throws IOException, PurpleSkyException {
        Request.Builder builder = new Request.Builder();
        builder.url(resource);
        if(headrs != null){
            for(NameValuePair pair : headrs){
                builder.addHeader(pair.getName(), pair.getValue());
            }
        }

        addLanguageHeader(builder);
        addAuthenticationHeader(builder, resource);

        FormEncodingBuilder formBuilder = new FormEncodingBuilder();
        if(postBody != null){
            for(NameValuePair pair : postBody){
                formBuilder.add(pair.getName(), pair.getValue());
            }
        }
        builder.post(formBuilder.build());
        Response response = new OkHttpClient().newCall(builder.build()).execute();

        HTTPURLResponseHolder holder = new HTTPURLResponseHolder();
        holder.setResponseCode(response.code());
        holder.setSuccessful(response.isSuccessful());
        if(response.isSuccessful()){
            holder.setOutput(response.body().string());
        } else {
            holder.setError(response.body().string());
            ErrorTranslator.translateHttpError(PurpleSkyApplication.get(), response.code(), holder.getError(), resource.toString());
        }

        return holder;
    }

    private static void addAuthenticationHeader(Request.Builder builder, URL resource) {
        if (PurplemoonAPIConstantsV1.HOST.equalsIgnoreCase(resource.getHost())) { // TODO Rewrite this
            String token = PersistantModel.getInstance().getOAuthAccessToken();
            if (StringUtility.isNullOrEmpty(token)) {
                if (BuildConfig.DEBUG)
                    Log.w(TAG, "Requesting URL " + resource + " without token");
            } else {
                builder.addHeader(PurplemoonAPIConstantsV1.AUTH_HEADER_NAME, PurplemoonAPIConstantsV1.AUTH_HEADER_VALUEPREFIX + token);
            }
        }
    }

    private static void addLanguageHeader(Request.Builder builder) {
        String language = Locale.getDefault().getLanguage();
        if (StringUtility.isNullOrEmpty(language)) {
            language = Locale.US.getLanguage();
        }
        builder.addHeader("Accept-Language", language);
    }
}
