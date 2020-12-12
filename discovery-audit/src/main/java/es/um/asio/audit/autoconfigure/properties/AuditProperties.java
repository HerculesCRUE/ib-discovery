package es.um.asio.audit.autoconfigure.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Properties for Audit.
 */
@ConfigurationProperties("app.audit")
@Validated
@Getter
@Setter
@ToString
public class AuditProperties {
    /**
     * Flag to set if audit is enabled.
     */
    // private boolean enabled;
}
