package ch.defiant.purplesky.loaders.conversations;

import java.util.List;

import android.content.Context;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.services.MessageService;
import ch.defiant.purplesky.util.Holder;

public class OnlineConversationLoader extends AbstractConversationLoader {

    public OnlineConversationLoader(Context context) {
        super(context, R.id.loader_chatlist_online);
    }

    @Override
    public Holder<List<UserMessageHistoryBean>> loadInBackground() {
        
        try {
            return Holder.newInstance(MessageService.getOnlineConversations());
        } catch (Exception e) {
            return new Holder<List<UserMessageHistoryBean>>(e);
        }

    }

}
