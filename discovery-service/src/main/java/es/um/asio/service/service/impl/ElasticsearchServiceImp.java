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

@Service
public class ElasticsearchServiceImp implements ElasticsearchService {

    private final Logger logger = LoggerFactory.getLogger(ElasticsearchServiceImp.class);

    private static final String INSERTED = "inserted";

    @Autowired
    TripleObjectESRepository repository;

    @Autowired
    TripleObjectESCustomRepository customRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    CacheServiceImp cache;

    @Autowired
    ElasticRegistryRepository elasticRegistryRepository;

    @Autowired
    ApplicationState applicationState;

    private static final String FAIL = "fail";

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

    @Override
    public void deleteTripleObjectES(TripleObjectES toES) {
        try {
            repository.delete(toES);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void deleteTripleObjectsES(List<TripleObjectES> tosES) {
        try {
            repository.deleteAll(tosES);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void deleteTripleObject(TripleObject to) {
        try {
            repository.delete( new TripleObjectES(to) );
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

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

    @Override
    public List<TripleObjectES> getAllByNodeAndTripleStoreAndClassName(String node, String tripleStore, String className) {
        return customRepository.getAllTripleObjectsESByNodeAndTripleStoreAndClassName(node,tripleStore,className);
    }

    @Override
    public List<TripleObjectES> getAll() {
        return customRepository.getAllTripleObjectsES();
    }

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

    @Override
    public TripleObjectES getTripleObjectESById(String id) {
        Optional<TripleObjectES> response = repository.findById(id);
        return response.orElse(null);
    }

    @Override
    public TripleObject getTripleObjectById(String id) {
        TripleObjectES toES = getTripleObjectESById(id);
        if (toES == null)
            return null;
        else
            return new TripleObject(toES);
    }

    @Override
    public List<TripleObjectES> getTripleObjectsESByFilterAndAttributes(String indexName, String node,String tripleStore,String className, List<Pair<String, Object>> params) {
        List<TripleObjectES> tripleObjectsES = new ArrayList<>();
        List<Triplet<String,String,String>> filters = new ArrayList<>();
        if (node!=null)
            filters.add(new Triplet<>("tripleStore.node.node",node,"TERM"));
        if (tripleStore!=null)
            filters.add(new Triplet<>("tripleStore.tripleStore",tripleStore,"TERM"));
        filters.add(new Triplet<>("className",className,"MATCH"));
        try {
            tripleObjectsES = customRepository.findByClassNameAndAttributesWithPartialMatch(indexName,filters,params);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return tripleObjectsES;
    }

    @Override
    public List<TripleObject> getTripleObjectsByFilterAndAttributes(String indexName, String node,String tripleStore,String className, List<Pair<String, Object>> params) {
        List<TripleObject> tripleObjects = new ArrayList<>();
        List<Triplet<String,String,String>> filters = new ArrayList<>();
        if (node!=null)
            filters.add(new Triplet<>("tripleStore.node.node",node,"TERM"));
        if (tripleStore!=null)
            filters.add(new Triplet<>("tripleStore.tripleStore",tripleStore,"TERM"));
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

    @Override
    public List<TripleObjectES> getSimilarTripleObjectsES(TripleObject tripleObject) {
        return new ArrayList<>();
    }

    @Override
    public List<TripleObject> getSimilarTripleObjects(TripleObject tripleObject) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Set<String>> getAllSimplifiedTripleObject(String node, String tripleStore) {
        return customRepository.getAllClassAndId(node, tripleStore);
    }
}
