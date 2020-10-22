package es.um.asio.service.service;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.StatsHandler;

import java.util.Map;

public interface RedisService {

    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap();

    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMapByNodeAndStorageAndClass(String node, String tripleStore, String className);

    public void setTriplesMap(Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap, boolean keepKeys, boolean doAsync);

    public void setTriplesMapByNodeAndStorageAndClass(String node, String tripleStore, String className, Map<String, TripleObject> triplesMap, boolean doAsync);

    public Map<String, Map<String, Map<String,TripleObject>>> getFilteredTriples();

    public void setFilteredTriples(Map<String, Map<String, Map<String,TripleObject>>> filteredTriples);

    public StatsHandler getEntityStats();

    public void setEntityStats(StatsHandler statsHandler);

    public Map<String, Map<String, Map<String, Map<String,TripleObject>>>> getElasticSearchTriplesMap();

    public void setElasticSearchTriplesMap(Map<String, Map<String, Map<String, Map<String,TripleObject>>>> elasticSearchTriplesMap);

}
