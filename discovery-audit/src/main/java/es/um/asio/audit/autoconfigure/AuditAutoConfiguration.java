package es.um.asio.audit.autoconfigure;

import es.um.asio.audit.AuditConfiguration;
import es.um.asio.audit.autoconfigure.properties.AuditProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Infinispan cache autoconfiguration.
 */
@Configuration
@PropertySource("classpath:app-audit.properties")
@ComponentScan(basePackageClasses = { AuditConfiguration.class })
@ConditionalOnProperty(prefix = "app.audit", name = "enabled", havingValue = "false", matchIfMissing = true)
@EnableConfigurationProperties(AuditProperties.class)
@EnableJpaAuditing
public class AuditAutoConfiguration {

}
