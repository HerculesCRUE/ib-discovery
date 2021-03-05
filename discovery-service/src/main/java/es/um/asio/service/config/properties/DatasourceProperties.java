package es.um.asio.service.config.properties;

import es.um.asio.service.config.DataProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.validation.annotation.Validated;


/**
 * Datasource related configuration propreties.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
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
