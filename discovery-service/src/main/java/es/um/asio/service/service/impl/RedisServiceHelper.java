package es.um.asio.service.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.repository.redis.StringRedisRepository;
import org.javatuples.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * RedisServiceHelper implementation. Service for wok with REDIS in asynchronous mode
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public class RedisServiceHelper {

    Gson gson;

    /**
     * Init structures
     */
    @PostConstruct
    public void init() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    /**
     * Save in REDIS  in asynchronous mode
     * @see TripleObject
     * @param redisRepository StringRedisRepository. The redis repository
     * @param key String. The key in REDIS
     * @param triplesMap Map<String, TripleObject>. Triple objects. Key is the entity ID and value is the TripleObject
     * @return CompletableFuture<Boolean>
     */
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Boolean> setTripleMap(StringRedisRepository redisRepository, String key, Map<String, TripleObject> triplesMap) {
        JsonObject jTriplesMap = gson.fromJson(gson.toJson(triplesMap),JsonObject.class);
        redisRepository.add(key, jTriplesMap.toString());
        return CompletableFuture.completedFuture(true);
    }

    /**
     * Get from REDIS  in asynchronous mode, by key
     * @param redisRepository StringRedisRepository. The redis repository
     * @param key String. The key in REDIS
     * @return CompletableFuture<Pair<String,Map<String, TripleObject>>>. Pair where first parameter is the class and second is Map<String, TripleObject>. The Key is the entity ID and value is the TripleObject
     */
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Pair<String,Map<String, TripleObject>>> getTripleMap(StringRedisRepository redisRepository, String key) {
        String jMapStr = redisRepository.getBy(key);

        Type type = new TypeToken< Map<String, TripleObject>>() {}.getType();
        Map<String, TripleObject> triplesMap = gson.fromJson(jMapStr, type);

        return CompletableFuture.completedFuture(new Pair<>(key,triplesMap));
    }

}
