package es.um.asio.service.config;

import es.um.asio.service.repository.redis.StringRedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis configuration properties.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EntityScan(basePackages = "es.um.asio.service.model.redis")
public class RedisConfig {

    private final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    JedisConnectionFactory connectionFactory(DataProperties dataProperties) {
        logger.info("Starting REDIS with HOST: {}, PORT: {}, PASSWORD: {}",dataProperties.getRedis().getHost(),dataProperties.getRedis().getPort(),dataProperties.getRedis().getPassword());
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(dataProperties.getRedis().getHost());
        redisStandaloneConfiguration.setPort(dataProperties.getRedis().getPort());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(dataProperties.getRedis().getPassword()));
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    StringRedisRepository stringRedisRepository(StringRedisTemplate template) {
        return new StringRedisRepository(template);
    }
}
