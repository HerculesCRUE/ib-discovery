package es.um.asio.service.service;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.EntityStats;

import java.util.Map;

public interface RedisService {

    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap();

    public void setTriplesMap(Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap);

    public Map<String, Map<String, Map<String,TripleObject>>> getFilteredTriples();

    public void setFilteredTriples(Map<String, Map<String, Map<String,TripleObject>>> filteredTriples);

    public EntityStats getEntityStats();

    public void setEntityStats(EntityStats entityStats);

}
