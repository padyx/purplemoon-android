package ch.defiant.purplesky.loaders.message;

import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.util.Holder;

public class OlderMessageOnlineLoader extends AbstractMessageLoader {

    private final long m_upToMessageId;

    public OlderMessageOnlineLoader(Context c, Bundle args, IConversationAdapter adapter, IMessageService msgService) {
        super(c, R.id.loader_message_moreoldOnline, args, adapter, msgService);
        m_upToMessageId = args.getLong(ArgumentConstants.ARG_ID, -1);
        if(m_upToMessageId == -1L){
            throw new IllegalArgumentException("Missing id for bounding to load older messages");
        }
    }

    @Override
    public Holder<MessageResult> loadInBackground() {
        Holder<List<PrivateMessage>> holder = messageService.getPreviousMessagesOnline(m_userId, m_upToMessageId);
        if (holder.getException() != null){
            return new Holder<>(holder.getException());
        }
        List<PrivateMessage> obj = holder.getContainedObject();
        List<IPrivateMessage> messages = new ArrayList<IPrivateMessage>(obj);

        return Holder.newInstance(new MessageResult().setUnreadMessages(messages));
    }

}
