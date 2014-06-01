package ch.defiant.purplesky.loaders.conversations;

import android.content.Context;

import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.util.Holder;

public class OnlineConversationLoader extends AbstractConversationLoader {

    public OnlineConversationLoader(Context context, IMessageService msgService) {
        super(context, R.id.loader_chatlist_online, msgService);
    }

    @Override
    public Holder<List<UserMessageHistoryBean>> loadInBackground() {
        
        try {
            return Holder.newInstance(messageService.getOnlineConversations());
        } catch (Exception e) {
            return new Holder<List<UserMessageHistoryBean>>(e);
        }

    }

}
