package ch.defiant.purplesky.dao;

import android.support.annotation.NonNull;

import java.util.Collection;

import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessage;

/**
 * @author Patrick Bänziger
 * @since 1.4
 */
public interface IMessageDao {

    /**
     * Insert a single message into the database.
     * @param message Message to create
     */
    void create(@NonNull PrivateMessage message);

    /**
     * Insert multiple single message into the database.
     * @param messages Messages to create
     */
    void create(@NonNull Collection<PrivateMessage> messages);

    /**
     * Update the status after a send attempt
     * @param message Message to alter
     */
    void updateStatus(@NonNull IPrivateMessage message);


}
