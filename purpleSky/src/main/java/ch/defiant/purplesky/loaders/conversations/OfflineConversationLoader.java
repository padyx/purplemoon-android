package ch.defiant.purplesky.loaders.conversations;

import java.util.List;

import android.content.Context;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.services.MessageService;
import ch.defiant.purplesky.util.Holder;

public class OfflineConversationLoader extends AbstractConversationLoader {

    public OfflineConversationLoader(Context context) {
        super(context, R.id.loader_chatlist_offline);
    }

    @Override
    public Holder<List<UserMessageHistoryBean>> loadInBackground() {
        List<UserMessageHistoryBean> conversations = MessageService.getCachedConversations();
        MessageService.injectProfilePictureUrlForId(conversations);
        return Holder.newInstance(conversations);
    }

}
