package ch.defiant.purplesky.beans;

import java.util.Date;

import ch.defiant.purplesky.enums.MessageType;

/**
 * @author Patrick Bänziger
 */
public interface IPrivateMessage {

    String getMessageText();
    long getRecipientId();
    long getSenderId();
    Date getTimeSent();

}
