package ch.defiant.purplesky.core;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1.Errors;
import ch.defiant.purplesky.exceptions.PoweruserException;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.exceptions.WrongCredentialsException;

public class ErrorTranslator {

    public static final String TAG = ErrorTranslator.class.getSimpleName();

    public static void translateHttpError(Context c, int responseCode, String errorOut, String request) throws PurpleSkyException {
        if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            // General server difficulty
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Internal server error reported. Request was: '" + request + "'. Response was: " + errorOut);
                throwGenericException(c);
            }
        } else if (responseCode == 501) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Server indicated that API part does not exist! Request was: '" + request + "'. Response was: " + errorOut);
                throwGenericException(c);
            }
        } else if (responseCode == HttpURLConnection.HTTP_PAYMENT_REQUIRED) {
            throw new PoweruserException(c, R.string.Error_NoPowerUserGeneric);
        }
        else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
            try {
                JSONObject obj = new JSONObject(errorOut);
                parseForbiddenErrorText(c, obj);
            } catch (JSONException e) {
                // Unknown error
                throwGenericException(c);
            }
        } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            try {
                parseBadRequestErrorText(c, new JSONObject(errorOut));
            } catch (JSONException e) {
                throwGenericException(c);
            }
        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new WrongCredentialsException();
        } else {
            throwGenericException(c);
        }
    }

    private static void parseForbiddenErrorText(Context c, JSONObject obj) throws PurpleSkyException {
        String err = obj.optString(Errors.JSON_ERROR_TYPE, null);
        if (err == null) {
            throwGenericException(c);
        } else if (Errors.JSON_ERROR_TYPE_BLOCKINGUSER.equals(err)) {
            throw new PurpleSkyException(c, R.string.ErrorActionYouAreBlocking);
        } else if (Errors.JSON_ERROR_TYPE_BLOCKEDBYOTHER.equals(err)) {
            throw new PurpleSkyException(c, R.string.ErrorActionBeingBlocked);
        } else {
            throwGenericException(c);
        }
    }

    public static void throwGenericException(Context c) throws PurpleSkyException {
        throw new PurpleSkyException(c, R.string.UnknownErrorOccured);
    }

    private static void parseBadRequestErrorText(Context c, JSONObject obj) throws PurpleSkyException {
        String err = obj.optString(Errors.JSON_ERROR_TYPE, null);
        if (err == null) {
            throwGenericException(c);
        } else if (Errors.JSON_ERROR_TYPE_NOTEXT.equals(err)) {
            throw new PurpleSkyException(c, R.string.ErrorGenericNoText);
        } else if (Errors.JSON_ERROR_TYPE_TEXTTOOLONG.equals(err)) {
            throw new PurpleSkyException(c, R.string.ErrorGenericTextTooLong);
        } else if (Errors.JSON_ERROR_TYPE_TOOMANYPOSTITSGENERAL.equals(err)) {
            throw new PurpleSkyException(c, R.string.ErrorPostitsTooManyGeneral);
        } else if (Errors.JSON_ERROR_TYPE_TOOMANYPOSTITSWITHUSER.equals(err)) {
            throw new PurpleSkyException(c, R.string.ErrorPostitsTooManyWithUser);
        } else if (Errors.JSON_ERROR_TYPE_USERNOTFOUND.equals(err)) {
            throw new PurpleSkyException(c, R.string.ErrorGenericUserNotFound);
        } else {
            throwGenericException(c);
        }
    }

}
