package ch.defiant.purplesky.api.common;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.core.PersistantModel;
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

    private static void addAuthenticationHeader(Request.Builder builder, URL resource) {
        if (resource.toString().startsWith(PurplemoonAPIConstantsV1.BASE_URL)) { // TODO Rewrite this
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
