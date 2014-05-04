package ch.defiant.purplesky.loaders.message;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.services.MessageService;
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

    public RefreshMessageLoader(Context c, Bundle args) {
        super(c, R.id.loader_message_refresh, args);

        m_sinceMsgId = args.getLong(ArgumentConstants.ARG_ID, -1L);
        if (m_sinceMsgId == -1) {
            throw new IllegalArgumentException("Missing Message Id");
        }
    }

    @Override
    public Holder<MessageResult> loadInBackground() {
        boolean loadMore = true;
        ArrayList<PrivateMessage> overallList = new ArrayList<PrivateMessage>();

        while (loadMore) {
            loadMore = false;
            Holder<List<PrivateMessage>> res = MessageService.getNewMessagesFromUser(m_userId, m_sinceMsgId);
            if (res.getException() != null) {
                return new Holder<MessageResult>(res.getException());
            }

            List<PrivateMessage> list = res.getContainedObject();
            if (list != null) {
                overallList.addAll(list);
            }
            if (list != null && list.size() == MessageService.BATCH) {
                // Repeat. Last one is newest - set it
                m_sinceMsgId = list.get(MessageService.BATCH - 1).getMessageHead().getMessageId();
                loadMore = true;
            }
        }
        return Holder.newInstance(new MessageResult().setUnreadMessages(overallList));
    }

}
