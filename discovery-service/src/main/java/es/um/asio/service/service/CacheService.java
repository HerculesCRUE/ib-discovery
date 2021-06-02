package es.um.asio.service.service;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.stats.StatsHandler;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * CacheService interface. For handle cached data
 * @see TripleObject
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface CacheService {

    /**
     * Add a new TripleObject in memory.
     * @see TripleObject
     * @param node String. The node name.
     * @param triple String. The triple store name.
     * @param to TripleObject. The triple object instance to add.
     */
    public void addTripleObject(String node, String triple, TripleObject to);

    /**
     * Add a new TripleObject in memory from TripleObjectES.
     * @see TripleObjectES
     * @param node String. The node name.
     * @param triple String. The triple store name.
     * @param toES TripleObjectES. The triple object ES instance to add.
     */
    public void addTripleObjectES(String node, String triple, TripleObjectES toES);

    /**
     * remove TripleObject from memory.
     * @see TripleObject
     * @param node String. The node name.
     * @param triple String. The triple store name.
     * @param to TripleObject. The triple object instance to add.
     */
    public void removeTripleObject(String node, String triple, TripleObject to);

    /**
     * Save all in Redis
     */
    public void saveInCache();

    /**
     * Save Triples Map estructure in Redis
     */
    public void saveTriplesMapInCache();

    /**
     * Save Triples Map estructure in Redis by node, tripleStore and className
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @param className String. The class name.
     */
    public void saveTriplesMapInCache(String node, String tripleStore, String className);

    /**
     * Get TripleObject by node, tripleStore and className
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @param className String. The class name.
     */
    public Map<String, Map<String, Map<String, Map<String,TripleObject>>>> getTipleMapByNodeAndTripleStoreAndClassName(String node, String tripleStore, String className);

    /**
     * Save Filter Map In Cache
     */
    public void saveFilterMapInCache();

    /**
     * Save Entity Stats In Cache
     */
    public void saveEntityStatsInCache();

    /**
     * Save Elastic Search Triples Map In Cache
     */
    public void saveElasticSearchTriplesMapInCache();


    /**
     * Load Triples Map from Redis Cache
     * @return Map<String, Map<String, Map<String, Map<String,TripleObject>>>>. The Triples Map structure
     */
    public Map<String, Map<String, Map<String, Map<String,TripleObject>>>> loadTiplesMapFromCache();

    /**
     * Load Filtered Map from Redis Cache
     * @return Map<String,Map<String, Map<String,TripleObject>>> . The Filtered Map structure
     */
    public Map<String,Map<String, Map<String,TripleObject>>> loadFilteredMapFromCache();

    /**
     * Load Elastic Search Triples Map from Redis Cache
     * @return Map<String, Map<String, Map<String, Map<String,TripleObject>>>> . Elastic Search Triples Map  structure
     */
    public Map<String, Map<String, Map<String, Map<String,TripleObject>>>> loadElasticSearchTiplesMapFromCache();

    /**
     * Load stats from cache
     * @see StatsHandler
     * @return StatsHandler
     */
    public StatsHandler loadEntitiesStatsFromCache();

    /**
     * Check if is populated cache
     * @return boolean
     */
    public boolean isPopulatedCache();

    /**
     * Generaty entity stats
     */
    public void generateEntityStats();

    /**
     * Get filtered iterator
     * @return Iterator<TripleObject>
     */
    public Iterator<TripleObject> getFilteredIterator();

    /**
     * Get all triple Objects by node and triple store
     * @see TripleObject
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @return Set<TripleObject>
     */
    public Set<TripleObject> getAllTripleObjects(String node, String tripleStore);

    /**
     * Get all triple Objects by node ,triple store and class name
     * @see TripleObject
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @param className String. The class name.
     * @return Map<String,TripleObject>
     */
    public Map<String,TripleObject> getTripleObjects(String node,String tripleStore, String className);

    /**
     * Get specific  triple Objects by node ,triple store , class name and id
     * @see TripleObject
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @param className String. The class name.
     * @param id String. The id of the entity
     * @return
     */
    public TripleObject getTripleObject(String node, String tripleStore, String className, String id);

    /**
     * Update all stats
     */
    public void updateStats();

    /**
     * Get all nodes
     * @return Set<String>
     */
    public Set<String> getAllNodes();

    /**
     * Get all triple stores
     * @param node
     * @return Set<String>
     */
    public Set<String> getAllTripleStoreByNode(String node);
}
