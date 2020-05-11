package es.um.asio.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * JPA constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JpaConstants {
    /**
     * Hibernate UUID generator name.
     */
    public static final String HIBERNATE_UUID_GENERATOR_NAME = "uuid";

    /**
     * Hibernate UUID generator strategy.
     */
    public static final String HIBERNATE_UUID_GENERATOR_STRATEGY = "uuid2";
}
