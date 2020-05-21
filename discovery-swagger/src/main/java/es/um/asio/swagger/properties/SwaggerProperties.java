package es.um.asio.swagger.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

/**
 * Properties for Swagger.
 */
@ConfigurationProperties("app.swagger")
@Validated
@Getter
@Setter
public class SwaggerProperties {
    /**
     * Indicates if swagger must be enabled.
     */
    private boolean enabled = true;

    /**
     * Indicates if REST API is secured with OAuth.
     */
    private boolean secured = false;

    /**
     * Authorization server URL.
     */
    private String authServerUrl;

    /**
     * Authorization server Client ID.
     */
    private String authServerClientId;

    /**
     * Authorization server Client Secret.
     */
    private String authServerClientSecret;
}
