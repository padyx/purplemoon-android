package ch.defiant.purplesky.api.gallery;

import java.io.IOException;
import java.util.List;

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
     * @param profileId Id of the user
     * @return List of picture folders, without contents.
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
    public List<PictureFolder> getFoldersWithPictures(String profileId, List<String> folders)
            throws IOException, PurpleSkyException;

    /**
     * Enter the password for the password protected folder.
     * @param profileId Users id
     * @param folderId  Folder id
     * @param password Password for the folder
     * @return Result of this operation
     * @throws IOException
     * @throws PurpleSkyException
     */
    public EnterPasswordResponse enterPassword(String profileId, String folderId, String password) throws IOException, PurpleSkyException;
}
