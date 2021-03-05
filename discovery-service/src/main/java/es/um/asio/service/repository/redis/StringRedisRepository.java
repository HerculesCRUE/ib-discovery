package es.um.asio.service.repository.redis;

import es.um.asio.service.service.impl.TextHandlerServiceImp;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashSet;
import java.util.Set;

/**
 * StringRedisRepository. Handle operation in Redis
 * @see StringRedisTemplate
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public class StringRedisRepository {

    private final StringRedisTemplate template;

    /**
     * Constructor
     * @see StringRedisTemplate
     * @param template StringRedisTemplate. The template of Redis
     */
    public StringRedisRepository(StringRedisTemplate template) {
        this.template = template;
    }

    /**
     * Add in Redis by key value
     * @param key String. The key in Redis
     * @param value String. The value in Redis
     */
    public void add(String key, String value) {
        template.opsForValue().set(key,value);
    }

    /**
     * Get value in Redis by key
     * @param key String. The key in Redis
     * @return String. The response in Redis
     */
    public String getBy(String key) {
        return template.opsForValue().get(key);
    }

    /**
     * Get all values in Redis by pattern key
     * @param patternKey String. The pattern Key in Redis
     * @return Set<String>. The responses in Redis
     */
    public Set<String> getKeys(String patternKey) {
        return template.keys(patternKey);
    }

    /**
     * Get all values in Redis by pattern key
     * @param patterKey String. The pattern Key in Redis
     * @return Set<String>. The responses in Redis
     */
    public Set<String> getAllValuesBy(String patterKey) {
        final Set<String> keys = getKeys(patterKey);
        final Set<String> values = new HashSet<>(keys.size());

        for (String key : keys) {
            values.add(getBy(key));
        }

        return values;
    }

    /**
     * Delete in Redis by Key
     * @param key String. The key in Redis
     */
    public void delete(String key) {
        template.opsForValue().getOperations().delete(key);
    }



}
