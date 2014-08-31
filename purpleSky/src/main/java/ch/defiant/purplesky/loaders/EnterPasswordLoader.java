package ch.defiant.purplesky.loaders;

import android.content.Context;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.gallery.EnterPasswordResponse;
import ch.defiant.purplesky.api.gallery.IGalleryAdapter;
import ch.defiant.purplesky.util.Holder;

/**
 * @author Chakotay
 */
public class EnterPasswordLoader extends SimpleAsyncLoader<Holder<EnterPasswordResponse>> {

    private final IGalleryAdapter galleryAdapter;
    private final String profileId;
    private final String password;
    private final String folderId;

    public EnterPasswordLoader(IGalleryAdapter adapter, Context c, String profileId, String folderId, String password){
        super(c, R.id.loader_picturefolder_enterpassword);
        galleryAdapter = adapter;
        this.profileId = profileId;
        this.password = password;
        this.folderId = folderId;
    }

    @Override
    public Holder<EnterPasswordResponse> loadInBackground() {
        try {
            return Holder.of(galleryAdapter.enterPassword(profileId, folderId, password));
        } catch (Exception e) {
            return new Holder<EnterPasswordResponse>(e);
        }
    }
}
