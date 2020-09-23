package es.um.asio.service.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;

/**
 * Cors related configuration properties.
 */
@Configuration
@ConfigurationProperties("data")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class DataProperties {
    /**
     * Elastic Search Configuration
     */
    private ElasticSearch elasticSearch;

    /**
     * Redis Configuration
     */
    private Redis redis;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ElasticSearch {
        private String host;
        private int port;
        private String password;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Redis {
        private String host;
        private int port;
        private String password;
    }

}
