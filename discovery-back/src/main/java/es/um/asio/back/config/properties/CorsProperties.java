package es.um.asio.back.config.properties;

import java.util.Arrays;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Cors related configuration properties.
 */
@ConfigurationProperties("app.cors")
@Validated
@Getter
@Setter
@ToString
public class CorsProperties {
    /**
     * Enable or disable the CORS filter for this application.
     */
    private boolean enabled;

    /**
     * Allowed origin for CORS.
     */
    private String allowedOrigin;

    /**
     * Allowed methods for CORS.
     */
    private String allowedMethods;

    /**
     * The allowed headers for CORS.
     */
    private String[] allowedHeaders;

    /**
     * CORS preflight max age allowed.
     */
    private String maxAge;

    /**
     * Gets the allowed headers for CORS.
     * 
     * @return allowed headers for CORS.
     */
    public String[] getAllowedHeaders() {
        return Arrays.copyOf(this.allowedHeaders, this.allowedHeaders.length);
    }

    /**
     * Sets the allowed headers for CORS.
     * 
     * @param allowedHeaders
     *            allowed headers for CORS.
     */
    public void setAllowedHeaders(final String[] allowedHeaders) {
        this.allowedHeaders = Arrays.copyOf(allowedHeaders, allowedHeaders.length);
    }
}
