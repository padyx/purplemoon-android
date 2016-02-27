package ch.defiant.purplesky.loaders.message;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.PendingMessage;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.SendOptions;
import ch.defiant.purplesky.core.SendOptions.UnreadHandling;
import ch.defiant.purplesky.dao.IPendingMessageDao;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.services.BinderServiceWrapper;
import ch.defiant.purplesky.services.MessageSendingService;
import ch.defiant.purplesky.services.UploadService;
import ch.defiant.purplesky.util.Holder;

/**
 * Message loader that sends a message and returns new ones. Requires a profile id for the recipient (String, {@link ArgumentConstants#ARG_USERID} and
 * a {@link PrivateMessage} provided with {@link ArgumentConstants#ARG_MESSAGE}.
 * 
 * @author Patrick Bänziger
 */
public class SendMessageLoader extends SimpleAsyncLoader<Holder<MessageResult>>{

    @NonNull
    private PendingMessage m_message;
    @NonNull
    private IPendingMessageDao m_messageDao;

    public SendMessageLoader(Context c, Bundle args, @NonNull IPendingMessageDao messageDao) {
        super(c, R.id.loader_message_send);

        m_messageDao = messageDao;

        Object message0 = args.getSerializable(ArgumentConstants.ARG_MESSAGE);
        if (message0 == null) {
            throw new IllegalArgumentException("No message to send!");
        } else {
            m_message = (PendingMessage) message0;
        }
    }

    @Override
    public Holder<MessageResult> loadInBackground() {
        m_message = m_messageDao.create(m_message);

        Intent intent = new Intent(PurpleSkyApplication.get(), MessageSendingService.class);
        PurpleSkyApplication.get().startService(intent);

        MessageResult result = new MessageResult();
        result.setSentMessage(m_message);
        return new Holder<>(result);
    }

}
