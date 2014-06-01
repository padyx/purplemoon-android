package ch.defiant.purplesky.loaders;

import android.content.Context;
import android.os.Bundle;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.IMessageService;

public class CachedUsernameLoader extends SimpleAsyncLoader<String> {

    private final IMessageService messageService;
    private String m_userId;

    public CachedUsernameLoader(Context c, Bundle b, IMessageService msgService){
        super(c, R.id.loader_username);
        messageService = msgService;
        m_userId = b.getString(ArgumentConstants.ARG_USERID);
        if(m_userId == null){
            throw new IllegalArgumentException("Missing userId");
        }
    }
    
    @Override
    public String loadInBackground() {
        return messageService.getUserNameForId(m_userId);
    }

}
