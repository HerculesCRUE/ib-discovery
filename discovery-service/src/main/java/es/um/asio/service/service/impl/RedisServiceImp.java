package es.um.asio.service.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import es.um.asio.service.config.Datasources;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.relational.CacheRegistry;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.model.stats.StatsHandler;
import es.um.asio.service.repository.redis.StringRedisRepository;
import es.um.asio.service.repository.relational.CacheRegistryRepository;
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

/**
 * RedisService implementation. For handle operations with REDIS
 * @see TripleObject
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
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
    Datasources dataSources;

    private static final String TRIPLES_MAP_PREFIX = "TRIPLES_MAP";
    private static final String TRIPLES_MAP_KEYS = "TRIPLES_MAP_KEYS";
    private static final String FILTERED_KEY = "FILTERED";
    private static final String ENTITY_STATS_KEY = "ENTITY_STATS_KEY";
    private static final String ELASTICSEARCH_KEY = " ELASTICSEARCH_KEY";

    private Gson gson;

    @PostConstruct
    public void init() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    /**
     * Get all Triple Object With recursive Map structure stored by Node -> Triple Store -> Class Name -> Id, TripleObject
     * @see TripleObject
     * @return Map<String, Map<String, Map<String, Map<String, TripleObject>>>>
     */
    @Override
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap() {
        Map<String, Map<String, Map<String, Map<String, TripleObject>>>> cachedMap = new HashMap<>();
        String cachedKeys = redisRepository.getBy(TRIPLES_MAP_KEYS);
        List<String> keys = gson.fromJson(cachedKeys, List.class);
        if (keys == null)
            keys = new ArrayList<>();
        List<String> nodesPrefixes = new ArrayList<>();
        // Filtro los keys para los data sources definidos
        for (Datasources.Node node : dataSources.getNodes()) {
            for (Datasources.Node.TripleStore tripleStore : node.getTripleStores()) {
                nodesPrefixes.add(TRIPLES_MAP_PREFIX+":"+node.getNodeName()+"."+tripleStore.getType()+".");
            }
        }

/*        for (String nodePrefix : nodesPrefixes) {
            keys = keys.stream().filter(k -> (!(k.contains(nodePrefix)))).collect(Collectors.toList());
        }*/

        List<CompletableFuture<Pair<String,Map<String, TripleObject>>>> futures = new ArrayList<>();
        if (keys!=null && !keys.isEmpty()) {
            for (String key : keys) {
                logger.info("Reading {} from redis cache", key);
                futures.add(redisServiceHelper.getTripleMap(redisRepository,key));
            }

            for (CompletableFuture<Pair<String, Map<String, TripleObject>>> future : futures) {
                Pair<String, Map<String, TripleObject>> pairMap = future.join();
                if (pairMap.getValue1()!=null) {
                    logger.info("Reading {} from redis cache DONE, founds {} instances", pairMap.getValue0(), pairMap.getValue1().size());
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

    /**
     * Get all Triple Object With recursive Map structure stored by Node -> Triple Store -> Class Name -> Id, TripleObject
     * @see TripleObject
     * @return Map<String, Map<String, Map<String, Map<String, TripleObject>>>>
     */
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
                        ( node==null || keyParts[0].trim().equalsIgnoreCase(node) ) &&
                        ( tripleStore==null || keyParts[1].trim().equalsIgnoreCase(tripleStore) ) &&
                        ( className==null || keyParts[2].trim().equalsIgnoreCase(className) )
                ) {
                    logger.info("Reading {} from redis cache", key);
                    futures.add(redisServiceHelper.getTripleMap(redisRepository,/*TRIPLES_MAP_PREFIX+":"+*/key));
                }
            }

            for (CompletableFuture<Pair<String, Map<String, TripleObject>>> future : futures) {
                Pair<String, Map<String, TripleObject>> pairMap = future.join();
                logger.info("Reading {} from redis cache DONE", pairMap.getValue0());
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

    /**
     * Store in REDIS, the structure Map<String, Map<String, Map<String, Map<String, TripleObject>>>> which is a recursive Map structure stored by Node -> Triple Store -> Class Name -> Id, TripleObject
     * @see TripleObject
     * @param triplesMap Map<String, Map<String, Map<String, Map<String, TripleObject>>>>. The structure to save
     * @param keepKeys boolean. If false old keys will be deleted, else the keys will be preserved
     * @param doAsync boolean. If true the operation will be done in asynchronous mode, else the operation will be in synchronous mode
     */
    @Override
    public void setTriplesMap(Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap, boolean keepKeys, boolean doAsync) {

        // Obtencion del mapa de Keys anterior
        Set<String> keys = new HashSet<>();
        if (keepKeys) {
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
            CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[futures.size()])
            );
        }
        JsonArray jKeys = gson.fromJson(gson.toJson(keys), JsonArray.class);
        redisRepository.add(TRIPLES_MAP_KEYS,jKeys.toString());
    }

    /**
     * Store in REDIS, the structure Map<String, Map<String, Map<String, Map<String, TripleObject>>>> which is a recursive Map structure stored by Node -> Triple Store -> Class Name -> Id, TripleObject
     * The operation will be filtered by Node, Triple Store and Class Name
     * @see TripleObject
     * @param node String. The node name
     * @param tripleStore String. The triple store name
     * @param className String. The class name
     * @param triplesMap Map<String, TripleObject>. The triple Objects to Store in REDIS
     * @param doAsync boolean. If true the operation will be done in asynchronous mode, else the operation will be in synchronous mode
     */
    @Override
    public void setTriplesMapByNodeAndStorageAndClass(String node, String tripleStore, String className, Map<String, TripleObject> triplesMap, boolean doAsync) {
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        String cachedKeys = redisRepository.getBy(TRIPLES_MAP_KEYS);
        List<String> keys = gson.fromJson(cachedKeys, List.class);

        String key = String.format("%s:%s.%s.%s",TRIPLES_MAP_PREFIX,node,triplesMap,className);
        keys.add(key);
        futures.add(redisServiceHelper.setTripleMap(redisRepository,key,triplesMap));

        if (!doAsync) {
            CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[futures.size()])
            );
        }
        JsonArray jKeys = gson.fromJson(gson.toJson(keys), JsonArray.class);
        redisRepository.add(TRIPLES_MAP_KEYS,jKeys.toString());
    }

    /**
     * Get Triple Objects filtered by date of last modification
     * @see TripleObject
     * @return Map<String, Map<String, Map<String,TripleObject>>>
     */
    @Override
    public Map<String, Map<String, Map<String, TripleObject>>> getFilteredTriples() {
        String cachedTriplesMapStr = redisRepository.getBy(FILTERED_KEY);
        if (cachedTriplesMapStr!=null) {
            try {
                Gson gsonInner = new GsonBuilder()
                        .setPrettyPrinting()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                Type type = new TypeToken<Map<String, Map<String, Map<String, TripleObject>>>>() {}.getType();
                return gsonInner.fromJson(cachedTriplesMapStr, type);
            } catch (Exception e) {
                return new HashMap<>();
            }
        }
        return new HashMap<>();
    }

    /**
     * UPDATE Triple Objects filtered by date of last modification
     * @see TripleObject
     * @param filteredTriples Map<String, Map<String, Map<String,TripleObject>>>
     */
    @Override
    public void setFilteredTriples(Map<String, Map<String, Map<String, TripleObject>>> filteredTriples) {
        Gson gsonInner = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        JsonObject jTriplesMap = gsonInner.fromJson(gsonInner.toJson(filteredTriples),JsonObject.class);
        redisRepository.add(FILTERED_KEY,jTriplesMap.toString());
    }

    /**
     * Get the entities Stats
     * @see StatsHandler
     * @return StatsHandler
     */
    @Override
    public StatsHandler getEntityStats() {
        String entityStatsStr = redisRepository.getBy(ENTITY_STATS_KEY);
        if (entityStatsStr!=null) {
            try {
                Gson gsonInner = new GsonBuilder()
                        .setPrettyPrinting()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                Type type = new TypeToken<Map<String ,Map<String, Map<String, EntityStats>>>>() {}.getType();
                Map<String ,Map<String, Map<String,EntityStats>>> stats = gsonInner.fromJson(entityStatsStr, type);
                StatsHandler statsHandler = new StatsHandler();
                statsHandler.setStats(stats);
                return statsHandler;
            } catch (Exception e) {
                return new StatsHandler();
            }
        }
        return new StatsHandler();
    }

    /**
     * UPDATE the entities Stats
     * @see StatsHandler
     * @param statsHandler StatsHandler. The stats of the entities
     */
    @Override
    public void setEntityStats(StatsHandler statsHandler) {
        Gson gsonInner = new Gson();
        Map<String, Map<String, Map<String, EntityStats>>> stats = statsHandler.getStats();
        JsonObject jEntityStats = gsonInner.fromJson(gsonInner.toJson(stats),JsonObject.class);
        redisRepository.add(ENTITY_STATS_KEY,jEntityStats.toString());
    }

    /**
     * Get the Entities inserted in Elasticsearch
     * @return Map<String, Map<String, Map<String, Map<String,TripleObject>>>>
     */
    @Override
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getElasticSearchTriplesMap() {
        String cachedElasticSearchTriplesMapStr = redisRepository.getBy(ELASTICSEARCH_KEY);
        if (cachedElasticSearchTriplesMapStr!=null) {
            try {
                Gson gsonInner = new GsonBuilder()
                        .setPrettyPrinting()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                Type type = new TypeToken<Map<String, Map<String, Map<String, Map<String, TripleObject>>>>>() {}.getType();
                return gsonInner.fromJson(cachedElasticSearchTriplesMapStr, type);
            } catch (Exception e) {
                return new HashMap<>();
            }
        }
        return new HashMap<>();
    }

    /**
     * UPDATE the Entities inserted in Elasticsearch
     * @return Map<String, Map<String, Map<String, Map<String,TripleObject>>>>
     */
    @Override
    public void setElasticSearchTriplesMap(Map<String, Map<String, Map<String, Map<String, TripleObject>>>> elasticSearchTriplesMap) {
        Gson gsonInner = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        JsonObject jElasticSearchTriplesMap = gsonInner.fromJson(gsonInner.toJson(elasticSearchTriplesMap),JsonObject.class);
        redisRepository.add(ELASTICSEARCH_KEY,jElasticSearchTriplesMap.toString());
    }
}


