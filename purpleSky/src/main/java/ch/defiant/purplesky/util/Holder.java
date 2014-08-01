package ch.defiant.purplesky.util;

/**
 * Generic holder class that holds either an object or an exception.
 * @param <T> Type of the normally contained object.
 */
public class Holder<T> {

    private final T m_containedObject;
    private final Exception m_exception;

    public static <Q> Holder<Q> newInstance(Q value){
        return new Holder<Q>(value);
    }
    public static <Q> Holder<Q> of(Q value){
        return new Holder<Q>(value);
    }
    
    public Holder(T obj) {
        m_containedObject = obj;
        m_exception = null;
    }

    public Holder(Exception e) {
        m_exception = e;
        m_containedObject = null;
    }

    public T getContainedObject() {
        return m_containedObject;
    }

    public Exception getException() {
        return m_exception;
    }

    public boolean isException(){
        return m_exception != null;
    }

    public boolean isObject(){
        return m_containedObject != null;
    }
}
