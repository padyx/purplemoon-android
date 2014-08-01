package ch.defiant.purplesky.api.report;

/**
 *
 * @author Chakotay
 * @since 1.1.0
 */
public enum ReportResponse {
    /**
     * Indicates that the report was successful
     */
    OK,
    /**
     * Indicates a (unspecific) business error
     */
    ERROR,
    /**
     * The text that the user entered was too long
     */
    TEXT_TOO_LONG,
    /**
     * The user was already deleted.
     */
    INVALID_USER,
    /**
     * Too many reports were made within a timeframe
     */
    TOO_MANY
}
