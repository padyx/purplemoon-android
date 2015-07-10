package ch.defiant.purplesky.loaders;

import java.io.IOException;

import android.content.Context;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

public class OwnUserLoader extends SimpleAsyncLoader<Object> {

    public OwnUserLoader(Context c) {
        super(c, R.id.loader_drawer_userbean_own);
    }

    @Override
    public MinimalUser loadInBackground() {
        try {
            return PurpleSkyApplication.get().getUserService().getMinimalUser(PersistantModel.getInstance().getUserProfileId(), true);
        } catch (IOException e) {
            return null;
        } catch (PurpleSkyException e) {
            return null;
        }
    }
}