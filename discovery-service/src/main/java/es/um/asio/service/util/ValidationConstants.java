package es.um.asio.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Validation constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationConstants {
    /**
     * Maximum size for strings by default.
     */
    public static final int MAX_LENGTH_DEFAULT = 255;

    /**
     * Maximum size for UUIDs.
     */
    public static final int MAX_LENGTH_UUID = 36;

    /**
     * Minimum password size.
     */
    public static final int MIN_PASSWORD_LENGTH = 6;
}
