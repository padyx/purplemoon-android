package ch.defiant.purplesky.translators;

import android.content.res.Resources;
import android.util.Log;
import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.constants.ProfileListMap;
import ch.defiant.purplesky.core.PurpleSkyApplication;

public class ProfileTranslator {

    private static final String REPLACEMENT_STRING = "[^a-zA-Z0-9_]";

    /**
     * This is the prefix for all the keys for key descriptions (like 'age') in the localization file for the profile.
     * {@code String name = PREFIXKEY + apiKey}. The name can be used to retrieve the localized key from the resources.
     */
    private static final String PREFIXKEY = "string/profileKeyText_";

    /**
     * This is the prefix for all the keys for value descriptions (like 'hetero') in the localization file for the profile.
     * {@code String name = PREFIXVALUE + apiKey + '_' + apiValue}
     */
    private static final String PREFIXVALUE = "string/profileValueText_";
    private static final String PACKAGE = PurpleSkyApplication.get().getPackageName();
    private static final String TAG = ProfileTranslator.class.getSimpleName();

    /**
     * Tries to translate the provided APIKey to a localized value.
     * 
     * @param apiKey
     * @return Localized value, or {@code null} if no localized value could be found
     */
    public static String translateAPIKey(String apiKey) {
        if (apiKey == null)
            return null;

        // Remove all non A-Za-z0-9 or underscore characters
        apiKey = apiKey.replaceAll(REPLACEMENT_STRING, "");

        Resources resources = PurpleSkyApplication.get().getResources();

        // Try to find
        int keyIdentifier = resources.getIdentifier(PREFIXKEY + apiKey, null, PACKAGE);

        if (keyIdentifier == 0) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "API Key '" + apiKey + "' unresolved");
            }
            return null;
        }
        return resources.getString(keyIdentifier);
    }

    public static String translateAPIValue(String apiKey, String apiValue) {
        if (apiKey == null || apiValue == null)
            return null;

        if (ProfileListMap.getInstance().hasSpecialHandling(apiKey)) {
            String value = ProfileListMap.getInstance().handle(apiKey, apiValue);
            if (value == null) {
                return null;
            }
            return value;
        }

        // Remove all non A-Za-z0-9 characters
        apiKey = apiKey.replaceAll(REPLACEMENT_STRING, "");
        String cleanedValue = apiValue.replaceAll(REPLACEMENT_STRING, "");

        Resources resources = PurpleSkyApplication.get().getResources();
        // TODO Maybe speed up this(getIdentifier)? No lookup if length exceeds certain value?
        int valueIdentifier = resources.getIdentifier(PREFIXVALUE + apiKey + '_' + cleanedValue, null, PACKAGE);
        if (valueIdentifier == 0) { // This may just be a userdefined value. So don't worry. Return untranslated
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "API Value '" + apiValue + "' for key '" + apiKey + "' unresolved");
            }
            return apiValue; // Copy it...
        } else {
            return resources.getString(valueIdentifier);
        }
    }

}
