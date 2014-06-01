package ch.defiant.purplesky.loaders.message;

import android.content.Context;
import android.os.Bundle;

import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.util.Holder;

/**
 * Loads older messages from base. Requires {@link ArgumentConstants#ARG_USERID} (String) and a message id (int) provided ({@link ArgumentConstants#ARG_ID}),
 * in the argument bundle.
 * 
 * @author Patrick Bänziger
 * 
 */
public class OlderMessageDBLoader extends AbstractMessageLoader {

    private final long m_upToMessageId;

    public OlderMessageDBLoader(Context c, Bundle args, IPurplemoonAPIAdapter adapter, IMessageService msgService) {
        super(c, R.id.loader_message_moreoldDB, args, adapter, msgService);
        
        m_upToMessageId = args.getLong(ArgumentConstants.ARG_ID, -1L);
        if(m_upToMessageId == -1L){
            throw new IllegalArgumentException("Missing id for bounding to load older messages");
        }
    }

    @Override
    public Holder<MessageResult> loadInBackground() {
        List<PrivateMessage> msgs = messageService.getPreviousCachedMessagesWithUser(m_userId, m_upToMessageId);
        return Holder.newInstance(new MessageResult().setUnreadMessages(msgs));
    }

}
