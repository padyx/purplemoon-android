package ch.defiant.purplesky.loaders;

import java.io.IOException;

import android.content.Context;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

public class ProfileImageLoader extends SimpleAsyncLoader<Object> {

    public ProfileImageLoader(Context c) {
        super(c, R.id.loader_drawermenu_profileimage);
    }

    @Override
    public Object loadInBackground() {
        MinimalUser user;
        try {
            user = PurpleSkyApplication.getContext().getUserService().getMinimalUser(PersistantModel.getInstance().getUserProfileId(), true);
            if (user != null) {
                if (user.getProfilePictureURLDirectory() == null) {
                    return null;
                } else {
                    return user.getProfilePictureURLDirectory().toString();
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        } catch (PurpleSkyException e) {
            return null;
        }
    }
}