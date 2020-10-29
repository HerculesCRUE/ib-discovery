package es.um.asio.service.service.impl;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.model.stats.StatsHandler;
import es.um.asio.service.repository.elasticsearch.TripleObjectESCustomRepository;
import es.um.asio.service.repository.elasticsearch.TripleObjectESRepository;
import es.um.asio.service.service.ElasticsearchService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ElasticsearchServiceImp implements ElasticsearchService {

    private final Logger logger = LoggerFactory.getLogger(ElasticsearchServiceImp.class);

    @Autowired
    TripleObjectESRepository repository;

    @Autowired
    TripleObjectESCustomRepository customRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    CacheServiceImp cache;

    @Override
    public TripleObjectES saveTripleObjectES(TripleObjectES toES) {
        try {
            return repository.save(toES);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public Iterable<TripleObjectES> saveTripleObjectsES(List<TripleObjectES> tosES) {
        try {
            return repository.saveAll(tosES);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public TripleObjectES saveTripleObject(TripleObject to) {
        try {
            return repository.save(new TripleObjectES(to));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public Iterable<TripleObjectES> saveTripleObjects(List<TripleObject> tos) {
        List<TripleObjectES> tripleObjectES = new ArrayList<>();
        try {
            for (TripleObject to :tos) {
                tripleObjectES.add(new TripleObjectES(to));
            }
            return saveTripleObjectsES(tripleObjectES);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return tripleObjectES;
        }
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
    public List<TripleObjectES> getAllByClassName(String className) {
        return customRepository.getAllTripleObjectsESByClassByClassName(className);
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
                allTripleObjectES.put(toES.getId(),toES);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return allTripleObjectES;
    }

    @Override
    public TripleObjectES getTripleObjectESById(String id) {
        Optional<TripleObjectES> response = repository.findById(id);
        response.orElse(null);
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
        List<Pair<String,String>> filters = new ArrayList<>();
        filters.add(new Pair<>("tripleStore.node.node",node));
        filters.add(new Pair<>("tripleStore.tripleStore",tripleStore));
        filters.add(new Pair<>("className",className));
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
        List<Pair<String,String>> filters = new ArrayList<>();
        filters.add(new Pair<>("tripleStore.node.node",node));
        filters.add(new Pair<>("tripleStore.tripleStore",tripleStore));
        filters.add(new Pair<>("className",className));
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
        List<TripleObjectES> tripleObjectsES = new ArrayList<>();
        StatsHandler stats = cache.getStatsHandler();
        return tripleObjectsES;
    }

    @Override
    public List<TripleObject> getSimilarTripleObjects(TripleObject tripleObject) {
        List<TripleObject> tripleObjects = new ArrayList<>();
        return tripleObjects;
    }
}
