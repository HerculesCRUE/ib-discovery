package es.um.asio.service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

/**
 * JPA related properties.
 */
@Validated
@Getter
@Setter
public class JpaProperties {
    /**
     * Additional native properties to set on the JPA provider.
     */
    private Map<String, String> properties = new HashMap<>();

    /**
     * Whether to initialize the schema on startup.
     */
    private boolean generateDdl = false;

    /**
     * Whether to enable logging of SQL statements.
     */
    private boolean showSql = false;

    /**
     * Whether to enable logging of SQL statements.
     */
    private boolean formatSql = false;

    /**
     * Whether to enable logging of SQL statements.
     */
    private boolean useSqlComments = false;

    /**
     * JPA dialect for the database.
     */
    private String dialect;
}
