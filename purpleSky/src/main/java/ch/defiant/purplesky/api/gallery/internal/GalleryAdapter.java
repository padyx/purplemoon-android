package ch.defiant.purplesky.api.gallery.internal;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.defiant.purplesky.api.common.APINetworkUtility;
import ch.defiant.purplesky.api.common.ErrorJSONTranslator;
import ch.defiant.purplesky.api.gallery.EnterPasswordResponse;
import ch.defiant.purplesky.api.gallery.IGalleryAdapter;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.HTTPURLResponseHolder;
import ch.defiant.purplesky.util.StringUtility;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class GalleryAdapter implements IGalleryAdapter {

    @Override
    public List<PictureFolder> getPictureFolders(String profileId) throws IOException, PurpleSkyException {

        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(GalleryAPIConstants.PICTUREFOLDER_FOLDERSONLY_URL);
        sb.append(profileId);

        JSONObject jsonObject = APINetworkUtility.performGETRequestForJSONObject(new URL(sb.toString()));
        return GalleryJSONTranslator.translateToPictureFolders(jsonObject);
    }

    @Override
    public List<PictureFolder> getMyPictureFolders() throws IOException, PurpleSkyException {
        PersistantModel model = PurpleSkyApplication.get().getPersistantModel();
        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(GalleryAPIConstants.PICTUREFOLDER_FOLDERSONLY_ME_URL);

        JSONArray jsonArray = APINetworkUtility.performGETRequestForJSONArray(new URL(sb.toString()));
        return GalleryJSONTranslator.translateToPictureFolders(model.getUserProfileId(), jsonArray);
    }

    @Override
    public List<PictureFolder> getFoldersWithPictures(String profileId, List<String> folders) throws IOException, PurpleSkyException {
        List<PictureFolder> list = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(GalleryAPIConstants.PICTUREFOLDER_WITHPICTURES_URL);
        sb.append(profileId);
        if (folders != null && folders.size() > 0) {
            sb.append("?");
            sb.append(PurplemoonAPIConstantsV1.JSON_PICTUREFOLDER_IDS);
            sb.append("=");
            for (int i = 0, size = folders.size(); i < size; i++) {
                sb.append(folders.get(i));
                if (i != size - 1) {
                    sb.append(",");
                }
            }
        }

        JSONObject obj = APINetworkUtility.performGETRequestForJSONObject(new URL(sb.toString()));
        if (obj == null) {
            return list;
        }
        List<PictureFolder> translatedFolders = GalleryJSONTranslator.translateToPictureFolders(obj);
        if (translatedFolders != null) {
            for (PictureFolder f : translatedFolders) {
                if (f == null)
                    continue;
                list.add(f);
            }
        }
        return list;
    }

    @Override
    public EnterPasswordResponse enterPassword(String profileId, String folderId, String password) throws IOException, PurpleSkyException {
        URL url = new URL(PurplemoonAPIConstantsV1.BASE_URL + GalleryAPIConstants.ENTERPASSWORD_URL + profileId + "/" + folderId);
        List<Pair<String,String>> body = Collections.singletonList(new Pair<>(GalleryAPIConstants.ENTERPASSWORD_PASSWORD_PARAM, password));
        HTTPURLResponseHolder response = APINetworkUtility.postForResponseHolderNoThrow(url, body, null);

        if(response.isSuccessful()){
            return EnterPasswordResponse.OK;
        } else {
            String errorString = new ErrorJSONTranslator().translate(response.getError());
            if(StringUtility.isNullOrEmpty(errorString)){
                return EnterPasswordResponse.ERROR;
            } else {
                return new EnterPasswordResponseTranslator().translate(errorString);
            }
        }
    }

}
