package ch.defiant.purplesky.loaders.message;

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
 * Loads older messages from base. Requires {@link ArgumentConstants#ARG_USERID} (String) and a message id (int) provided ({@link ArgumentConstants#ARG_ID}),
 * in the argument bundle.
 * 
 * @author Patrick Bänziger
 * 
 */
public class OlderMessageDBLoader extends AbstractMessageLoader {

    private final long m_upToMessageId;

    public OlderMessageDBLoader(Context c, Bundle args) {
        super(c, R.id.loader_message_moreoldDB, args);
        
        m_upToMessageId = args.getLong(ArgumentConstants.ARG_ID, -1L);
        if(m_upToMessageId == -1L){
            throw new IllegalArgumentException("Missing id for bounding to load older messages");
        }
    }

    @Override
    public Holder<MessageResult> loadInBackground() {
        List<PrivateMessage> msgs = MessageService.getPreviousCachedMessagesWithUser(m_userId, m_upToMessageId);
        return Holder.newInstance(new MessageResult().setUnreadMessages(msgs));
    }

}
