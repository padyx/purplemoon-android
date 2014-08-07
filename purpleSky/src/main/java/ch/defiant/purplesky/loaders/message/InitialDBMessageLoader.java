package ch.defiant.purplesky.loaders.message;

import android.content.Context;
import android.os.Bundle;

import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.util.Holder;
/**
 * 
 * Loader to load the newest messages with a user from the database.
 * @author Patrick Bänziger
 *
 */
public class InitialDBMessageLoader extends AbstractMessageLoader {

    public InitialDBMessageLoader(Context c, Bundle args, IConversationAdapter adapter, IMessageService msgService) {
        super(c, R.id.loader_message_initial, args, adapter, msgService);
    }

    @Override
    public Holder<MessageResult> loadInBackground() {
        List<PrivateMessage> list = messageService.getNewestCachedMessagesWithUser(m_userId);
        return Holder.newInstance(new MessageResult().setUnreadMessages(list));
    }

}
