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

/**
 * Loads new messages with a user after a certain message. Requires the other users profileId (as String in {@link ArgumentConstants#ARG_USERID}) and
 * the messageId (as long in {@link ArgumentConstants#ARG_ID}).
 * 
 * @author Patrick Bänziger
 * 
 */
public class RefreshMessageLoader extends AbstractMessageLoader {

    private long m_sinceMsgId;

    public RefreshMessageLoader(Context c, Bundle args, IConversationAdapter adapter, IMessageService msgService) {
        super(c, R.id.loader_message_refresh, args, adapter, msgService);

        m_sinceMsgId = args.getLong(ArgumentConstants.ARG_ID, -1L);
        if (m_sinceMsgId == -1) {
            throw new IllegalArgumentException("Missing Message Id");
        }
    }

    @Override
    public Holder<MessageResult> loadInBackground() {
        boolean loadMore = true;
        List<IPrivateMessage> overallList = new ArrayList<>();

        while (loadMore) {
            loadMore = false;
            Holder<List<PrivateMessage>> res = messageService.getNewMessagesFromUser(m_userId, m_sinceMsgId);
            if (res.getException() != null) {
                return new Holder<>(res.getException());
            }

            List<PrivateMessage> list = res.getContainedObject();
            if (list != null) {
                overallList.addAll(list);
            }
            if (list != null && list.size() == messageService.BATCH) {
                // Repeat. Last one is newest - set it
                m_sinceMsgId = list.get(messageService.BATCH - 1).getMessageHead().getMessageId();
                loadMore = true;
            }
        }
        return Holder.newInstance(new MessageResult().setUnreadMessages(overallList));
    }

}
