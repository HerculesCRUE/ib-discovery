package es.um.asio.service.config;

import es.um.asio.service.config.DataProperties;
import es.um.asio.service.repository.StringRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class RedisConfig {

    @Autowired
    DataProperties dataProperties;

    @Bean
    JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory jedisConFactory
            = new JedisConnectionFactory();
        jedisConFactory.setHostName(dataProperties.getRedis().getHost());
        jedisConFactory.setPort(dataProperties.getRedis().getPort());
        jedisConFactory.setPassword(dataProperties.getRedis().getPassword());
        return jedisConFactory;
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
