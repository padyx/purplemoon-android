package ch.defiant.purplesky.loaders;

import android.support.annotation.NonNull;

import ch.defiant.purplesky.api.gallery.EnterPasswordResponse;
import ch.defiant.purplesky.beans.PictureFolder;

/**
* @author Patrick Bänziger
*/
public class EnterPasswordResponseComposite {

    private final EnterPasswordResponse m_response;
    private final PictureFolder m_folder;

    public EnterPasswordResponseComposite(@NonNull EnterPasswordResponse response, @NonNull PictureFolder folder){
        m_response = response;
        m_folder = folder;
    }

    @NonNull
    public PictureFolder getFolder() {
        return m_folder;
    }

    @NonNull
    public EnterPasswordResponse getResponse() {
        return m_response;
    }
}
