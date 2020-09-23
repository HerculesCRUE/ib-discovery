package es.um.asio.service.repository;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashSet;
import java.util.Set;

public class StringRedisRepository {

    private final StringRedisTemplate template;

    public StringRedisRepository(StringRedisTemplate template) {
        this.template = template;
    }

    public void add(String key, String value) {
        template.opsForValue().set(key,value);
    }
    
    public String getBy(String key) {
        return template.opsForValue().get(key);
    }

    public Set<String> getKeys(String patternKey) {
        return template.keys(patternKey);
    }

    public Set<String> getAllValuesBy(String patterKey) {
        final Set<String> keys = getKeys(patterKey);
        final Set<String> values = new HashSet<String>(keys.size());

        for (String key : keys) {
            values.add(getBy(key));
        }

        return values;
    }

    public void delete(String key) {
        template.opsForValue().getOperations().delete(key);
    }



}
