package ch.defiant.purplesky.loaders;

import android.content.Context;

import java.io.IOException;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

public class StatusLoader extends SimpleAsyncLoader<Object> {

    private final IPurplemoonAPIAdapter apiAdapter;

    public StatusLoader(Context c, IPurplemoonAPIAdapter apiAdapter) {
        super(c, R.id.loader_drawermenu_status);
        this.apiAdapter = apiAdapter;
    }

    @Override
    public Object loadInBackground() {
        try {
            return apiAdapter.getOwnOnlineStatus();
        } catch (IOException e) {
            return null;
        } catch (PurpleSkyException e) {
            return null;
        }
    }
}