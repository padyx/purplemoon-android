package ch.defiant.purplesky.loaders.conversations;

import android.content.Context;

import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.util.Holder;

public class OfflineConversationLoader extends AbstractConversationLoader {

    public OfflineConversationLoader(Context context, IMessageService msgService) {
        super(context, R.id.loader_chatlist_offline, msgService);
    }

    @Override
    public Holder<List<UserMessageHistoryBean>> loadInBackground() {
        List<UserMessageHistoryBean> conversations = messageService.getCachedConversations();
        messageService.injectProfilePictureUrlForId(conversations);
        return Holder.newInstance(conversations);
    }

}
