package ch.defiant.purplesky.listeners;

import java.io.Serializable;

public interface IResultDeliveryReceiver<T> extends Serializable{

    void deliverResult(T result);

    /**
     * A call to this method indicates that no result can be provided.
     */
    void noResult();

}
