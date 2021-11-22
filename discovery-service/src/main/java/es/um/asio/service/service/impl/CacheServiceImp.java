package es.um.asio.service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import es.um.asio.service.model.URIComponent;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.stats.StatsHandler;
import es.um.asio.service.repository.elasticsearch.TripleObjectESCustomRepository;
import es.um.asio.service.service.CacheService;
import es.um.asio.service.util.Utils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * CacheServiceImpl class. For handle cached data. Implement CacheService
 * @see CacheService
 * @see TripleObject
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public class CacheServiceImp implements CacheService {

    private final Logger logger = LoggerFactory.getLogger(CacheServiceImp.class);

    private Map<String, Map<String, Map<String, Map<String,TripleObject>>>> triplesMap; // Class --> Instances
    private Map<String, Map<String, Map<String, Map<Integer,Map<Integer,Map<String,TripleObject>>>>>> triplesMapByDate; // Class --> Instances
    private Map<String, Map<String, Map<String,TripleObject>>> filtered;
    private Map<String, Map<String, Map<String, Map<String,TripleObject>>>> esTriplesMap; // Class --> Instances
    private StatsHandler statsHandler;
    DateFormat dateFormat;
    private Map<String, Map<String, Map<String, Map<String,Map<String, Pair<String,TripleObject>>>>>> inversePointersMap;

    @Autowired
    RedisServiceImp redisServiceImp;

    @Autowired
    ElasticsearchServiceImp elasticsearchServiceImp;

    @Autowired
    SchemaServiceImp serviceImp;

    @Value("${app.domain}")
    String domain;

    @PostConstruct
    public void initialize() {
        triplesMap = new HashMap<>();
        triplesMapByDate = new HashMap<>();
        filtered = new HashMap<>();
        esTriplesMap = new HashMap<>();
        statsHandler = new StatsHandler();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        inversePointersMap = new HashMap<>();
    }

    public ElasticsearchServiceImp getElasticsearchServiceImp() {
        return elasticsearchServiceImp;
    }

    /**
     * Add a new TripleObject in memory.
     * @see TripleObject
     * @param node String. The node name.
     * @param triple String. The triple store name.
     * @param to TripleObject. The triple object instance to add.
     */
    @Override
    public void addTripleObject(String node, String triple, TripleObject to) {
        if (to!=null) {
            if (!triplesMap.containsKey(node) || triplesMap.get(node) == null) {
                triplesMap.put(node, new HashMap<>());
            }

            if (!triplesMapByDate.containsKey(node) || triplesMapByDate.get(node) == null)
                triplesMapByDate.put(node, new HashMap<>());

            if (!triplesMap.get(node).containsKey(triple)  || triplesMap.get(node).get(triple) == null) {
                triplesMap.get(node).put(triple, new HashMap<>());
            }

            if (!triplesMapByDate.get(node).containsKey(triple) || triplesMapByDate.get(node).get(triple) == null) {
                triplesMapByDate.get(node).put(triple, new HashMap<>());
            }

            if (!triplesMap.get(node).get(triple).containsKey(to.getClassName()) || triplesMap.get(node).get(triple).get(to.getClassName()) == null) {
                triplesMap.get(node).get(triple).put(to.getClassName(), new HashMap<>());
            }

            if (!triplesMapByDate.get(node).get(triple).containsKey(to.getClassName()) || triplesMapByDate.get(node).get(triple).get(to.getClassName()) == null) {
                triplesMapByDate.get(node).get(triple).put(to.getClassName(), new HashMap<>());
            }

            if (!triplesMapByDate.get(node).get(triple).get(to.getClassName()).containsKey(to.getYear()) || triplesMapByDate.get(node).get(triple).get(to.getClassName()).get(to.getYear()) == null) {
                triplesMapByDate.get(node).get(triple).get(to.getClassName()).put(to.getYear(), new HashMap<>());
            }

            if (!triplesMapByDate.get(node).get(triple).get(to.getClassName()).get(to.getYear()).containsKey(to.getMonth()) || triplesMapByDate.get(node).get(triple).get(to.getClassName()).get(to.getYear()).get(to.getMonth()) == null) {
                triplesMapByDate.get(node).get(triple).get(to.getClassName()).get(to.getYear()).put(to.getMonth(), new HashMap<>());
            }

            triplesMap.get(node).get(triple).get(to.getClassName()).put(to.getId(), to);
            triplesMapByDate.get(node).get(triple).get(to.getClassName()).get(to.getYear()).get(to.getMonth()).put(to.getId(), to);
        }
    }

    /**
     * Add a new TripleObject in memory from TripleObjectES.
     * @see TripleObjectES
     * @param node String. The node name.
     * @param triple String. The triple store name.
     * @param toES TripleObjectES. The triple object ES instance to add.
     */
    @Override
    public void addTripleObjectES(String node, String triple, TripleObjectES toES) {
        TripleObject to = new TripleObject(toES);
        addTripleObject(node,triple,to);
    }

    /**
     * remove TripleObject from memory.
     * @see TripleObject
     * @param node String. The node name.
     * @param triple String. The triple store name.
     * @param to TripleObject. The triple object instance to add.
     */
    @Override
    public void removeTripleObject(String node, String triple, TripleObject to) {
        if (    triplesMap.containsKey(node) &&
                triplesMap.get(node).containsKey(triple) &&
                triplesMap.get(node).get(triple).containsKey(to.getClassName())) {
            triplesMap.get(node).get(triple).get(to.getClassName()).remove(to.getId());
        }
        if (
                triplesMapByDate.containsKey(node) &&
                triplesMapByDate.get(node).containsKey(triple) &&
                triplesMapByDate.get(node).get(triple).containsKey(to.getClassName()) &&
                triplesMapByDate.get(node).get(triple).get(to.getClassName()).containsKey(to.getYear()) &&
                triplesMapByDate.get(node).get(triple).get(to.getClassName()).get(to.getYear()).containsKey(to.getMonth()) &&
                triplesMapByDate.get(node).get(triple).get(to.getClassName()).get(to.getYear()).get(to.getMonth()).containsKey(to.getId())
        ) {
            triplesMapByDate.get(node).get(triple).get(to.getClassName()).get(to.getYear()).get(to.getMonth()).remove(to.getId());
        }
    }

    /**
     * Save all in Redis
     */
    @Override
    public void saveInCache() {
        try {
            redisServiceImp.setTriplesMap(this.triplesMap, true, true);
            redisServiceImp.setFilteredTriples(this.filtered);
            redisServiceImp.setEntityStats(this.statsHandler);
            redisServiceImp.setElasticSearchTriplesMap(this.esTriplesMap);
        } catch (Exception e) {
            logger.error("Error in saveInCache: {}",e.getMessage());
        }
    }

    /**
     * Save Triples Map estructure in Redis
     */
    @Override
    public void saveTriplesMapInCache() {
        try {
            redisServiceImp.setTriplesMap(this.triplesMap, true,true);
        } catch (Exception e) {
            logger.error("Error in saveTriplesMapInCache: {}",e.getMessage());
        }
    }

    /**
     * Save Triples Map estructure in Redis by node, tripleStore and className
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @param className String. The class name.
     */
    @Override
    public void saveTriplesMapInCache(String node, String tripleStore, String className) {
        try {
            redisServiceImp.setTriplesMap(getTipleMapByNodeAndTripleStoreAndClassName(node, tripleStore, className), true, true);
        } catch (Exception e) {
            logger.error("Error saving in REDIS: {}",e.getMessage());
        }
    }

    /**
     * Get TripleObject by node, tripleStore and className
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @param className String. The class name.
     */
    @Override
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTipleMapByNodeAndTripleStoreAndClassName(String node, String tripleStore, String className) {
        Map<String, Map<String, Map<String, Map<String, TripleObject>>>> returnedTripleMap = new HashMap<>();
        for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nodeEntry : getTriplesMap().entrySet()) { // Node Loop
            if (node == null || nodeEntry.getKey().trim().equals(node.trim())) {
                if (!returnedTripleMap.containsKey(nodeEntry.getKey()))
                    returnedTripleMap.put(nodeEntry.getKey(), new HashMap<>());
                for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleStoreEntry : nodeEntry.getValue().entrySet()) { // TripleStore Loop Loop
                    if (tripleStore == null || tripleStoreEntry.getKey().trim().equals(tripleStore.trim())) {
                        if (!returnedTripleMap.get(nodeEntry.getKey()).containsKey(tripleStoreEntry.getKey()))
                            returnedTripleMap.get(nodeEntry.getKey()).put(tripleStoreEntry.getKey(), new HashMap<>());
                        for (Map.Entry<String, Map<String, TripleObject>> classEntry : tripleStoreEntry.getValue().entrySet()) {
                            if (className == null || classEntry.getKey().trim().equals(className.trim())) {
                                if (!returnedTripleMap.get(nodeEntry.getKey()).get(tripleStoreEntry.getKey()).containsKey(classEntry.getKey()))
                                    returnedTripleMap.get(nodeEntry.getKey()).get(tripleStoreEntry.getKey()).put(classEntry.getKey(), new HashMap<>());
                                for (Map.Entry<String, TripleObject> instanceEntry : classEntry.getValue().entrySet()) {
                                    returnedTripleMap.get(nodeEntry.getKey()).get(tripleStoreEntry.getKey()).get(classEntry.getKey()).put(instanceEntry.getKey(),instanceEntry.getValue());
                                }
                            }
                        }

                    }
                }
            }
        }
        return returnedTripleMap;
    }

    /**
     * Save Filter Map In Cache
     */
    @Override
    public void saveFilterMapInCache() {
        try {
            redisServiceImp.setFilteredTriples(this.filtered);
        } catch (Exception e) {
            logger.error("Error in Save Filter Map in Cache");
        }
    }

    /**
     * Save Entity Stats In Cache
     */
    @Override
    public void saveEntityStatsInCache() {
        try {
            redisServiceImp.setEntityStats(this.statsHandler);
        } catch (Exception e) {
            logger.error("Error in setEntityStats: {}",e.getMessage());
        }
    }

    /**
     * Save Elastic Search Triples Map In Cache
     */
    @Override
    public void saveElasticSearchTriplesMapInCache() {
        try {
            redisServiceImp.setElasticSearchTriplesMap(this.esTriplesMap);
        } catch (Exception e) {
            logger.error("Error in setElasticSearchTriplesMap: {}",e.getMessage());
        }
    }

    /**
     * Load Triples Map from Redis Cache
     * @return Map<String, Map<String, Map<String, Map<String,TripleObject>>>>. The Triples Map structure
     */
    @Override
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> loadTiplesMapFromCache() {
        try {
            return redisServiceImp.getTriplesMap();
        } catch (Exception e) {
            logger.error("Error in loadTiplesMapFromCache: {}",e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Load Elastic Search Triples Map from Redis Cache
     * @return Map<String, Map<String, Map<String, Map<String,TripleObject>>>> . Elastic Search Triples Map  structure
     */
    @Override
    public Map<String, Map<String, Map<String, TripleObject>>> loadFilteredMapFromCache() {
        try {
            return redisServiceImp.getFilteredTriples();
        } catch (Exception e) {
            logger.error("Error in loadFilteredMapFromCache: {}",e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Load stats from cache
     * @see StatsHandler
     * @return StatsHandler
     */
    @Override
    public StatsHandler loadEntitiesStatsFromCache() {
        try {
            return redisServiceImp.getEntityStats();
        } catch (Exception e) {
            logger.error("Error in loadEntitiesStatsFromCache: {}",e.getMessage());
            return null;
        }
    }

    /**
     * Load Elastic Search Triples Map from Redis Cache
     * @return Map<String, Map<String, Map<String, Map<String,TripleObject>>>> . Elastic Search Triples Map  structure
     */
    @Override
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> loadElasticSearchTiplesMapFromCache() {
        try {
            return redisServiceImp.getElasticSearchTriplesMap();
        } catch (Exception e) {
            logger.error("Error in loadElasticSearchTiplesMapFromCache: {}",e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Check if is populated cache
     * @return boolean
     */
    @Override
    public boolean isPopulatedCache() {
        return (triplesMap.size()>0 && filtered.size()>0);
    }

    /**
     * Generaty entity stats
     */
    @Override
    public void generateEntityStats() {
        statsHandler.cleanStats();
        for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nodeEntry: triplesMap.entrySet()) { // Node
            for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleEntry: nodeEntry.getValue().entrySet()) { // Triple
                for (Map.Entry<String, Map<String, TripleObject>> classEntry: tripleEntry.getValue().entrySet()) { // Class
                    for (Map.Entry<String, TripleObject> tipleObjectEntry: classEntry.getValue().entrySet()) { // TripleObject
                        statsHandler.addAttributes(nodeEntry.getKey(),tripleEntry.getKey(),tipleObjectEntry.getValue());
                    }
                }
            }
        }
        if (redisServiceImp!=null)
            redisServiceImp.setEntityStats(statsHandler);
    }

    @Override
    public Map<String, Map<String, Map<String, Map<String, Map<String, Pair<String,TripleObject>>>>>> getInverseMap() {
        return inversePointersMap;
    }

    /**
     * Generaty entity stats
     */
    @Override
    public void generateInverseMap() {
        for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nodeEntry: triplesMap.entrySet()) { // Node
            for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleEntry: nodeEntry.getValue().entrySet()) { // Triple
                for (Map.Entry<String, Map<String, TripleObject>> classEntry: tripleEntry.getValue().entrySet()) { // Class
                    logger.info("\t Generating inverse dependencies by class {} with {} instances",classEntry.getKey(),classEntry.getValue().size());
                    for (Map.Entry<String, TripleObject> tipleObjectEntry: classEntry.getValue().entrySet()) { // TripleObject
                        aggregateToInverseMap(tipleObjectEntry.getValue());
                    }
                }
            }
        }
        logger.info("...Inverse dependencies generated");
    }

    private void aggregateToInverseMap(TripleObject toOrigin){ // toObj  => Origen
        try {
            for (Map.Entry<String, Object> att : toOrigin.getAttributes().entrySet()) {
                if (att.getValue() instanceof List) {
                    for ( Object lValue:  ((List)att.getValue())) {
                        extractLinks(toOrigin, att.getKey(), lValue);
                    }
                } else {
                    extractLinks(toOrigin, att.getKey(), att.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<String, Map<String, Pair<String, TripleObject>>> getDependencies(String node, String triple, String className) {
        try {
            return inversePointersMap.get(node).get(triple).get(className);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Pair<String, TripleObject>> getDependencies(String node, String triple, String className, String entityId) {
        try {
            return inversePointersMap.get(node).get(triple).get(className).get(entityId);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void extractLinks(TripleObject toOrigin, String attKey, Object attValue) {
        if (attValue!=null && Utils.isValidString(attValue.toString())) {
            // uc  => Destino
            URIComponent uc = Utils.getInstanceLink(attValue.toString(), serviceImp.getCanonicalSchema(), domain);
            if (uc != null && uc.getConcept() != null && uc.getReference() != null) {
                TripleObject toTarget = getTripleObject(toOrigin.getTripleStore().getNode().getNodeName(), toOrigin.getTripleStore().getName(), uc.getConcept(), uc.getReference());
                if (toTarget != null) {
                    if (!inversePointersMap.containsKey(toTarget.getTripleStore().getNode().getNodeName()))
                        inversePointersMap.put(toTarget.getTripleStore().getNode().getNodeName(), new HashMap<>());
                    if (!inversePointersMap.get(toTarget.getTripleStore().getNode().getNodeName()).containsKey(toTarget.getTripleStore().getName()))
                        inversePointersMap.get(toTarget.getTripleStore().getNode().getNodeName()).put(toTarget.getTripleStore().getName(), new HashMap<>());
                    if (!inversePointersMap.get(toTarget.getTripleStore().getNode().getNodeName()).get(toTarget.getTripleStore().getName()).containsKey(toTarget.getClassName())) {
                        inversePointersMap.get(toTarget.getTripleStore().getNode().getNodeName()).get(toTarget.getTripleStore().getName()).put(toTarget.getClassName(), new HashMap<>());
                    }
                    if (!inversePointersMap.get(toTarget.getTripleStore().getNode().getNodeName()).get(toTarget.getTripleStore().getName()).get(toTarget.getClassName()).containsKey(toTarget.getId())) {
                        inversePointersMap.get(toTarget.getTripleStore().getNode().getNodeName()).get(toTarget.getTripleStore().getName()).get(toTarget.getClassName()).put(toTarget.getId(), new HashMap<>());
                    }
                    inversePointersMap.get(toTarget.getTripleStore().getNode().getNodeName()).get(toTarget.getTripleStore().getName()).get(toTarget.getClassName()).get(toTarget.getId()).put(toOrigin.getId(), new Pair(attKey,toOrigin));
                }
            }
        }
    }

    /**
     * Get Links To TripleObjects
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @param className String. The class name.
     * @return Map<String,String> with id of Triple Object that reference the Triple Object give in parameter and the property name
     */
    @Override
    public Map<String,Pair<String,TripleObject>> getLinksToTripleObject(TripleObject to) {
        if (
            to !=null &&
            inversePointersMap.containsKey(to.getTripleStore().getNode().getNodeName()) &&
            inversePointersMap.get(to.getTripleStore().getNode().getNodeName()).containsKey(to.getTripleStore().getName()) &&
            inversePointersMap.get(to.getTripleStore().getNode().getNodeName()).get(to.getTripleStore().getName()).containsKey(to.getClassName())  &&
            inversePointersMap.get(to.getTripleStore().getNode().getNodeName()).get(to.getTripleStore().getName()).get(to.getClassName()).containsKey(to.getId())
        ) {
           return  inversePointersMap.get(to.getTripleStore().getNode().getNodeName()).get(to.getTripleStore().getName()).get(to.getClassName()).get(to.getId());
        }
        return new HashMap<>();
    }

    /**
     * Get filtered iterator
     * @return Iterator<TripleObject>
     */
    @Override
    public Iterator<TripleObject> getFilteredIterator() {
        List<TripleObject> tripleObjects = new ArrayList<>();
        for (Map.Entry<String, Map<String,Map<String, TripleObject>>> nodeEntry : filtered.entrySet()) {
            for (Map.Entry<String, Map<String, TripleObject>> tripleEntry : filtered.get(nodeEntry.getKey()).entrySet()) {
                for (Map.Entry<String, TripleObject> tripleObjectEntry : filtered.get(nodeEntry.getKey()).get(tripleEntry.getKey()).entrySet()) {
                    tripleObjects.add(tripleObjectEntry.getValue());
                }
            }
        }
        return tripleObjects.iterator();
    }

    /**
     * Get all triple Objects by node and triple store
     * @see TripleObject
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @return Set<TripleObject>
     */
    @Override
    public Set<TripleObject> getAllTripleObjects(String node, String tripleStore) {
        Set<TripleObject> triples = new HashSet<>();
        if (triplesMap!=null && triplesMap.entrySet() != null) {
            for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nodeEntry : triplesMap.entrySet()) { // Node
                if (nodeEntry!=null && nodeEntry.getKey().equals(node) && nodeEntry.getValue() != null && nodeEntry.getValue().entrySet() != null) {
                    for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleEntry : nodeEntry.getValue().entrySet()) { // Node
                        if (tripleEntry!=null && tripleEntry.getKey().equals(tripleStore) && tripleEntry.getValue() != null && tripleEntry.getValue().entrySet() != null) {
                            for (Map.Entry<String, Map<String, TripleObject>> classEntry : tripleEntry.getValue().entrySet()) { // Node
                                if (classEntry!=null && classEntry.getValue() != null && classEntry.getValue().entrySet() != null) {
                                    for (Map.Entry<String, TripleObject> tipleObjectEntry : classEntry.getValue().entrySet()) { // Node
                                        triples.add(tipleObjectEntry.getValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return triples;
    }

    /**
     * Get all triple Objects by node ,triple store and class name
     * @see TripleObject
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @param className String. The class name.
     * @return Map<String,TripleObject>
     */
    @Override
    public Map<String, TripleObject> getTripleObjects(String node, String tripleStore, String className) {
        if (this.triplesMap.containsKey(node) && this.triplesMap.get(node).containsKey(tripleStore) && this.triplesMap.get(node).get(tripleStore).containsKey(className))
            return this.triplesMap.get(node).get(tripleStore).get(className);
        else
            return null;
    }

    /**
     * Get specific  triple Objects by node ,triple store , class name and id
     * @see TripleObject
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @param className String. The class name.
     * @param id String. The id of the entity
     * @return
     */
    @Override
    public TripleObject getTripleObject(String node, String tripleStore, String className, String id) {
        try {
            if (this.triplesMap.containsKey(node) && this.triplesMap.get(node).containsKey(tripleStore) && this.triplesMap.get(node).get(tripleStore).containsKey(className) && this.triplesMap.get(node).get(tripleStore).get(className).containsKey(id))
                return this.triplesMap.get(node).get(tripleStore).get(className).get(id);
            else
                return null;
        } catch (Exception e) {
            logger.error(String.format("Error in getTripleObject: %s", e.getMessage()));
            return null;
        }

    }

    @Override
    public TripleObject getTripleObject(TripleObject tripleObject) {
        TripleObject to = getTripleObject(
                tripleObject.getTripleStore().getNode().getNodeName(),
                tripleObject.getTripleStore().getName(),
                tripleObject.getClassName(),
                tripleObject.getId());
        return (to!=null)?to:tripleObject;
    }

    /**
     * Get all int Triples Map
     * @return Map<String, Map<String, Map<String, Map<String, TripleObject>>>>
     */
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap() {
        return triplesMap;
    }

    /**
     * Update Triple map in memory
     * @param triplesMap Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap. The new Triple Map
     */
    public void setTriplesMap(Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap) {
        this.triplesMap = triplesMap;
        if (triplesMap!=null && triplesMap.entrySet()!=null) {
            for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> node : triplesMap.entrySet()) {
                if (!triplesMapByDate.containsKey(node.getKey()))
                    triplesMapByDate.put(node.getKey(), new HashMap<>());
                if (node!=null && node.getValue() != null && node.getValue().entrySet() != null){
                    for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleStore : node.getValue().entrySet()) {
                        if (!triplesMapByDate.get(node.getKey()).containsKey(tripleStore.getKey()))
                            triplesMapByDate.get(node.getKey()).put(tripleStore.getKey(), new HashMap<>());
                        if (tripleStore != null && tripleStore.getValue() != null && tripleStore.getValue().entrySet() != null) {
                            for (Map.Entry<String, Map<String, TripleObject>> className : tripleStore.getValue().entrySet()) {
                                logger.info("complete load in cache node: {}, tripleStore: {}, class: {}",node.getKey(),tripleStore.getKey(),className.getKey());
                                if (!triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).containsKey(className.getKey()))
                                    triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).put(className.getKey(), new HashMap<>());
                                if (className != null &&  className.getValue() != null && className.getValue().entrySet() != null) {
                                    for (Map.Entry<String, TripleObject> to : className.getValue().entrySet()) {
                                        to.getValue().buildFlattenAttributes();
                                        TripleObject toInner = null;
                                        try {
                                            toInner = new ObjectMapper().convertValue(to.getValue(), TripleObject.class);
                                            if (toInner.getLastModification() != 0) {
                                                Calendar cal = Calendar.getInstance();
                                                cal.setTime(new Date(toInner.getLastModification()));
                                                int year = cal.get(Calendar.YEAR);
                                                int month = cal.get(Calendar.MONTH);
                                                if (!triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).get(className.getKey()).containsKey(year)) {
                                                    triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).get(className.getKey()).put(year, new HashMap<>());
                                                }
                                                if (!triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).get(className.getKey()).get(year).containsKey(month)) {
                                                    triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).get(className.getKey()).get(year).put(month, new HashMap<>());
                                                }
                                                triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).get(className.getKey()).get(year).get(month).put(toInner.getId(), toInner);

                                            }
                                        } catch (Exception e) {
                                            logger.error(e.getMessage());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.info("Completed load in cache");
    }

    /**
     * Update Triple map ES in memory
     * @param esTriplesMap Map<String, Map<String, Map<String, Map<String,TripleObject>>>>. The nes Triple Map ES
     */
    public void setEsTriplesMap(Map<String, Map<String, Map<String, Map<String,TripleObject>>>> esTriplesMap) {
        this.esTriplesMap = esTriplesMap;
    }

    /**
     * Get all Triple Objects
     * @see TripleObject
     * @return Set<TripleObject>
     */
    public Set<TripleObject> getEsTriplesMapAsSet() {
        Set<TripleObject> tos = new HashSet<>();
        for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nodeEntry: esTriplesMap.entrySet()) { // Node
            for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleEntry: nodeEntry.getValue().entrySet()) { // Triple
                for (Map.Entry<String, Map<String, TripleObject>> classEntry: tripleEntry.getValue().entrySet()) { // Class
                    for (Map.Entry<String, TripleObject> tipleObjectEntry: classEntry.getValue().entrySet()) { // TO
                        tos.add(tipleObjectEntry.getValue());
                    }
                }
            }
        }
        return tos;
    }

    /**
     * Return filtered Triple Map
     * @return Map<String, Map<String, Map<String, TripleObject>>>
     */
    public Map<String, Map<String, Map<String, TripleObject>>> getFiltered() {
        return filtered;
    }

    /**
     * Update filtered Triple Map. Map<String, Map<String, Map<String, TripleObject>>> the new triple map filtered
     * @param filtered
     */
    public void setFiltered(Map<String, Map<String, Map<String, TripleObject>>> filtered) {
        this.filtered = filtered;
    }

    /**
     * Get the Stats handler
     * @see StatsHandler
     * @return  StatsHandler
     */
    public StatsHandler getStatsHandler() {
        return statsHandler;
    }

    /**
     * UPDATE the Stats handler
     * @param statsHandler StatsHandler
     */
    public void setStatsHandler(StatsHandler statsHandler) {
        this.statsHandler = statsHandler;
    }

    /**
     * Get the list of all classes by node and triple store
     * @param node String. The node name
     * @param tripleStore String. The triple store name
     * @return List<String>
     */
    public  List<String> getAllClassesByNodeAndTripleStore(String node, String tripleStore) {
        if (triplesMap.containsKey(node) && triplesMap.get(node).containsKey(tripleStore)) {
            return new ArrayList<>(triplesMap.get(node).get(tripleStore).keySet());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Update all stats
     */
    @Override
    public void updateStats() {
        statsHandler = new StatsHandler();
        statsHandler.cleanStats();
        for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nodeEntry: triplesMap.entrySet()) { // Node
            for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleEntry: nodeEntry.getValue().entrySet()) { // Triple
                for (Map.Entry<String, Map<String, TripleObject>> classEntry: tripleEntry.getValue().entrySet()) { // Class
                    logger.info("Update Stats by class: {}",classEntry.getKey());
                    for (Map.Entry<String, TripleObject> tipleObjectEntry: classEntry.getValue().entrySet()) { // TO
                        try {
                            TripleStore ts = tipleObjectEntry.getValue().getTripleStore();
                            statsHandler.addAttributes(ts.getNode().getNodeName(), ts.getName(), tipleObjectEntry.getValue());
                        } catch (Exception e) {
                            logger.error("Update Stats Error: {}",e.getMessage());
                        }
                    }
                }
            }
        }
    }

    /**
     * Get all nodes
     * @return Set<String>
     */
    @Override
    public Set<String> getAllNodes() {
        return triplesMap.keySet();
    }

    /**
     * Get all triple stores
     * @param node
     * @return Set<String>
     */
    @Override
    public Set<String> getAllTripleStoreByNode(String node) {
        Set<String> tripleStores = new HashSet<>();
        if (triplesMap.containsKey(node)) {
            return triplesMap.get(node).keySet();
        }
        return tripleStores;
    }
}
