package es.um.asio.service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

/**
 * Datasource related configuration propreties.
 */
@Getter
@Setter
@Validated
public class DatasourceProperties {
    /**
     * JDBC driver class name.
     */
    private String driverClassName;
    /**
     * Username for the database.
     */
    private String username;
    /**
     * Password for the database.
     */
    private String password;

    /**
     * URL of the database
     */
    private String url;

    /**
     * JNDI name of the datasource.
     */
    private String jndiName;
}
