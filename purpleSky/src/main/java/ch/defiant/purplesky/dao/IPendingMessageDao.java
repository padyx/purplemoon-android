package ch.defiant.purplesky.dao;

import android.support.annotation.NonNull;

import ch.defiant.purplesky.beans.PendingMessage;

/**
 * @author Patrick Bänziger
 */
public interface IPendingMessageDao {

    @NonNull PendingMessage create(@NonNull PendingMessage message);


}
