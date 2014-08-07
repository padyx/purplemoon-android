package ch.defiant.purplesky.api.gallery.internal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.beans.Picture;
import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.util.DateUtility;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class GalleryJSONTranslator {

    public static final String TAG = GalleryJSONTranslator.class.getSimpleName();

    public static List<PictureFolder> translateToPictureFolders(JSONObject obj) {
        ArrayList<PictureFolder> list = new ArrayList<PictureFolder>();
        if (obj == null)
            return list;

        if (!obj.has(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID)) {
            Log.d(TAG, "Cannot translate: No profileId associated with pictureFolder");
            return list;
        }

        try {
            String profileId = obj.getString(PurplemoonAPIConstantsV1.JSON_USER_PROFILE_ID);

            // Get the folders
            if (obj.has(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_FOLDERS)) {
                JSONArray folders = obj.getJSONArray(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_FOLDERS);

                list = translateToPictureFolders(profileId, folders);
            }
        } catch (JSONException e) {
            Log.w(TAG, "Translation from JSON to PictureFolders failed", e);
        }
        return list;
    }

    private static Picture translateToPicture(JSONObject obj) {
        if (obj == null)
            return null;
        int pictureID = obj.optInt(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_ID, -1);
        String url = obj.optString(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_URL, null);

        if (pictureID == -1 || url == null)
            return null;
        Picture pic = new Picture();
        pic.setUrl(url);
        pic.setPictureId(pictureID);

        pic.setMaxWidth(obj.optInt(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_MAX_WIDTH));
        pic.setMaxHeight(obj.optInt(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_MAX_HEIGHT));

        long timestamp = obj.optLong(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_DATE, -1);
        if (timestamp != -1) {
            pic.setDate(DateUtility.getFromUnixTime(timestamp));
        }

        return pic;
    }

    public static ArrayList<PictureFolder> translateToPictureFolders(String profileId, JSONArray folders) {
        ArrayList<PictureFolder> list = new ArrayList<PictureFolder>();
        for (int i = 0, size = folders.length(); i < size; i++) {
            JSONObject jsonFolder = folders.optJSONObject(i);
            if (jsonFolder == null)
                continue;

            String id = jsonFolder.optString(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_ID, null);
            if (id == null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Missing folder id, cannot translate!");
                }
                continue;
            }

            PictureFolder folder = new PictureFolder();
            folder.setFolderId(id);
            folder.setProfileId(profileId);

            String name = jsonFolder.optString(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_NAME, "");
            folder.setName(name);

            int pictureCount = jsonFolder.optInt(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_COUNT, 0);
            folder.setDeclaredPictureCount(pictureCount);

            boolean isProtected = false;
            isProtected = jsonFolder.optBoolean(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PASSPROTECTED_BOOL, false);
            folder.setPasswordProtected(isProtected);

            boolean hasAccess = false;
            hasAccess = jsonFolder.optBoolean(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_ACCESSGRANTED_BOOL, true);
            folder.setAccessGranted(hasAccess);

            JSONArray pictures = jsonFolder.optJSONArray(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_PICTURE_ARRAY);
            if (pictures != null) {
                ArrayList<Picture> pictureList = new ArrayList<Picture>();
                for (int p = 0, psize = pictures.length(); p < psize; p++) {
                    JSONObject picture = pictures.optJSONObject(p);
                    if (picture != null) {
                        Picture pic = translateToPicture(picture);
                        if (pic != null)
                            pictureList.add(pic);
                    }
                }
                folder.setPictures(pictureList);
            }

            list.add(folder);
        }
        return list;
    }

}
