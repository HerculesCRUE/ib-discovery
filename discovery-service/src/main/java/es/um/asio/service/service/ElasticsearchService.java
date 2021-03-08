package es.um.asio.service.service;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import org.javatuples.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Elasticsearch handler interface. For handle to interact with Elasticsearch
 * @see TripleObject
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface ElasticsearchService {


    /**
     * Save in Elasticsearch
     * @see TripleObjectES
     * @param toES TripleObjectES. Object to save
     * @return String
     */
    public String saveTripleObjectES(TripleObjectES toES);

    /**
     * Save list in Elasticsearch
     * @see TripleObjectES
     * @param tosES List<TripleObjectES>. List of Objects to save
     * @return Map<String, Map<String, String>>.
     */
    public Map<String, Map<String, String>> saveTripleObjectsES(List<TripleObjectES> tosES);

    /**
     * Save in Elasticsearch
     * @see TripleObject
     * @param to TripleObject. Object to save
     * @return String
     */
    public String saveTripleObject(TripleObject to);

    /**
     * Save list in Elasticsearch
     * @see TripleObject
     * @param tosES List<TripleObject>. List of Objects to save
     * @return Map<String, Map<String, String>>.
     */
    public Map<String, Map<String, String>> saveTripleObjects(List<TripleObject> tosES);

    /**
     * Delete in Elasticsearch
     * @see TripleObjectES
     * @param toES TripleObjectES. Object to delete
     */
    public void deleteTripleObjectES(TripleObjectES toES);

    /**
     * Delete in Elasticsearch
     * @see TripleObjectES
     * @param tosES List<TripleObjectES>. List of Objects to delete
     */
    public void deleteTripleObjectsES(List<TripleObjectES> tosES);

    /**
     * Delete in Elasticsearch
     * @see TripleObject
     * @param to TripleObject. Object to delete
     */
    public void deleteTripleObject(TripleObject to);

    /**
     * Delete in Elasticsearch
     * @see TripleObject
     * @param tosES List<TripleObject>. List of Objects to delete
     */
    public void deleteTripleObjects(List<TripleObject> tosES);

    /**
     * Get all objects stored in Elasticsearch
     * @return List<TripleObjectES>
     */
    public List<TripleObjectES> getAll();

    /**
     * Get all objects stored in Elasticsearch, filtered by Node, Triple Store and ClassName
     * @param node String. The Node to filter
     * @param tripleStore String. The Triple Store to filter
     * @param className String. The Class Name to filter
     * @return List<TripleObjectES>
     */
    public List<TripleObjectES> getAllByNodeAndTripleStoreAndClassName(String node, String tripleStore, String className);

    /**
     * Get Map with the entity id as key and the object in TripleObjectES model as value
     * @see TripleObjectES
     * @return Map<String,TripleObjectES>
     */
    public Map<String,TripleObjectES> getAllMappedById();

    /**
     * Get TripleObjectES object stored in Elasticsearch, filtered by Id
     * @see TripleObjectES
     * @param id String. The entity Id
     * @return TripleObjectES
     */
    public TripleObjectES getTripleObjectESById(String id);

    /**
     * Get TripleObject object stored in Elasticsearch, filtered by Id
     * @see TripleObject
     * @param id String. The entity Id
     * @return TripleObject
     */
    public TripleObject getTripleObjectById(String id);

    /**
     * Build Query in Elasticsearch for search similar object filtered by Node, Triple Store and ClassName with similarities in attributes pass in parameter
     * @see TripleObjectES
     * @param indexName String. The index in Elasticsearch where search
     * @param node String. The Node to filter
     * @param tripleStore String. The Triple Store to filter
     * @param className String. The Class Name to filter
     * @param params List<Pair<String,Object>>. List of attributes to search similarities where the key is the attribute and the value is the value which must be similar
     * @return List<TripleObjectES>  with the similarities found
     */
    public List<TripleObjectES> getTripleObjectsESByFilterAndAttributes(String indexName, String node, String tripleStore,String className, List<Pair<String,Object>> params);

    /**
     * Build Query in Elasticsearch for search similar object filtered by Node, Triple Store and ClassName with similarities in attributes pass in parameter
     * @see TripleObject
     * @param indexName String. The index in Elasticsearch where search
     * @param node String. The Node to filter
     * @param tripleStore String. The Triple Store to filter
     * @param className String. The Class Name to filter
     * @param params List<Pair<String,Object>>. List of attributes to search similarities where the key is the attribute and the value is the value which must be similar
     * @return List<TripleObject> with the similarities found
     */
    public List<TripleObject> getTripleObjectsByFilterAndAttributes(String indexName, String node, String tripleStore,String className, List<Pair<String,Object>> params);

    /**
     * Search similar objects at TripleObject pass in parameter
     * @see TripleObject
     * @see TripleObjectES
     * @param tripleObject TripleObject to search similarities
     * @return List<TripleObjectES> with the similarities found
     */
    public List<TripleObjectES> getSimilarTripleObjectsES(TripleObject tripleObject);

    /**
     * Search similar objects at TripleObject pass in parameter
     * @see TripleObject
     * @param tripleObject TripleObject to search similarities
     * @return List<TripleObject> with the similarities found
     */
    public List<TripleObject> getSimilarTripleObjects(TripleObject tripleObject);

    /**
     * Get all Objects filtered by Node and Triple Store, in SimplifiedTripleObject form
     * @param node String. The Node name to filter
     * @param tripleStore. The Triple Store name to filter
     * @return Map<String, Set<String>>
     */
    public Map<String, Set<String>> getAllSimplifiedTripleObject(String node, String tripleStore);
}
