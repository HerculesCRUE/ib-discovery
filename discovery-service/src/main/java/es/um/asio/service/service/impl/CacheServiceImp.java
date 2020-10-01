package es.um.asio.service.service.impl;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CacheServiceImp implements CacheService {

    private Map<String, Map<String, Map<String, Map<String,TripleObject>>>> triplesMap; // Class --> Instances
    private Map<String, Map<String, Map<String,TripleObject>>> filtered;
    private Map<String, Map<String, Map<String, Map<String,TripleObject>>>> esTriplesMap; // Class --> Instances
    private EntityStats entityStats;
    DateFormat dateFormat;

    @Autowired
    RedisServiceImp redisServiceImp;

    @PostConstruct
    private void initialize() throws Exception {
        triplesMap = new HashMap<>();
        filtered = new HashMap<>();
        esTriplesMap = new HashMap<>();
        entityStats = new EntityStats();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    @Override
    public void addTripleObject(String node, String triple, TripleObject to, Date filterDate) {
        if (!triplesMap.containsKey(node)) {
            triplesMap.put(node,new HashMap<>());
        }
        if (!filtered.containsKey(node)) {
            filtered.put(node, new HashMap<>());
        }

        if (!triplesMap.get(node).containsKey(triple)) {
            triplesMap.get(node).put(triple, new HashMap<>());
        }
        if (!filtered.get(node).containsKey(triple)) {
            filtered.get(node).put(triple, new HashMap<>());
        }

        if (!triplesMap.get(node).get(triple).containsKey(to.getClassName())) {
            triplesMap.get(node).get(triple).put(to.getClassName(),new HashMap<>());
        }
        triplesMap.get(node).get(triple).get(to.getClassName()).put(to.getId(),to);

        if (to.getLastModification().compareTo(filterDate) >= 0) {
            filtered.get(node).get(triple).put(to.getId(),to);
        }

        entityStats.addAttributes(node,triple,to);
    }

    @Override
    public void saveInCache() {
        redisServiceImp.setTriplesMap(this.triplesMap);
        redisServiceImp.setFilteredTriples(this.filtered);
        redisServiceImp.setEntityStats(this.entityStats);
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
    public EntityStats loadEntitiesStatsFromCache() {
        return  redisServiceImp.getEntityStats();
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
                        entityStats.addAttributes(nodeEntry.getKey(),tripleEntry.getKey(),tipleObjectEntry.getValue());
                    }
                }
            }
        }
        redisServiceImp.setEntityStats(entityStats);
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
    public Map<String, TripleObject> getTripleObjects(String node, String tripleStore, String className) {
        if (this.triplesMap.containsKey(node) && this.triplesMap.get(node).containsKey(tripleStore) && this.triplesMap.get(node).get(tripleStore).containsKey(className))
            return this.triplesMap.get(node).get(tripleStore).get(className);
        else
            return null;
    }

    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap() {
        return triplesMap;
    }

    public void setTriplesMap(Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap) {
        this.triplesMap = triplesMap;
    }

    public Map<String, Map<String, Map<String, TripleObject>>> getFiltered() {
        return filtered;
    }

    public void setFiltered(Map<String, Map<String, Map<String, TripleObject>>> filtered) {
        this.filtered = filtered;
    }

    public EntityStats getEntityStats() {
        return entityStats;
    }

    public void setEntityStats(EntityStats entityStats) {
        this.entityStats = entityStats;
    }
}
