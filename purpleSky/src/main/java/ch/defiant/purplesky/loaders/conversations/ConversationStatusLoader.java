package ch.defiant.purplesky.loaders.conversations;

import android.content.Context;
import android.os.Bundle;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;

public class ConversationStatusLoader extends SimpleAsyncLoader<Holder<UserMessageHistoryBean>> {

    private final IConversationAdapter apiAdapter;
    private String m_profileId;

    public ConversationStatusLoader(Context context, Bundle b, IConversationAdapter apiAdapter) {
        super(context, R.id.loader_conversationstatus);
        this.apiAdapter = apiAdapter;
        if(b==null){
            throw new IllegalArgumentException("No arguments");
        }
        if(!b.containsKey(ArgumentConstants.ARG_USERID)){
            throw new IllegalArgumentException("Missing user id");
        } 
        
        m_profileId = b.getString(ArgumentConstants.ARG_USERID);
    }

    @Override
    public Holder<UserMessageHistoryBean> loadInBackground() {
        try{
            UserMessageHistoryBean bean = apiAdapter.getConversationStatus(m_profileId);
            return Holder.newInstance(bean);
        }
        catch(Exception e){
            return new Holder<UserMessageHistoryBean>(e);
        }
    }

}
