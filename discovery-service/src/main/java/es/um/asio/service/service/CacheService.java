package es.um.asio.service.service;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.StatsHandler;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface CacheService {

    public void addTripleObject(String node, String triple, TripleObject to);

    public void addTripleObjectES(String node, String triple, TripleObject to);

    public void removeTripleObject(String node, String triple, TripleObject to);

    public void saveInCache();

    public void saveTriplesMapInCache();

    public void saveTriplesMapInCache(String node, String tripleStore, String className);

    public Map<String, Map<String, Map<String, Map<String,TripleObject>>>> getTipleMapByNodeAndTripleStoreAndClassName(String node, String tripleStore, String className);

    public void saveFilterMapInCache();

    public void saveEntityStatsInCache();

    public void saveElasticSearchTriplesMapInCache();

    public Map<String, Map<String, Map<String, Map<String,TripleObject>>>> loadTiplesMapFromCache();

    public Map<String,Map<String, Map<String,TripleObject>>> loadFilteredMapFromCache();

    public Map<String, Map<String, Map<String, Map<String,TripleObject>>>> loadElasticSearchTiplesMapFromCache();

    public StatsHandler loadEntitiesStatsFromCache();

    public boolean isPopulatedCache();

    public void generateEntityStats();

    public Iterator<TripleObject> getFilteredIterator();

    public Set<TripleObject> getAllTripleObjects();

    public Map<String,TripleObject> getTripleObjects(String node,String tripleStore, String className);

    public TripleObject getTripleObject(String node, String tripleStore, String className, String id);

    public void updateStats();
}
