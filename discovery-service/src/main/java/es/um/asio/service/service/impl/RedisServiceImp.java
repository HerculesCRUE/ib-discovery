package es.um.asio.service.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.repository.StringRedisRepository;
import es.um.asio.service.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Service
public class RedisServiceImp implements RedisService {

    @Autowired
    StringRedisRepository redisRepository;

    private final String TRIPLES_MAP_KEY = "TRIPLES_MAP";
    private final String FILTERED_KEY = "FILTERED";
    private final String ENTITY_STATS_KEY = "FILTERED";

    @Override
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap() {
        String cachedTriplesMapStr = redisRepository.getBy(TRIPLES_MAP_KEY);
        if (cachedTriplesMapStr!=null) {
            try {
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                Type type = new TypeToken<Map<String, Map<String, Map<String, Map<String, TripleObject>>>>>() {
                }.getType();
                return gson.fromJson(cachedTriplesMapStr, type);
            } catch (Exception e) {
                return new HashMap<>();
            }
        }
        return new HashMap<>();
    }

    @Override
    public void setTriplesMap(Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        JsonObject jTriplesMap = gson.fromJson(gson.toJson(triplesMap),JsonObject.class);
        redisRepository.add(TRIPLES_MAP_KEY,jTriplesMap.toString());
    }

    @Override
    public Map<String, Map<String, Map<String, TripleObject>>> getFilteredTriples() {
        String cachedTriplesMapStr = redisRepository.getBy(FILTERED_KEY);
        if (cachedTriplesMapStr!=null) {
            try {
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                Type type = new TypeToken<Map<String, Map<String, Map<String, TripleObject>>>>() {}.getType();
                return gson.fromJson(cachedTriplesMapStr, type);
            } catch (Exception e) {
                return new HashMap<>();
            }
        }
        return new HashMap<>();
    }

    @Override
    public void setFilteredTriples(Map<String, Map<String, Map<String, TripleObject>>> filteredTriples) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        JsonObject jTriplesMap = gson.fromJson(gson.toJson(filteredTriples),JsonObject.class);
        redisRepository.add(FILTERED_KEY,jTriplesMap.toString());
    }

    @Override
    public EntityStats getEntityStats() {
        String entityStatsStr = redisRepository.getBy(ENTITY_STATS_KEY);
        if (entityStatsStr!=null) {
            try {
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                Type type = new TypeToken<EntityStats>() {}.getType();
                return gson.fromJson(entityStatsStr, type);
            } catch (Exception e) {
                return new EntityStats();
            }
        }
        return new EntityStats();
    }

    @Override
    public void setEntityStats(EntityStats entityStats) {
        Gson gson = new Gson();
        JsonObject jEntityStats = gson.fromJson(gson.toJson(entityStats),JsonObject.class);
        redisRepository.add(ENTITY_STATS_KEY,jEntityStats.toString());
    }
}
