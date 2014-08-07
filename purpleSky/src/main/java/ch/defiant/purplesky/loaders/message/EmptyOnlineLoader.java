package ch.defiant.purplesky.loaders.message;

import android.content.Context;
import android.os.Bundle;

import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.util.Holder;

/**
 * Message loader that loads the newest messages of the conversation with the specified user.
 * Use when nothing is present locally.
 * @author Patrick Bänziger
 *
 */
public class EmptyOnlineLoader extends AbstractMessageLoader {

    /**
     * Constructor. Requires arguments, containing the {@link ArgumentConstants#ARG_USERID} value
     * @param c context
     * @param args Argument bundle
     */
    public EmptyOnlineLoader(Context c, Bundle args, IConversationAdapter adapter, IMessageService msgService) {
        super(c, R.id.loader_message_empty, args, adapter, msgService);
    }

    @Override
    public Holder<MessageResult> loadInBackground() {
        Holder<List<PrivateMessage>> res = messageService.getNewMessagesFromUser(m_userId, null);
        if(res.getException() != null){
            return new Holder<MessageResult>(res.getException());
        }
        return Holder.newInstance(new MessageResult().setUnreadMessages(res.getContainedObject()));
    }

}
