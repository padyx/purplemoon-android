package ch.defiant.purplesky.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.util.StringUtility;

public class PictureFolder implements Serializable {

    private static final long serialVersionUID = -7153656231945900429L;

    private String m_profileId;

    private String m_folderId;
    private String m_name;
    private int m_pictureCount;
    private boolean m_passwordProtected;
    private boolean m_accessGranted;

    private List<Picture> m_pictures = Collections.emptyList();

    private int m_declaredPictureCount;

    /**
     * Default constructor.
     */
    public PictureFolder() {
    }

    public PictureFolder(String profileId, String folderId, String name) {
        setProfileId(profileId);
        setFolderId(folderId);
        setName(name);
    }

    public String getProfileId() {
        return m_profileId;
    }

    public void setProfileId(String profileId) {
        m_profileId = profileId;
    }

    public String getFolderId() {
        return m_folderId;
    }

    public void setFolderId(String folderId) {
        m_folderId = folderId;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public int getPictureCount() {
        return m_pictureCount;
    }

    public boolean isPasswordProtected() {
        return m_passwordProtected;
    }

    public void setPasswordProtected(boolean passwordProtected) {
        m_passwordProtected = passwordProtected;
    }

    public boolean isAccessGranted() {
        return m_accessGranted;
    }

    public void setAccessGranted(boolean accessGranted) {
        m_accessGranted = accessGranted;
    }

    @Override
    public String toString() {
        if (StringUtility.isNullOrEmpty(getName())) {
            return PurpleSkyApplication.getContext().getString(R.string.UnknownFolder);
        } else {
            return getName();
        }
    }

    public List<Picture> getPictures() {
        return m_pictures;
    }

    public void setPictures(List<Picture> pictures) {
        m_pictures = pictures;
        m_pictureCount = m_pictures.size();
    }

    public void setDeclaredPictureCount(int pictureCount) {
        m_declaredPictureCount = pictureCount;
    }

    public int getDeclaredPictureCount() {
        return m_declaredPictureCount;
    }
}
