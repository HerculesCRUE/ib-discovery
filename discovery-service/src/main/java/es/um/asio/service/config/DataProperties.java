package es.um.asio.service.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

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


    private boolean readCacheFromFirebase;
    /**
     * Elastic Search Configuration
     */
    private ElasticSearch elasticSearch;

    /**
     * Redis Configuration
     */
    private Redis redis;

    /**
     * Redis Configuration
     */
    private Kafka kafka;

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

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Kafka {
        private String host;
        private int port;
        private TopicEntityChange topicEntityChange;
        private TopicDiscoveryAction topicDiscoveryAction;

        public String getBootStrapAddress() {
            return host+":"+port;
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        public static class TopicEntityChange {
            private String topic;
            private String group_id;
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        public static class TopicDiscoveryAction {
            private String topic;
            private String group_id;
        }
    }

}
