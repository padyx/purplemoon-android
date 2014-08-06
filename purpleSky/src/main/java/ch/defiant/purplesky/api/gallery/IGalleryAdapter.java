package ch.defiant.purplesky.api.gallery;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public interface IGalleryAdapter {

    /**
     * Returns the picture folders for the specified user.
     *
     * @param profileId
     * @return
     * @throws java.io.IOException
     * @throws ch.defiant.purplesky.exceptions.PurpleSkyException
     * @throws IllegalArgumentException
     *             If the profileId
     */
    public List<PictureFolder> getPictureFolders(String profileId) throws IOException, PurpleSkyException;

    public List<PictureFolder> getMyPictureFolders() throws IOException, PurpleSkyException;

    /**
     * Retrieves folders, with pictures associated.
     *
     * @param profileId
     *            For which profile to retrieve
     * @param folders
     *            (Optional) Restrict to particular folders
     * @return Map of Folders: Key = FolderId
     * @throws IOException
     * @throws PurpleSkyException
     */
    public Map<String, PictureFolder> getFoldersWithPictures(String profileId, List<String> folders)
            throws IOException, PurpleSkyException;
}
