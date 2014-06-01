package ch.defiant.purplesky.loaders.conversations;

import java.util.List;

import android.content.Context;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;

public abstract class AbstractConversationLoader extends SimpleAsyncLoader<Holder<List<UserMessageHistoryBean>>> {

    protected final IMessageService messageService;

    public AbstractConversationLoader(Context context, int type, IMessageService messageService) {
        super(context, type);
        this.messageService = messageService;
    }


}