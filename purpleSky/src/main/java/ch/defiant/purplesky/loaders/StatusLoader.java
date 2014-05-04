package ch.defiant.purplesky.loaders;

import java.io.IOException;

import android.content.Context;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PurplemoonAPIAdapter;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

public class StatusLoader extends SimpleAsyncLoader<Object> {

    public StatusLoader(Context c) {
        super(c, R.id.loader_drawermenu_status);
    }

    @Override
    public Object loadInBackground() {
        try {
            return PurplemoonAPIAdapter.getInstance().getOwnOnlineStatus();
        } catch (IOException e) {
            return null;
        } catch (PurpleSkyException e) {
            return null;
        }
    }
}