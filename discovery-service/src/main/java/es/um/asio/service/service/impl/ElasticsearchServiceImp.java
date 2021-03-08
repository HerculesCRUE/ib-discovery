package es.um.asio.service.service.impl;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.relational.ElasticRegistry;
import es.um.asio.service.repository.elasticsearch.TripleObjectESCustomRepository;
import es.um.asio.service.repository.elasticsearch.TripleObjectESRepository;
import es.um.asio.service.repository.relational.ElasticRegistryRepository;
import es.um.asio.service.service.ElasticsearchService;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.ElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Elasticsearch handler implementation. For handle to interact with Elasticsearch
 * @see TripleObject
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public class ElasticsearchServiceImp implements ElasticsearchService {

    private final Logger logger = LoggerFactory.getLogger(ElasticsearchServiceImp.class);

    private static final String INSERTED = "inserted";

    @Autowired
    TripleObjectESRepository repository;

    @Autowired
    TripleObjectESCustomRepository customRepository;

    @Autowired
    CacheServiceImp cache;

    @Autowired
    ElasticRegistryRepository elasticRegistryRepository;

    @Autowired
    ApplicationState applicationState;

    private static final String FAIL = "fail";

    /**
     * Save in Elasticsearch
     * @see TripleObjectES
     * @param toES TripleObjectES. Object to save
     * @return String
     */
    @Override
    public String saveTripleObjectES(TripleObjectES toES) {
        try {
            repository.save(toES);
            return INSERTED;
        } catch (ElasticsearchException e) {
            Map<String, String> fails = e.getFailedDocuments();
            logger.error(e.getMessage());
            return fails.containsKey(String.valueOf(toES.getId()))?fails.get(String.valueOf(toES.getId())):FAIL;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return FAIL;
        }
    }

    /**
     * Save list in Elasticsearch
     * @see TripleObjectES
     * @param tosES List<TripleObjectES>. List of Objects to save
     * @return Map<String, Map<String, String>>.
     */
    @Override
    public Map<String, Map<String, String>> saveTripleObjectsES(List<TripleObjectES> tosES) {
        Map<String, Map<String, String>> result = new HashMap<>();
        Map<String,String> inserted = new HashMap<>();
        Map<String,String> fails = new HashMap<>();
        Map<String,Map<String,Map<String,Integer>>> insertedCounter = new HashMap<>();
        List<ElasticRegistry> insertedRegistry = new ArrayList<>();
        try {
            Iterable<TripleObjectES> res = repository.saveAll(tosES);
            for (TripleObjectES toES : res) {
                inserted.put(toES.getEntityId(),INSERTED);
                if (!insertedCounter.containsKey(toES.getTripleStore().getNode().getNodeName()))
                    insertedCounter.put(toES.getTripleStore().getNode().getNodeName(), new HashMap<>());
                if (!insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).containsKey(toES.getTripleStore().getName()))
                    insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).put(toES.getTripleStore().getName(), new HashMap<>());
                if (!insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).get(toES.getTripleStore().getName()).containsKey(toES.getClassName()))
                    insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).get(toES.getTripleStore().getName()).put(toES.getClassName(),0);
                insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).get(toES.getTripleStore().getName()).put(toES.getClassName(),insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).get(toES.getTripleStore().getName()).get(toES.getClassName())+1);
            }
            for (Map.Entry<String, Map<String, Map<String, Integer>>> nodeEntry : insertedCounter.entrySet()) {
                for (Map.Entry<String, Map<String, Integer>> tsEntry :  nodeEntry.getValue().entrySet()) {
                    for (Map.Entry<String, Integer> classEntry : tsEntry.getValue().entrySet()) {
                        insertedRegistry.add(new ElasticRegistry(applicationState.getApplication(),nodeEntry.getKey(),tsEntry.getKey(),classEntry.getKey(),classEntry.getValue()));
                    }
                }
            }

        } catch (ElasticsearchException e) {
            Map<String, String> failsResult = e.getFailedDocuments();
            for (TripleObjectES toES : tosES) {
                if (failsResult.containsKey(String.valueOf(toES.getId())))
                    fails.put(String.valueOf(toES.getId()),failsResult.get(String.valueOf(toES.getId())));
                else {
                    inserted.put(toES.getEntityId(), "failed");
                    if (!insertedCounter.containsKey(toES.getTripleStore().getNode().getNodeName()))
                        insertedCounter.put(toES.getTripleStore().getNode().getNodeName(), new HashMap<>());
                    if (!insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).containsKey(toES.getTripleStore().getName()))
                        insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).put(toES.getTripleStore().getName(), new HashMap<>());
                    if (!insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).get(toES.getTripleStore().getName()).containsKey(toES.getClassName()))
                        insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).get(toES.getTripleStore().getName()).put(toES.getClassName(),0);
                    insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).get(toES.getTripleStore().getName()).put(toES.getClassName(),insertedCounter.get(toES.getTripleStore().getNode().getNodeName()).get(toES.getTripleStore().getName()).get(toES.getClassName())+1);


                }
            }

            for (Map.Entry<String, Map<String, Map<String, Integer>>> nodeEntry : insertedCounter.entrySet()) {
                for (Map.Entry<String, Map<String, Integer>> tsEntry :  nodeEntry.getValue().entrySet()) {
                    for (Map.Entry<String, Integer> classEntry : tsEntry.getValue().entrySet()) {
                        insertedRegistry.add(new ElasticRegistry(applicationState.getApplication(),nodeEntry.getKey(),tsEntry.getKey(),classEntry.getKey(),classEntry.getValue()));
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        result.put("INSERTED",inserted);
        result.put("FAILED",fails);
        elasticRegistryRepository.saveAll(insertedRegistry);
        return result;
    }

    /**
     * Save in Elasticsearch
     * @see TripleObject
     * @param to TripleObject. Object to save
     * @return String
     */
    @Override
    public String saveTripleObject(TripleObject to) {
        try {
            repository.save(new TripleObjectES(to));
            return INSERTED;
        } catch (ElasticsearchException e) {
            Map<String, String> fails = e.getFailedDocuments();
            logger.error(e.getMessage());
            return fails.containsKey(to.getId())?fails.get(to.getId()):"fail";
        }catch (Exception e) {
            logger.error(e.getMessage());
            return "fail";
        }
    }

    /**
     * Save list in Elasticsearch
     * @see TripleObject
     * @param tosES List<TripleObject>. List of Objects to save
     * @return Map<String, Map<String, String>>.
     */
    @Override
    public Map<String, Map<String, String>> saveTripleObjects(List<TripleObject> tos) {
        List<TripleObjectES> tripleObjectES = new ArrayList<>();
        Map<String, Map<String, String>> result = new HashMap<>();
        Map<String,String> inserted = new HashMap<>();
        Map<String,String> fails = new HashMap<>();
        try {
            for (TripleObject to :tos) {
                tripleObjectES.add(new TripleObjectES(to));
            }
            Iterable<TripleObjectES> res = repository.saveAll(tripleObjectES);
            for (TripleObjectES toES : res) {
                inserted.put(toES.getEntityId(),INSERTED);
            }
        } catch (ElasticsearchException e) {
            Map<String, String> failsResult = e.getFailedDocuments();
            for (TripleObjectES toES : tripleObjectES) {
                if (failsResult.containsKey(String.valueOf(toES.getId())))
                    fails.put(toES.getEntityId(),failsResult.get(String.valueOf(toES.getId())));
                else
                    inserted.put(toES.getEntityId(),INSERTED);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        result.put("INSERTED",inserted);
        result.put("FAILED",fails);
        return result;
    }

    /**
     * Delete in Elasticsearch
     * @see TripleObjectES
     * @param toES TripleObjectES. Object to delete
     */
    @Override
    public void deleteTripleObjectES(TripleObjectES toES) {
        try {
            repository.delete(toES);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Delete in Elasticsearch
     * @see TripleObjectES
     * @param tosES List<TripleObjectES>. List of Objects to delete
     */
    @Override
    public void deleteTripleObjectsES(List<TripleObjectES> tosES) {
        try {
            repository.deleteAll(tosES);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Delete in Elasticsearch
     * @see TripleObject
     * @param to TripleObject. Object to delete
     */
    @Override
    public void deleteTripleObject(TripleObject to) {
        try {
            repository.delete( new TripleObjectES(to) );
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Delete in Elasticsearch
     * @see TripleObject
     * @param tosES List<TripleObject>. List of Objects to delete
     */
    @Override
    public void deleteTripleObjects(List<TripleObject> tos) {
        try {
            List<TripleObjectES> tripleObjectES = new ArrayList<>();
            for (TripleObject to :tos) {
                tripleObjectES.add(new TripleObjectES(to));
            }
            deleteTripleObjectsES(tripleObjectES);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Get all objects stored in Elasticsearch, filtered by Node, Triple Store and ClassName
     * @param node String. The Node to filter
     * @param tripleStore String. The Triple Store to filter
     * @param className String. The Class Name to filter
     * @return List<TripleObjectES>
     */
    @Override
    public List<TripleObjectES> getAllByNodeAndTripleStoreAndClassName(String node, String tripleStore, String className) {
        return customRepository.getAllTripleObjectsESByNodeAndTripleStoreAndClassName(node,tripleStore,className);
    }

    /**
     * Get all objects stored in Elasticsearch
     * @return List<TripleObjectES>
     */
    @Override
    public List<TripleObjectES> getAll() {
        return customRepository.getAllTripleObjectsES();
    }

    /**
     * Get Map with the entity id as key and the object in TripleObjectES model as value
     * @see TripleObjectES
     * @return Map<String,TripleObjectES>
     */
    @Override
    public Map<String,TripleObjectES> getAllMappedById() {
        Map<String,TripleObjectES> allTripleObjectES = new HashMap<>();
        try {
            for ( TripleObjectES toES: getAll()) {
                allTripleObjectES.put(toES.getEntityId(),toES);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return allTripleObjectES;
    }

    /**
     * Get TripleObjectES object stored in Elasticsearch, filtered by Id
     * @see TripleObjectES
     * @param id String. The entity Id
     * @return TripleObjectES
     */
    @Override
    public TripleObjectES getTripleObjectESById(String id) {
        Optional<TripleObjectES> response = repository.findById(id);
        return response.orElse(null);
    }

    /**
     * Get TripleObject object stored in Elasticsearch, filtered by Id
     * @see TripleObject
     * @param id String. The entity Id
     * @return TripleObject
     */
    @Override
    public TripleObject getTripleObjectById(String id) {
        TripleObjectES toES = getTripleObjectESById(id);
        if (toES == null)
            return null;
        else
            return new TripleObject(toES);
    }

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
    @Override
    public List<TripleObjectES> getTripleObjectsESByFilterAndAttributes(String indexName, String node,String tripleStore,String className, List<Pair<String, Object>> params) {
        List<TripleObjectES> tripleObjectsES = new ArrayList<>();
        List<Triplet<String,String,String>> filters = new ArrayList<>();
        if (node!=null)
            filters.add(new Triplet<>("tripleStore.node.nodeName",node,"TERM"));
        if (tripleStore!=null)
            filters.add(new Triplet<>("tripleStore.name",tripleStore,"TERM"));
        filters.add(new Triplet<>("className",className,"MATCH"));
        try {
            tripleObjectsES = customRepository.findByClassNameAndAttributesWithPartialMatch(indexName,filters,params);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return tripleObjectsES;
    }

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
    @Override
    public List<TripleObject> getTripleObjectsByFilterAndAttributes(String indexName, String node,String tripleStore,String className, List<Pair<String, Object>> params) {
        List<TripleObject> tripleObjects = new ArrayList<>();
        List<Triplet<String,String,String>> filters = new ArrayList<>();
        if (node!=null)
            filters.add(new Triplet<>("tripleStore.node.nodeName",node,"TERM"));
        if (tripleStore!=null)
            filters.add(new Triplet<>("tripleStore.name",tripleStore,"TERM"));
        filters.add(new Triplet<>("className",className,"MATCH"));
        try {

            List<TripleObjectES> responses = customRepository.findByClassNameAndAttributesWithPartialMatch(indexName,filters,params);
            for (TripleObjectES toES : responses) {
                tripleObjects.add(new TripleObject(toES));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return tripleObjects;
    }

    /**
     * Search similar objects at TripleObject pass in parameter
     * @see TripleObject
     * @see TripleObjectES
     * @param tripleObject TripleObject to search similarities
     * @return List<TripleObjectES> with the similarities found
     */
    @Override
    public List<TripleObjectES> getSimilarTripleObjectsES(TripleObject tripleObject) {
        return new ArrayList<>();
    }

    /**
     * Search similar objects at TripleObject pass in parameter
     * @see TripleObject
     * @param tripleObject TripleObject to search similarities
     * @return List<TripleObject> with the similarities found
     */
    @Override
    public List<TripleObject> getSimilarTripleObjects(TripleObject tripleObject) {
        return new ArrayList<>();
    }

    /**
     * Get all Objects filtered by Node and Triple Store, in SimplifiedTripleObject form
     * @param node String. The Node name to filter
     * @param tripleStore. The Triple Store name to filter
     * @return Map<String, Set<String>>
     */
    @Override
    public Map<String, Set<String>> getAllSimplifiedTripleObject(String node, String tripleStore) {
        return customRepository.getAllClassAndId(node, tripleStore);
    }
}
