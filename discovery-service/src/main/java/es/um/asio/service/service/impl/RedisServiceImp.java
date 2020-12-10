package es.um.asio.service.service.impl;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.relational.CacheRegistry;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.model.stats.StatsHandler;
import es.um.asio.service.repository.redis.StringRedisRepository;
import es.um.asio.service.repository.relational.CacheRegistryRepository;
/*import es.um.asio.service.repository.relational.ElasticRegistryRepository;*/
import es.um.asio.service.service.RedisService;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@EnableCaching
public class RedisServiceImp implements RedisService {

    private final Logger logger = LoggerFactory.getLogger(RedisServiceImp.class);

    @Autowired
    StringRedisRepository redisRepository;

    @Autowired
    RedisServiceHelper redisServiceHelper;

    @Autowired
    CacheRegistryRepository cacheRegistryRepository;

    @Autowired
    ApplicationState applicationState;

    @Autowired
    DataSourcesConfiguration dataSourcesConfiguration;

    private final String TRIPLES_MAP_PREFIX = "TRIPLES_MAP";
    private final String TRIPLES_MAP_KEYS = "TRIPLES_MAP_KEYS";
    private final String FILTERED_KEY = "FILTERED";
    private final String ENTITY_STATS_KEY = "ENTITY_STATS_KEY";
    private final String ELASTICSEARCH_KEY = " ELASTICSEARCH_KEY";

    private Gson gson;

    @PostConstruct
    public void init() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    @Override
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap() {
        Map<String, Map<String, Map<String, Map<String, TripleObject>>>> cachedMap = new HashMap<>();
        String cachedKeys = redisRepository.getBy(TRIPLES_MAP_KEYS);
        List<String> keys = gson.fromJson(cachedKeys, List.class);
        List<String> nodesPrefixes = new ArrayList<>();
        // Filtro los keys para los data sources definidos
        for (DataSourcesConfiguration.Node node : dataSourcesConfiguration.getNodes()) {
            for (DataSourcesConfiguration.Node.TripleStore tripleStore : node.getTripleStores()) {
                nodesPrefixes.add(TRIPLES_MAP_PREFIX+":"+node.getNodeName()+"."+tripleStore.getType()+".");
            }
        }

        for (String nodePrefix : nodesPrefixes) {
            keys = keys.stream().filter(k -> k.contains(nodePrefix)).collect(Collectors.toList());
        }

        List<CompletableFuture<Pair<String,Map<String, TripleObject>>>> futures = new ArrayList<>();
        if (keys!=null && !keys.isEmpty()) {
            for (String key : keys) {
                logger.info(String.format("Reading %s from redis cache", key));
                futures.add(redisServiceHelper.getTripleMap(redisRepository,key));
            }

            for (CompletableFuture<Pair<String, Map<String, TripleObject>>> future : futures) {
                Pair<String, Map<String, TripleObject>> pairMap = future.join();
                if (pairMap.getValue1()!=null) {
                    logger.info(String.format("Reading %s from redis cache DONE, founds %d instances", pairMap.getValue0(), pairMap.getValue1().size()));
                    String k = pairMap.getValue0().replaceAll(TRIPLES_MAP_PREFIX + ":", "");
                    Map<String, TripleObject> m = pairMap.getValue1();

                    String[] kSet = k.split("\\.");
                    if (!cachedMap.containsKey(kSet[0])) {
                        cachedMap.put(kSet[0], new HashMap<>());
                    }
                    if (!cachedMap.get(kSet[0]).containsKey(kSet[1])) {
                        cachedMap.get(kSet[0]).put(kSet[1], new HashMap<>());
                    }
                    if (!cachedMap.get(kSet[0]).get(kSet[1]).containsKey(kSet[2])) {
                        cachedMap.get(kSet[0]).get(kSet[1]).put(kSet[2], m);
                    }
                }
            }
        }
        return cachedMap;
    }

    @Override
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMapByNodeAndStorageAndClass(String node, String tripleStore, String className) {
        Map<String, Map<String, Map<String, Map<String, TripleObject>>>> cachedMap = new HashMap<>();
        String cachedKeys = redisRepository.getBy(TRIPLES_MAP_KEYS);
        List<String> keys = gson.fromJson(cachedKeys, List.class);
        List<CompletableFuture<Pair<String,Map<String, TripleObject>>>> futures = new ArrayList<>();
        if (keys!=null && !keys.isEmpty()) {
            for (String key : keys) {
                String[] keyParts = key.replaceAll(TRIPLES_MAP_PREFIX + ":", "").split("\\.");
                if (
                        ( node==null || keyParts[0].trim().toLowerCase().equals(node) ) &&
                        ( tripleStore==null || keyParts[1].trim().toLowerCase().equals(tripleStore) ) &&
                        ( className==null || keyParts[2].trim().toLowerCase().equals(className) )
                ) {
                    logger.info(String.format("Reading %s from redis cache", key));
                    futures.add(redisServiceHelper.getTripleMap(redisRepository,/*TRIPLES_MAP_PREFIX+":"+*/key));
                }
            }

            for (CompletableFuture<Pair<String, Map<String, TripleObject>>> future : futures) {
                Pair<String, Map<String, TripleObject>> pairMap = future.join();
                logger.info(String.format("Reading %s from redis cache DONE", pairMap.getValue0()));
                String k = pairMap.getValue0().replaceAll(TRIPLES_MAP_PREFIX + ":", "");
                Map<String, TripleObject> m = pairMap.getValue1();

                String[] kSet = k.split("\\.");
                if (!cachedMap.containsKey(kSet[0])) {
                    cachedMap.put(kSet[0], new HashMap<>());
                }
                if (!cachedMap.get(kSet[0]).containsKey(kSet[1])) {
                    cachedMap.get(kSet[0]).put(kSet[1], new HashMap<>());
                }
                if (!cachedMap.get(kSet[0]).get(kSet[1]).containsKey(kSet[2])) {
                    cachedMap.get(kSet[0]).get(kSet[1]).put(kSet[2], m);
                }
            }
        }
        return cachedMap;
    }

    @Override
    public void setTriplesMap(Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap, boolean keepKeys, boolean doAsync) {

        // Obtencion del mapa de Keys anterior
        Set<String> keys = new HashSet<>();
        if (keepKeys) {
            Map<String, Map<String, Map<String, Map<String, TripleObject>>>> cachedMap = new HashMap<>();
            String cachedKeys = redisRepository.getBy(TRIPLES_MAP_KEYS);
            if (cachedKeys!=null)
                keys = gson.fromJson(cachedKeys, Set.class);
        }

        List<CompletableFuture<Boolean>> futures = new ArrayList<>();

        for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nodeEntry: triplesMap.entrySet()) { // Node
            for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleEntry: nodeEntry.getValue().entrySet()) { // TripleStore
                for (Map.Entry<String, Map<String, TripleObject>> classEntry: tripleEntry.getValue().entrySet()) { // class
                    Map<String, TripleObject> tMap = gson.fromJson(gson.toJson(classEntry.getValue()), LinkedTreeMap.class);
                    String key = String.format("%s:%s.%s.%s",TRIPLES_MAP_PREFIX,nodeEntry.getKey(),tripleEntry.getKey(),classEntry.getKey());
                    keys.add(key);
                    // DiscoveryApplication discoveryApplication, String node, String tripleStore, String className
                    CacheRegistry cacheRegistry = new CacheRegistry(applicationState.getApplication(),nodeEntry.getKey(),tripleEntry.getKey(),classEntry.getKey());
                    cacheRegistryRepository.save(cacheRegistry);
                    futures.add(redisServiceHelper.setTripleMap(redisRepository,key,tMap));
                }
            }
        }
        if (!doAsync) {
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[futures.size()])
            );
        }
        JsonArray jKeys = gson.fromJson(gson.toJson(keys), JsonArray.class);
        redisRepository.add(TRIPLES_MAP_KEYS,jKeys.toString());
    }

    @Override
    public void setTriplesMapByNodeAndStorageAndClass(String node, String tripleStore, String className, Map<String, TripleObject> triplesMap, boolean doAsync) {
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        String cachedKeys = redisRepository.getBy(TRIPLES_MAP_KEYS);
        List<String> keys = gson.fromJson(cachedKeys, List.class);

        String key = String.format("%s:%s.%s.%s",TRIPLES_MAP_PREFIX,node,triplesMap,className);
        keys.add(key);
        futures.add(redisServiceHelper.setTripleMap(redisRepository,key,triplesMap));

        if (!doAsync) {
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[futures.size()])
            );
        }
        JsonArray jKeys = gson.fromJson(gson.toJson(keys), JsonArray.class);
        redisRepository.add(TRIPLES_MAP_KEYS,jKeys.toString());
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
    public StatsHandler getEntityStats() {
        String entityStatsStr = redisRepository.getBy(ENTITY_STATS_KEY);
        if (entityStatsStr!=null) {
            try {
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                Type type = new TypeToken<Map<String ,Map<String, Map<String, EntityStats>>>>() {}.getType();
                Map<String ,Map<String, Map<String,EntityStats>>> stats = gson.fromJson(entityStatsStr, type);
                StatsHandler statsHandler = new StatsHandler();
                statsHandler.setStats(stats);
                return statsHandler;
            } catch (Exception e) {
                return new StatsHandler();
            }
        }
        return new StatsHandler();
    }

    @Override
    public void setEntityStats(StatsHandler statsHandler) {
        Gson gson = new Gson();
        Map<String, Map<String, Map<String, EntityStats>>> stats = statsHandler.getStats();
        JsonObject jEntityStats = gson.fromJson(gson.toJson(stats),JsonObject.class);
        redisRepository.add(ENTITY_STATS_KEY,jEntityStats.toString());
    }

    @Override
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getElasticSearchTriplesMap() {
        String cachedElasticSearchTriplesMapStr = redisRepository.getBy(ELASTICSEARCH_KEY);
        if (cachedElasticSearchTriplesMapStr!=null) {
            try {
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                Type type = new TypeToken<Map<String, Map<String, Map<String, Map<String, TripleObject>>>>>() {}.getType();
                return gson.fromJson(cachedElasticSearchTriplesMapStr, type);
            } catch (Exception e) {
                return new HashMap<>();
            }
        }
        return new HashMap<>();
    }

    @Override
    public void setElasticSearchTriplesMap(Map<String, Map<String, Map<String, Map<String, TripleObject>>>> elasticSearchTriplesMap) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        JsonObject jElasticSearchTriplesMap = gson.fromJson(gson.toJson(elasticSearchTriplesMap),JsonObject.class);
        redisRepository.add(ELASTICSEARCH_KEY,jElasticSearchTriplesMap.toString());
    }
}


