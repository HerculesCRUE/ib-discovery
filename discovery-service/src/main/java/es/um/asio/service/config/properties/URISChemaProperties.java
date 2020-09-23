package es.um.asio.service.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Cors related configuration properties.
 */
@ConfigurationProperties("app.uri")
@Validated
@Getter
@Setter
@ToString
public class URISChemaProperties {
    /**
     * canonical URI Schema.
     */
    private String canonicalURISchema;

    /**
     * canonical URI Language Schema.
     */
    private String canonicalURILanguageSchema;

}
