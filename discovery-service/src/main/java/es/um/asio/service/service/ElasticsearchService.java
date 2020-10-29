package es.um.asio.service.service;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import org.javatuples.Pair;

import java.util.List;
import java.util.Map;

public interface ElasticsearchService {


    public TripleObjectES saveTripleObjectES(TripleObjectES toES);

    public Iterable<TripleObjectES> saveTripleObjectsES(List<TripleObjectES> tosES);

    public TripleObjectES saveTripleObject(TripleObject to);

    public Iterable<TripleObjectES> saveTripleObjects(List<TripleObject> tosES);

    public void deleteTripleObjectES(TripleObjectES toES);

    public void deleteTripleObjectsES(List<TripleObjectES> tosES);

    public void deleteTripleObject(TripleObject to);

    public void deleteTripleObjects(List<TripleObject> tosES);

    public List<TripleObjectES> getAll();

    public List<TripleObjectES> getAllByClassName(String className);

    public Map<String,TripleObjectES> getAllMappedById();

    public TripleObjectES getTripleObjectESById(String id);

    public TripleObject getTripleObjectById(String id);

    public List<TripleObjectES> getTripleObjectsESByFilterAndAttributes(String indexName, String node, String tripleStore,String className, List<Pair<String,Object>> params);

    public List<TripleObject> getTripleObjectsByFilterAndAttributes(String indexName, String node, String tripleStore,String className, List<Pair<String,Object>> params);

    public List<TripleObjectES> getSimilarTripleObjectsES(TripleObject tripleObject);

    public List<TripleObject> getSimilarTripleObjects(TripleObject tripleObject);
}
