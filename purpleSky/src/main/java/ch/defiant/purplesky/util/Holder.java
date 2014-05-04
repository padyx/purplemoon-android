package ch.defiant.purplesky.util;

public class Holder<T> {

    private T m_containedObject;
    private Exception m_exception;

    public static <Q> Holder<Q> newInstance(Q value){
        return new Holder<Q>(value);
    }
    
    public Holder() {
    }

    public Holder(T obj) {
        setContainedObject(obj);
    }

    public Holder(Exception e) {
        setException(e);
    }

    public T getContainedObject() {
        return m_containedObject;
    }

    public void setContainedObject(T containedObject) {
        m_containedObject = containedObject;
    }

    public Exception getException() {
        return m_exception;
    }

    public void setException(Exception exception) {
        m_exception = exception;
    }

}
