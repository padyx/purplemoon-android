package ch.defiant.purplesky.api.common;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;

/**
 * Translates the raw responses of Purplemoon (in an error case) and returns the error key (machine-readable part).
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public class ErrorJSONTranslator implements ITranslator<String,String> {

    private static final String TAG = ErrorJSONTranslator.class.getSimpleName();

    @Override
    public @Nullable String translate(@Nullable String source) {
        try {
            JSONObject errorObject = new JSONObject(source);
            return errorObject.optString(PurplemoonAPIConstantsV1.Errors.JSON_ERROR_TYPE, null);
        } catch (JSONException e) {
            Log.e(TAG, "Error message unparsable", e);
            return null;
        }
    }
}
