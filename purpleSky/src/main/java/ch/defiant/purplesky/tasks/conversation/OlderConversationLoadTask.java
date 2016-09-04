package ch.defiant.purplesky.tasks.conversation;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

/**
 * Task that loads the next N older conversations
 *
 * @author Patrick Bänziger
 */
// TODO pbn Remove
public class OlderConversationLoadTask extends AsyncTask<String, Void, List<IPrivateMessage>> {

    @NonNull
    private final IConversationAdapter m_conversationAdapter;

    public OlderConversationLoadTask(@NonNull IConversationAdapter conversationAdapter){
        m_conversationAdapter = conversationAdapter;
    }


    @Override
    protected List<IPrivateMessage> doInBackground(String... params) {
        if(params == null){
            return Collections.emptyList();
        }

        for (String profileId : params) {
            // Basically - its only one at a time...

            // Get oldest cached messageId;
            // FIXME pbn Implement
            Long messageId = null;

            List<IPrivateMessage> list;
            if(messageId == null){
                AdapterOptions opts = new AdapterOptions();
                opts.setNumber(20);
                opts.setOrder(PurplemoonAPIConstantsV1.MESSAGE_CHATSHOW_ORDER_NEWESTFIRST);
                opts.setUptoId(messageId);

                try {
                    list = m_conversationAdapter.getRecentMessagesByUser(profileId, opts);
                } catch (IOException e) {
                    e.printStackTrace();
                    return Collections.emptyList();
                } catch (PurpleSkyException e) {
                    e.printStackTrace();
                    return Collections.emptyList();
                }
            } else {
                // Load older ones
                // FIXME implement
                list = Collections.emptyList();
            }

            return list;
        }

        return Collections.emptyList();
    }

}
