package es.um.asio.service.service;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.EntityStats;

import java.util.Iterator;
import java.util.Map;
import java.util.Date;

public interface CacheService {

    public void addTripleObject(String node, String triple, TripleObject to, Date filterDate);

    public void saveInCache();

    public Map<String, Map<String, Map<String, Map<String,TripleObject>>>> loadTiplesMapFromCache();

    public Map<String,Map<String, Map<String,TripleObject>>> loadFilteredMapFromCache();

    public EntityStats loadEntitiesStatsFromCache();

    public boolean isPopulatedCache();

    public void generateEntityStats();

    public Iterator<TripleObject> getFilteredIterator();

    public Map<String,TripleObject> getTripleObjects(String node,String tripleStore, String className);
}
