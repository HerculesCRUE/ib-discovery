package es.um.asio.service.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * User role definition.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRole {
    /**
     * Table name.
     */
    public static final String TABLE = "APPLICATION_USER_ROLE";

    /**
     * Column name constants.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Columns {

        /**
         * User ID column.
         */
        public static final String USER_ID = "USER_ID";
    }
}
