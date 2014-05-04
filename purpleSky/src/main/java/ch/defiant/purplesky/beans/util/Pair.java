package ch.defiant.purplesky.beans.util;

import java.io.Serializable;

public class Pair<S extends Serializable, T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -3289373175835990567L;
    private TOSTRING_TYPE m_configToString;

    public enum TOSTRING_TYPE {
        DEFAULT,
        FIRST,
        SECOND
    }

    private S m_first;
    private T m_second;

    public Pair(S first, T second) {
        setFirst(first);
        setSecond(second);
    }

    public Pair(S first, T second, TOSTRING_TYPE toStringType) {
        setFirst(first);
        setSecond(second);
        setConfigToString(toStringType);
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
        TOSTRING_TYPE toStringOption = getConfigToString();
        StringBuilder sb = new StringBuilder();
        switch (toStringOption) {
            case DEFAULT:
                sb.append("Pair[");
                sb.append(getFirst());
                sb.append(", ");
                sb.append(getSecond());
                sb.append("]");
            case FIRST:
                sb.append(getFirst());
                break;
            case SECOND:
                sb.append(getSecond());
                break;
        }
        return sb.toString();
    }

    public TOSTRING_TYPE getConfigToString() {
        return m_configToString;
    }

    public void setConfigToString(TOSTRING_TYPE configToString) {
        if (configToString == null) {
            configToString = TOSTRING_TYPE.DEFAULT;
        }
        m_configToString = configToString;
    }

}
