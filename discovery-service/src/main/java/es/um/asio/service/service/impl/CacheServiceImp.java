package es.um.asio.service.service.impl;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.swing.text.html.parser.Entity;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CacheServiceImp implements CacheService {

    private Map<String, Map<String, Map<String, Map<String,TripleObject>>>> triplesMap; // Class --> Instances
    private Map<String, Map<String, Map<String, Map<Integer,Map<Integer,Map<String,TripleObject>>>>>> triplesMapByDate; // Class --> Instances
    private Map<String, Map<String, Map<String,TripleObject>>> filtered;
    private Map<String, Map<String, Map<String, Map<String,TripleObject>>>> esTriplesMap; // Class --> Instances
    private EntityStats entityStats;
    DateFormat dateFormat;

    @Autowired
    RedisServiceImp redisServiceImp;

    @PostConstruct
    private void initialize() throws Exception {
        triplesMap = new HashMap<>();
        triplesMapByDate = new HashMap<>();
        filtered = new HashMap<>();
        esTriplesMap = new HashMap<>();
        entityStats = new EntityStats();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    @Override
    public void addTripleObject(String node, String triple, TripleObject to) {
        if (!triplesMap.containsKey(node)) {
            triplesMap.put(node,new HashMap<>());
        }

        if (!triplesMapByDate.containsKey(node))
            triplesMapByDate.put(node,new HashMap<>());

        if (!triplesMap.get(node).containsKey(triple)) {
            triplesMap.get(node).put(triple, new HashMap<>());
        }

        if (!triplesMapByDate.get(node).containsKey(triple)) {
            triplesMapByDate.get(node).put(triple, new HashMap<>());
        }

        if (!triplesMap.get(node).get(triple).containsKey(to.getClassName())) {
            triplesMap.get(node).get(triple).put(to.getClassName(),new HashMap<>());
        }

        if (!triplesMapByDate.get(node).get(triple).containsKey(to.getClassName())) {
            triplesMapByDate.get(node).get(triple).put(to.getClassName(),new HashMap<>());
        }

        if (!triplesMapByDate.get(node).get(triple).get(to.getClassName()).containsKey(to.getYear())) {
            triplesMapByDate.get(node).get(triple).get(to.getClassName()).put(to.getYear(),new HashMap<>());
        }

        if (!triplesMapByDate.get(node).get(triple).get(to.getClassName()).get(to.getYear()).containsKey(to.getMonth())) {
            triplesMapByDate.get(node).get(triple).get(to.getClassName()).get(to.getYear()).put(to.getMonth(),new HashMap<>());
        }

        triplesMap.get(node).get(triple).get(to.getClassName()).put(to.getId(),to);
        triplesMapByDate.get(node).get(triple).get(to.getClassName()).get(to.getYear()).get(to.getMonth()).put(to.getId(),to);

        entityStats.addAttributes(node,triple,to);
    }

    @Override
    public void addTripleObjectES(String node, String triple, TripleObject to) {
        if (!esTriplesMap.containsKey(node)) {
            esTriplesMap.put(node,new HashMap<>());
        }

        if (!esTriplesMap.get(node).containsKey(triple)) {
            esTriplesMap.get(node).put(triple, new HashMap<>());
        }

        if (!esTriplesMap.get(node).get(triple).containsKey(to.getClassName())) {
            esTriplesMap.get(node).get(triple).put(to.getClassName(),new HashMap<>());
        }

        esTriplesMap.get(node).get(triple).get(to.getClassName()).put(to.getId(),to);
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
        redisServiceImp.setTriplesMap(this.triplesMap);
        redisServiceImp.setFilteredTriples(this.filtered);
        redisServiceImp.setEntityStats(this.entityStats);
        redisServiceImp.setElasticSearchTriplesMap(this.esTriplesMap);
    }

    @Override
    public void saveTriplesMapInCache() {
        redisServiceImp.setTriplesMap(this.triplesMap);
    }

    @Override
    public void saveFilterMapInCache() {
        redisServiceImp.setFilteredTriples(this.filtered);
    }

    @Override
    public void saveEntityStatsInCache() {
        redisServiceImp.setEntityStats(this.entityStats);
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
    public EntityStats loadEntitiesStatsFromCache() {
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
    public Set<TripleObject> getAllTripleObjects() {
        Set<TripleObject> triples = new HashSet<>();
        for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nodeEntry: triplesMap.entrySet()) { // Node
            for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleEntry: nodeEntry.getValue().entrySet()) { // Node
                for (Map.Entry<String, Map<String, TripleObject>> classEntry: tripleEntry.getValue().entrySet()) { // Node
                    for (Map.Entry<String, TripleObject> tipleObjectEntry: classEntry.getValue().entrySet()) { // Node
                        triples.add(tipleObjectEntry.getValue());
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
/*        boolean l1 = this.triplesMap.containsKey(node);
        boolean l2 = this.triplesMap.get(node).containsKey(tripleStore);
        boolean l3 = this.triplesMap.get(node).get(tripleStore).containsKey(className);
        boolean l4 = this.triplesMap.get(node).get(tripleStore).get(className).containsKey(id);*/
        if (this.triplesMap.containsKey(node) && this.triplesMap.get(node).containsKey(tripleStore) && this.triplesMap.get(node).get(tripleStore).containsKey(className) && this.triplesMap.get(node).get(tripleStore).get(className).containsKey(id))
            return this.triplesMap.get(node).get(tripleStore).get(className).get(id);
        else
            return null;
    }

    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap() {
        return triplesMap;
    }

    public void setTriplesMap(Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap) {
        this.triplesMap = triplesMap;
        for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> node: triplesMap.entrySet()) {
            if (!triplesMapByDate.containsKey(node.getKey()))
                triplesMapByDate.put(node.getKey(), new HashMap<>());
            for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tripleStore:node.getValue().entrySet()) {
                if (!triplesMapByDate.get(node.getKey()).containsKey(tripleStore.getKey()))
                    triplesMapByDate.get(node.getKey()).put(tripleStore.getKey(), new HashMap<>());
                for (Map.Entry<String, Map<String, TripleObject>> className : tripleStore.getValue().entrySet()) {
                    if (!triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).containsKey(className.getKey()))
                       triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).put(className.getKey(), new HashMap<>());
                    for (Map.Entry<String, TripleObject> to : className.getValue().entrySet()) {
                        TripleObject toInner = to.getValue();
                        if (toInner.getLastModification() != null) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(toInner.getLastModification());
                            int year = cal.get(Calendar.YEAR);
                            int month = cal.get(Calendar.MONTH);
                            if (!triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).get(className.getKey()).containsKey(year)) {
                                triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).get(className.getKey()).put(year, new HashMap<>());
                            }
                            if (!triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).get(className.getKey()).get(year).containsKey(month)) {
                                triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).get(className.getKey()).get(year).put(month, new HashMap<>());
                            }
                            triplesMapByDate.get(node.getKey()).get(tripleStore.getKey()).get(className.getKey()).get(year).get(month).put(toInner.getId(),toInner);

                        }
                    }
                }
            }

        }
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

    public EntityStats getEntityStats() {
        return entityStats;
    }

    public void setEntityStats(EntityStats entityStats) {
        this.entityStats = entityStats;
    }


}
