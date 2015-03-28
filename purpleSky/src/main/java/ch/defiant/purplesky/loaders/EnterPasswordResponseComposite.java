package ch.defiant.purplesky.loaders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.defiant.purplesky.api.gallery.EnterPasswordResponse;
import ch.defiant.purplesky.beans.PictureFolder;

/**
* @author Patrick Bänziger
*/
public class EnterPasswordResponseComposite {

    private final EnterPasswordResponse m_response;
    private final PictureFolder m_folder;

    public EnterPasswordResponseComposite(@NonNull EnterPasswordResponse response, @Nullable PictureFolder folder){
        m_response = response;
        m_folder = folder;
    }

    @Nullable
    public PictureFolder getFolder() {
        return m_folder;
    }

    @NonNull
    public EnterPasswordResponse getResponse() {
        return m_response;
    }
}
