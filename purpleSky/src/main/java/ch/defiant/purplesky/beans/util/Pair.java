package ch.defiant.purplesky.beans.util;

import java.io.Serializable;

/**
 * A serializable implementation of a pair - unlike the Android provided version.
 * Of course this requires the elements to be serializable.
 * @param <S>
 * @param <T>
 */
public class Pair<S extends Serializable, T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -3289373175835990567L;


    private S m_first;
    private T m_second;

    public Pair(S first, T second) {
        setFirst(first);
        setSecond(second);
    }

    public void setFirst(S first) {
        m_first = first;
    }

    public S getFirst() {
        return m_first;
    }

    public void setSecond(T second) {
        m_second = second;
    }

    public T getSecond() {
        return m_second;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pair[");
        sb.append(getFirst());
        sb.append(", ");
        sb.append(getSecond());
        sb.append("]");
        return sb.toString();
    }

}
