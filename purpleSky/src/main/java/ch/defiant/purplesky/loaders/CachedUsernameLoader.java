package ch.defiant.purplesky.loaders;

import android.content.Context;
import android.os.Bundle;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.services.MessageService;

public class CachedUsernameLoader extends SimpleAsyncLoader<String> {

    private String m_userId;

    public CachedUsernameLoader(Context c, Bundle b){
        super(c, R.id.loader_username);
        m_userId = b.getString(ArgumentConstants.ARG_USERID);
        if(m_userId == null){
            throw new IllegalArgumentException("Missing userId");
        }
    }
    
    @Override
    public String loadInBackground() {
        return MessageService.getUserNameForId(m_userId);
    }

}
