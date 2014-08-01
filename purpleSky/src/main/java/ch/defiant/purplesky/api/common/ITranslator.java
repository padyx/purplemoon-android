package ch.defiant.purplesky.api.common;

/**
 * Generic interface for a translator.
 * @param <S> Source type
 * @param <T> target type
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
public interface ITranslator<S,T> {

    T translate(S source);

}
