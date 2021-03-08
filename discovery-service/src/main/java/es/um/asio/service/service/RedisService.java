package es.um.asio.service.service;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.StatsHandler;

import java.util.Map;

/**
 * RedisService interface. For handle operations with REDIS
 * @see TripleObject
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface RedisService {

    /**
     * Get all Triple Object With recursive Map structure stored by Node -> Triple Store -> Class Name -> Id, TripleObject
     * @see TripleObject
     * @return Map<String, Map<String, Map<String, Map<String, TripleObject>>>>
     */
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap();

    /**
     * Get all Triple Object With recursive Map structure stored by Node -> Triple Store -> Class Name -> Id, TripleObject, filtered by Node, Triple Store and Class Name
     * @see TripleObject
     * @param node String. The node name
     * @param tripleStore String. The triple store name
     * @param className String. The class name
     * @return Map<String, Map<String, Map<String, Map<String, TripleObject>>>>
     */
    public Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMapByNodeAndStorageAndClass(String node, String tripleStore, String className);

    /**
     * Store in REDIS, the structure Map<String, Map<String, Map<String, Map<String, TripleObject>>>> which is a recursive Map structure stored by Node -> Triple Store -> Class Name -> Id, TripleObject
     * @see TripleObject
     * @param triplesMap Map<String, Map<String, Map<String, Map<String, TripleObject>>>>. The structure to save
     * @param keepKeys boolean. If false old keys will be deleted, else the keys will be preserved
     * @param doAsync boolean. If true the operation will be done in asynchronous mode, else the operation will be in synchronous mode
     */
    public void setTriplesMap(Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap, boolean keepKeys, boolean doAsync);

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
    public void setTriplesMapByNodeAndStorageAndClass(String node, String tripleStore, String className, Map<String, TripleObject> triplesMap, boolean doAsync);

    /**
     * Get Triple Objects filtered by date of last modification
     * @see TripleObject
     * @return Map<String, Map<String, Map<String,TripleObject>>>
     */
    public Map<String, Map<String, Map<String,TripleObject>>> getFilteredTriples();

    /**
     * UPDATE Triple Objects filtered by date of last modification
     * @see TripleObject
     * @param filteredTriples Map<String, Map<String, Map<String,TripleObject>>>
     */
    public void setFilteredTriples(Map<String, Map<String, Map<String,TripleObject>>> filteredTriples);

    /**
     * Get the entities Stats
     * @see StatsHandler
     * @return StatsHandler
     */
    public StatsHandler getEntityStats();

    /**
     * UPDATE the entities Stats
     * @see StatsHandler
     * @param statsHandler StatsHandler. The stats of the entities
     */
    public void setEntityStats(StatsHandler statsHandler);

    /**
     * Get the Entities inserted in Elasticsearch
     * @return Map<String, Map<String, Map<String, Map<String,TripleObject>>>>
     */
    public Map<String, Map<String, Map<String, Map<String,TripleObject>>>> getElasticSearchTriplesMap();

    /**
     * UPDATE the Entities inserted in Elasticsearch
     * @return Map<String, Map<String, Map<String, Map<String,TripleObject>>>>
     */
    public void setElasticSearchTriplesMap(Map<String, Map<String, Map<String, Map<String,TripleObject>>>> elasticSearchTriplesMap);

}
