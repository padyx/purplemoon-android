package ch.defiant.purplesky.loaders.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.dao.IMessageDao;
import ch.defiant.purplesky.enums.MessageStatus;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.services.MessageSendingService;
import ch.defiant.purplesky.util.Holder;

/**
 * Message loader that sends a message and returns new ones. Requires a profile id for the recipient (String, {@link ArgumentConstants#ARG_USERID} and
 * a {@link PrivateMessage} provided with {@link ArgumentConstants#ARG_MESSAGE}.
 * 
 * @author Patrick Bänziger
 */
public class SendMessageLoader extends SimpleAsyncLoader<Holder<MessageResult>>{

    @NonNull
    private PrivateMessage m_message;
    @NonNull
    private IMessageDao m_messageDao;

    public SendMessageLoader(Context c, Bundle args, @NonNull IMessageDao messageDao) {
        super(c, R.id.loader_message_send);

        m_messageDao = messageDao;

        Object message0 = args.getSerializable(ArgumentConstants.ARG_MESSAGE);
        if (message0 == null) {
            throw new IllegalArgumentException("No message to send!");
        } else {
            m_message = (PrivateMessage) message0;
            m_message.setStatus(MessageStatus.NEW);
            m_message.setSenderId(PersistantModel.getInstance().getUserProfileId());
        }
    }

    @Override
    public Holder<MessageResult> loadInBackground() {
        // FIXME pbn Must
        m_messageDao.create(m_message);

        Intent intent = new Intent(PurpleSkyApplication.get(), MessageSendingService.class);

        PurpleSkyApplication.get().startService(intent);

        MessageResult result = new MessageResult();
        result.setSentMessage(m_message);
        return new Holder<>(result);
    }

}
