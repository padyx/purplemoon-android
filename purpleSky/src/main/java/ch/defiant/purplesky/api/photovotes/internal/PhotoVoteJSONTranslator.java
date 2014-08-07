package ch.defiant.purplesky.api.photovotes.internal;

import org.json.JSONObject;

import ch.defiant.purplesky.api.common.CommonJSONTranslator;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PhotoVoteBean;
import ch.defiant.purplesky.util.DateUtility;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class PhotoVoteJSONTranslator {

    /**
     * Translates the JSON input to a photovote bean. The previous vote is set if present, as is the user bean unless <code>userclazz</code> is null
     *
     * @param obj
     *            JSON input
     * @param userclazz
     *            The class that the user object shall be translated to
     * @return The translated photo vote beans
     */
    public static PhotoVoteBean translateToPhotoVoteBean(JSONObject obj, Class<? extends MinimalUser> userclazz) {
        if (obj == null) {
            return null;
        }

        PhotoVoteBean b = new PhotoVoteBean();
        b.setVoteId(obj.optLong(PhotoVoteAPIConstants.JSON_PHOTOVOTE_VOTEID));
        b.setMaxHeight(obj.optInt(PhotoVoteAPIConstants.JSON_PHOTOVOTE_MAXHEIGHT));
        b.setMaxWidth(obj.optInt(PhotoVoteAPIConstants.JSON_PHOTOVOTE_MAXWIDTH));
        b.setPictureUrlPrefix(obj.optString(PhotoVoteAPIConstants.JSON_PHOTOVOTE_PICTUREURL, null));
        b.setPosX(obj.optDouble(PhotoVoteAPIConstants.JSON_PHOTOVOTE_XPOS_FLOAT));
        b.setPosY(obj.optDouble(PhotoVoteAPIConstants.JSON_PHOTOVOTE_YPOS_FLOAT));
        b.setTimestamp(DateUtility.getFromUnixTime(obj.optLong(PhotoVoteAPIConstants.JSON_PHOTOVOTE_CREATED)));
        b.setVerdict(PhotoVoteAPIUtility.toPhotoVoteVerdict(obj.optInt(PhotoVoteAPIConstants.JSON_PHOTOVOTE_VERDICT)));
        if (userclazz != null && obj.has(PhotoVoteAPIConstants.JSON_PHOTOVOTE_USER)) {
            b.setUser(CommonJSONTranslator.translateToUser(obj.optJSONObject(PhotoVoteAPIConstants.JSON_PHOTOVOTE_USER), userclazz));
        }
        if (obj.has(PhotoVoteAPIConstants.JSON_PHOTOVOTE_PREVIOUS)) {
            b.setPreviousVote(translateToPhotoVoteBean(obj.optJSONObject(PhotoVoteAPIConstants.JSON_PHOTOVOTE_PREVIOUS), userclazz));
        }
        return b;
    }

}
