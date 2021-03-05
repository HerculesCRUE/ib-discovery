package es.um.asio.service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Persistence related properties.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@ConfigurationProperties("app.persistence")
@Validated
@Getter
@Setter
public class PersistenceProperties {
    /**
     * Datasource related properties.
     */
    @NotNull
    @NestedConfigurationProperty
    private DatasourceProperties datasource;

    /**
     * JPA related properties.
     */
    @NotNull
    @NestedConfigurationProperty
    private JpaProperties jpa;
}
