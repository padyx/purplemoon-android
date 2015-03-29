package ch.defiant.purplesky.loaders;

import android.content.Context;

import java.util.Collections;
import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.gallery.EnterPasswordResponse;
import ch.defiant.purplesky.api.gallery.IGalleryAdapter;
import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.util.CollectionUtil;
import ch.defiant.purplesky.util.Holder;

/**
 * @author Patrick Bänziger
 */
public class EnterPasswordLoader extends SimpleAsyncLoader<Holder<EnterPasswordResponseComposite>> {

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
    public Holder<EnterPasswordResponseComposite> loadInBackground() {
        try {
            EnterPasswordResponse response = galleryAdapter.enterPassword(profileId, folderId, password);
            if(response == EnterPasswordResponse.OK){
                List<PictureFolder> folder = galleryAdapter.getFoldersWithPictures(profileId, Collections.singletonList(folderId));
                if(CollectionUtil.isEmpty(folder)){
                    return Holder.of(new EnterPasswordResponseComposite(EnterPasswordResponse.FOLDER_UNAVAILABLE, null));
                } else {
                    return Holder.of(new EnterPasswordResponseComposite(response, CollectionUtil.firstElement(folder)));
                }
            } else {
                return Holder.of(new EnterPasswordResponseComposite(response, null));
            }
        } catch (Exception e) {
            return new Holder<EnterPasswordResponseComposite>(e);
        }
    }

}
