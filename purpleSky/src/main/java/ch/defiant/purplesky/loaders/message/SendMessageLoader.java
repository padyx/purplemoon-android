package ch.defiant.purplesky.loaders.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.core.PurplemoonAPIAdapter;
import ch.defiant.purplesky.core.SendOptions;
import ch.defiant.purplesky.core.SendOptions.UnreadHandling;
import ch.defiant.purplesky.services.MessageService;
import ch.defiant.purplesky.util.Holder;

/**
 * Message loader that sends a message and returns new ones. Requires a profile id for the recipient (String, {@link ArgumentConstants#ARG_USERID} and
 * a {@link PrivateMessage} provided with {@link ArgumentConstants#ARG_MESSAGE}.
 * 
 * @author Patrick Bänziger
 */
public class SendMessageLoader extends AbstractMessageLoader {

    private static final String TAG = SendMessageLoader.class.getSimpleName();
    private PrivateMessage m_message;
    
    public SendMessageLoader(Context c, Bundle args) {
        super(c, R.id.loader_message_send, args);
        
        m_message = (PrivateMessage) args.getSerializable(ArgumentConstants.ARG_MESSAGE);
        if (m_message == null) {
            throw new IllegalArgumentException("No message to send!");
        }
    }

    @Override
    public Holder<MessageResult> loadInBackground() {
        boolean finished = false;
        List<PrivateMessage> unreadMsgs = new ArrayList<PrivateMessage>();
        MessageResult sent = null;
        int maxsending = 5;
        while (!finished) {
            if (maxsending == 0) {
                Log.w(TAG, "Emergency abort. Retried sending without exception too many times");
                return new Holder<MessageResult>(new IllegalStateException("Emergency abort. Retried sending without exception too many times"));
            }
            Long lastReceivedTimestamp = MessageService.getLatestReceivedMessageTimestamp(m_userId);

            // If we had a message before, send it only if there is nothing in between
            // Otherwise, just send anyway
            SendOptions opts = new SendOptions();
            if (lastReceivedTimestamp != null) {
                opts.setUnreadHandling(UnreadHandling.ABORT);
                opts.setLatestRead(new Date(lastReceivedTimestamp));
            } else {
                opts.setUnreadHandling(UnreadHandling.SEND);
            }

            try {
                sent = PurplemoonAPIAdapter.getInstance().sendMessage(m_message, opts);
                maxsending--;
                // Add to database
                if (sent != null) {

                    if (sent.getUnreadMessages() != null) {
                        unreadMsgs.addAll(sent.getUnreadMessages());
                        MessageService.insertMessages(unreadMsgs);
                    }
                    if (sent.getSentMessage() != null) {
                        finished = true;
                        MessageService.insertMessage(sent.getSentMessage());
                        sent.setUnreadMessages(unreadMsgs);
                    }
                }
            } catch (Exception e) {
                return new Holder<MessageResult>(e);
            }
        }
        return new Holder<MessageResult>(sent);
    }

}
