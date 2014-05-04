package ch.defiant.purplesky.adapters;

public class SpinnerStateElement<T> {

    private final String m_text;
    private final T m_value;

    public SpinnerStateElement(T value, String text) {
        m_value = value;
        m_text = text;
    }

    @Override
    public String toString() {
        return m_text;
    }

    public T getValue() {
        return m_value;
    }

}
