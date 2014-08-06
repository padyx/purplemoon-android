package ch.defiant.purplesky.api.gallery.internal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.defiant.purplesky.api.common.APINetworkUtility;
import ch.defiant.purplesky.api.gallery.IGalleryAdapter;
import ch.defiant.purplesky.api.internal.JSONTranslator;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public class GalleryAdapter implements IGalleryAdapter {

    @Override
    public List<PictureFolder> getPictureFolders(String profileId) throws IOException, PurpleSkyException {

        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(ReportAPIConstants.PICTUREFOLDER_FOLDERSONLY_URL);
        sb.append(profileId);

        JSONObject jsonObject = APINetworkUtility.performGETRequestForJSONObject(new URL(sb.toString()));
        return JSONTranslator.translateToPictureFolders(jsonObject);
    }

    @Override
    public List<PictureFolder> getMyPictureFolders() throws IOException, PurpleSkyException {
        PersistantModel model = PurpleSkyApplication.get().getPersistantModel();
        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(ReportAPIConstants.PICTUREFOLDER_FOLDERSONLY_ME_URL);

        JSONArray jsonArray = APINetworkUtility.performGETRequestForJSONArray(new URL(sb.toString()));
        return JSONTranslator.translateToPictureFolders(model.getUserProfileId(), jsonArray);
    }

    @Override
    public Map<String, PictureFolder> getFoldersWithPictures(String profileId, List<String> folders) throws IOException, PurpleSkyException {
        HashMap<String, PictureFolder> map = new HashMap<String, PictureFolder>();

        StringBuilder sb = new StringBuilder();
        sb.append(PurplemoonAPIConstantsV1.BASE_URL);
        sb.append(ReportAPIConstants.PICTUREFOLDER_WITHPICTURES_URL);
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
            return map;
        }
        List<PictureFolder> translatedFolders = JSONTranslator.translateToPictureFolders(obj);
        if (translatedFolders != null) {
            for (PictureFolder f : translatedFolders) {
                if (f == null)
                    continue;
                map.put(f.getFolderId(), f);
            }
        }
        return map;
    }

}
