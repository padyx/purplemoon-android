package ch.defiant.purplesky.api.common;

import org.json.JSONObject;

/**
 * @author Patrick Baenziger
 */
public final class JSONUtility {

    private JSONUtility(){}

    public static Integer optInt(JSONObject obj, String name, Integer fallback){
        if (obj.has(name)){
            return obj.optInt(name);
        } else {
            return fallback;
        }
    }

}
