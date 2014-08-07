package ch.defiant.purplesky.api.postits.internal;

import org.json.JSONObject;

import java.util.Date;

import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.beans.PostIt;
import ch.defiant.purplesky.util.DateUtility;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class PostitJSONTranslator {

    public static PostIt translateToPostIt(JSONObject obj, Date lastcheck) {
        if (obj == null) {
            return null;
        }
        PostIt postIt = new PostIt();

        int id = obj.optInt(PurplemoonAPIConstantsV1.JSON_POSTIT_ID, -1);
        if (id != -1) {
            postIt.setId(id);
        }
        String text = obj.optString(PurplemoonAPIConstantsV1.JSON_POSTIT_TEXT, null);
        if (text != null) {
            postIt.setText(text);
        }
        boolean custom = obj.optBoolean(PurplemoonAPIConstantsV1.JSON_POSTIT_CUSTOM_BOOLEAN);
        postIt.setCustom(custom);

        long timeCreated = obj.optLong(PurplemoonAPIConstantsV1.JSON_POSTIT_CREATE_TIMESTAMP, -1);
        if (timeCreated != -1) {
            postIt.setDate(DateUtility.getFromUnixTime(timeCreated));
        }

        boolean isNew = false;
        if (timeCreated != -1 && lastcheck != null) {
            isNew = DateUtility.getFromUnixTime(timeCreated).after(lastcheck);
        }
        postIt.setNew(isNew);

        return postIt;
    }
}
