package es.um.asio.service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.stats.StatsHandler;
import es.um.asio.service.repository.relational.CacheRegistryRepository;
import es.um.asio.service.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CacheServiceImp implements CacheService {

    private final Logger logger = LoggerFactory.getLogger(CacheServiceImp.class);

    private Map<String, Map<String, Map<String, Map<String,TripleObject>>>> triplesMap; // Class --> Instances
    private Map<String, Map<String, Map<String, Map<Integer,Map<Integer,Map<String,TripleObject>>>>>> triplesMapByDate; // Class --> Instances
    private Map<String, Map<String, Map<String,TripleObject>>> filtered;
    private Map<String, Map<String, Map<String, Map<String,TripleObject>>>> esTriplesMap; // Class --> Instances
    private StatsHandler statsHandler;
    DateFormat dateFormat;

    @Autowired
    RedisServiceImp redisServiceImp;

    @Autowired
    CacheRegistryRepository cacheRegistryRepository;

    @PostConstruct
    private void initialize() throws Exception {
        triplesMap = new HashMap<>();
        triplesMapByDate = new HashMap<>();
        filtered = new HashMap<>();
        esTriplesMap = new HashMap<>();
        statsHandler = new StatsHandler();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

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

            //statsHandler.addAttributes(node, triple, to);
            // to.buildFlattenAttributes();
        }
    }

    @Override
    public void addTripleObjectES(String node, String triple, TripleObjectES toES) {
        TripleObject to = new TripleObject(toES);
        addTripleObject(node,triple,to);
    }

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

    @Override
    public void saveInCache() {
        redisServiceImp.setTriplesMap(this.triplesMap, true,true);
        redisServiceImp.setFilteredTriples(this.filtered);
        redisServiceImp.setEntityStats(this.statsHandler);
        redisServiceImp.setElasticSearchTriplesMap(this.esTriplesMap);
    }

    @Override
    public void saveTriplesMapInCache() {
        redisServiceImp.setTriplesMap(this.triplesMap, true,true);
    }

    @Override
    public void saveTriplesMapInCache(String node, String tripleStore, String className) {
        redisServiceImp.setTriplesMap(getTipleMapByNodeAndTripleStoreAndClassName(node,tripleStore,className), true, true);
    }

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

    @Override
    public void saveFilterMapInCache() {
        redisServiceImp.setFilteredTriples(this.filtered);
    }

    @Override
    public void saveEntityStatsInCache() {
        redisServiceImp.setEntityStats(this.statsHandler);
    }

    @Override
    public void saveElasticSearchTriplesMapInCache() {
        redisServiceImp.setElasticSearchTriplesMap(this.esTriplesMap);
    }

    @Override
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> loadTiplesMapFromCache() {
        return redisServiceImp.getTriplesMap();
    }

    @Override
    public Map<String, Map<String, Map<String, TripleObject>>> loadFilteredMapFromCache() {
        return redisServiceImp.getFilteredTriples();
    }

    @Override
    public StatsHandler loadEntitiesStatsFromCache() {
        return  redisServiceImp.getEntityStats();
    }

    @Override
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> loadElasticSearchTiplesMapFromCache() {
        return redisServiceImp.getElasticSearchTriplesMap();
    }

    @Override
    public boolean isPopulatedCache() {
        return (triplesMap.size()>0 && filtered.size()>0);
    }

    @Override
    public void generateEntityStats() {
        for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nodeEntry: triplesMap.entrySet()) { // Node
            for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleEntry: nodeEntry.getValue().entrySet()) { // Node
                for (Map.Entry<String, Map<String, TripleObject>> classEntry: tripleEntry.getValue().entrySet()) { // Node
                    for (Map.Entry<String, TripleObject> tipleObjectEntry: classEntry.getValue().entrySet()) { // Node
                        statsHandler.addAttributes(nodeEntry.getKey(),tripleEntry.getKey(),tipleObjectEntry.getValue());
                    }
                }
            }
        }
        redisServiceImp.setEntityStats(statsHandler);
    }

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

    @Override
    public Set<TripleObject> getAllTripleObjects() {
        Set<TripleObject> triples = new HashSet<>();
        if (triplesMap!=null && triplesMap.entrySet() != null) {
            for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nodeEntry : triplesMap.entrySet()) { // Node
                if (nodeEntry!=null && nodeEntry.getValue() != null && nodeEntry.getValue().entrySet() != null) {
                    for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleEntry : nodeEntry.getValue().entrySet()) { // Node
                        if (tripleEntry!=null && tripleEntry.getValue() != null && tripleEntry.getValue().entrySet() != null) {
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

    @Override
    public Map<String, TripleObject> getTripleObjects(String node, String tripleStore, String className) {
        if (this.triplesMap.containsKey(node) && this.triplesMap.get(node).containsKey(tripleStore) && this.triplesMap.get(node).get(tripleStore).containsKey(className))
            return this.triplesMap.get(node).get(tripleStore).get(className);
        else
            return null;
    }



    @Override
    public TripleObject getTripleObject(String node, String tripleStore, String className, String id) {
        try {
            if (this.triplesMap.containsKey(node) && this.triplesMap.get(node).containsKey(tripleStore) && this.triplesMap.get(node).get(tripleStore).containsKey(className) && this.triplesMap.get(node).get(tripleStore).get(className).containsKey(id))
                return this.triplesMap.get(node).get(tripleStore).get(className).get(id);
            else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap() {
        return triplesMap;
    }

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
                                logger.info("complete load in cache class: " + className.getKey());
                                if (!triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).containsKey(className.getKey()))
                                    triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).put(className.getKey(), new HashMap<>());
                                if (className != null && className.getValue() != null && className.getValue().entrySet() != null) {
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
                                            e.printStackTrace();
                                        }
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

    public void setEsTriplesMap(Map<String, Map<String, Map<String, Map<String,TripleObject>>>> esTriplesMap) {
        this.esTriplesMap = esTriplesMap;
    }

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

    public Map<String, Map<String, Map<String, TripleObject>>> getFiltered() {
        return filtered;
    }

    public void setFiltered(Map<String, Map<String, Map<String, TripleObject>>> filtered) {
        this.filtered = filtered;
    }

    public StatsHandler getStatsHandler() {
        return statsHandler;
    }

    public void setStatsHandler(StatsHandler statsHandler) {
        this.statsHandler = statsHandler;
    }

    @Override
    public void updateStats() {
        statsHandler = new StatsHandler();
        for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nodeEntry: triplesMap.entrySet()) { // Node
            for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleEntry: nodeEntry.getValue().entrySet()) { // Triple
                for (Map.Entry<String, Map<String, TripleObject>> classEntry: tripleEntry.getValue().entrySet()) { // Class
                    logger.info("Update Stats by class: "+ classEntry.getKey());
                    for (Map.Entry<String, TripleObject> tipleObjectEntry: classEntry.getValue().entrySet()) { // TO
                        try {
                            TripleStore ts = tipleObjectEntry.getValue().getTripleStore();
                            statsHandler.addAttributes(ts.getNode().getNode(), ts.getTripleStore(), tipleObjectEntry.getValue());
                        } catch (Exception e) {
                            logger.error("Update Stats Error: "+ e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
