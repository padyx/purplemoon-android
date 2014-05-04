package ch.defiant.purplesky.loaders.message;

import android.content.Context;
import android.os.Bundle;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;

public abstract class AbstractMessageLoader extends SimpleAsyncLoader<Holder<MessageResult>> {

    private final Bundle args;
    protected String m_userId;
    
    public AbstractMessageLoader(Context c, int type, Bundle args){
        super(c, type);
        this.args = args;

        if(args == null){
            throw new IllegalArgumentException("No arguments passed to loader!");
        }
        m_userId = args.getString(ArgumentConstants.ARG_USERID);
        if (m_userId == null){
            throw new IllegalArgumentException("Missing user id!");
        }
    }
    
    public Bundle getArguments(){
        return args;
    }
}
