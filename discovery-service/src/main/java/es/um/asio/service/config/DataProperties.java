package es.um.asio.service.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Data Properties configuration properties.
 * @see ElasticSearch
 * @see Redis
 * @see Kafka
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
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
     * @see ElasticSearch
     */
    private ElasticSearch elasticSearch;

    /**
     * Redis Configuration
     */
    private Redis redis;

    /**
     * Kafka Configuration
     */
    private Kafka kafka;

    /**
     * ElasticSearch configuration properties.
     * @author  Daniel Ruiz Santamaría
     * @version 2.0
     * @since   1.0
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ElasticSearch {
        private String host;
        private int port;
        private String password;


    }

    /**
     * Redis configuration properties.
     * @author  Daniel Ruiz Santamaría
     * @version 2.0
     * @since   1.0
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Redis {
        private String host;
        private int port;
        private String password;
    }

    /**
     * Kafka configuration properties.
     * @see TopicEntityChange
     * @see TopicDiscoveryAction
     * @author  Daniel Ruiz Santamaría
     * @version 2.0
     * @since   1.0
     */
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

        /**
         * Topic Entity Change configuration properties.
         * @author  Daniel Ruiz Santamaría
         * @version 2.0
         * @since   1.0
         */
        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        public static class TopicEntityChange {
            private String topic;
            private String groupId;
        }

        /**
         * Topic Discovery Action configuration properties.
         * @author  Daniel Ruiz Santamaría
         * @version 2.0
         * @since   1.0
         */
        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        public static class TopicDiscoveryAction {
            private String topic;
            private String groupId;
        }
    }

}
