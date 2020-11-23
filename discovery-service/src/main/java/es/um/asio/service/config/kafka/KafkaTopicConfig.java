package es.um.asio.service.config.kafka;

import es.um.asio.service.config.DataProperties;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Autowired
    DataProperties dataProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String,Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, dataProperties.getKafka().getBootStrapAddress());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic cacheUpdateTopic() {
        return new NewTopic(dataProperties.getKafka().getTopicEntityChange().getTopic(),1,(short) 1);
    }

    @Bean
    public NewTopic cacheDiscoveryActionTopic() {
        return new NewTopic(dataProperties.getKafka().getTopicDiscoveryAction().getTopic(),1,(short) 1);
    }
}
