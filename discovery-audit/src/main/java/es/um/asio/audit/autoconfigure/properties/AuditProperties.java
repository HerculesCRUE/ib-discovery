package es.um.asio.audit.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    private boolean enabled;
}
