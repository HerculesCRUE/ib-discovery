package es.um.asio.service.config.kafka;

import es.um.asio.service.config.DataProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The Kafka Producer config
 * @see DataProperties
 * @see ConsumerConfig
 * @see DefaultKafkaProducerFactory
 * @see KafkaListenerContainerFactory
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Configuration
public class KafkaProducerConfig {

    @Bean
    public Map<String, Object> producerConfigs(DataProperties dataProperties) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                dataProperties.getKafka().getBootStrapAddress());
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );
        return configProps;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory(DataProperties dataProperties) {
        return new DefaultKafkaProducerFactory<>(producerConfigs(dataProperties));
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(DataProperties dataProperties) {
        try {
            return new KafkaTemplate<>(producerFactory(dataProperties));
        } catch (Exception e) {
            return null;
        }
    }

}
