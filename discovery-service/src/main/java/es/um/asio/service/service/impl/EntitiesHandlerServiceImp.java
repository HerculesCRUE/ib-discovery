package es.um.asio.service.service.impl;

import es.um.asio.service.exceptions.CustomDiscoveryException;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.model.stats.StatsHandler;
import es.um.asio.service.service.EntitiesHandlerService;
import es.um.asio.service.util.Utils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class EntitiesHandlerServiceImp implements EntitiesHandlerService {

    @Autowired
    CacheServiceImp cache;

    @Autowired
    ElasticsearchServiceImp es;

    private final Logger logger = LoggerFactory.getLogger(EntitiesHandlerServiceImp.class);

    @Override
    public List<TripleObject> findEntitiesLinksByNodeAndTripleStoreAndClass(String node, String tripleStore, String className) {
        Map<String, TripleObject> tripleObjects = cache.getTripleObjects(node,tripleStore,className);
        if (tripleObjects.isEmpty())
            throw new CustomDiscoveryException(String.format("Not found for [ Node: %s, TripleStore: %s, ClassName: %s]",node,tripleStore, className));
        StatsHandler statsHandler = cache.getStatsHandler();
        Map<String, Float> stats = statsHandler.generateMoreRelevantAttributesMap(node,tripleStore,className);
        for (TripleObject to : tripleObjects.values()) {
            List<String> moreRelevant = getMoreRelevantAttributes(to,stats);
            List<Pair<String,Object>> params = new ArrayList<>();
            for (String relevantParam : moreRelevant) {
                Object value = to.getAttributeValue(relevantParam,to.getAttributes());
                if (value!=null) {
                    params.add(new Pair<>(relevantParam,value));
                }
            }
            List<TripleObject> matches = es.getTripleObjectsByFilterAndAttributes("triple-object",node,tripleStore,className,params)
                    .stream().filter(toInner-> !toInner.getId().equals(to.getId())).collect(Collectors.toList());
            logger.info(String.format("For [Node: %s, TripleStore: %s, ClassName: %s], founds %d similarities in Elasticsearch fro id: %s", node, tripleStore, className, matches.size(), to.getId()));
            if (matches.size()>0)
                System.out.println();
        }
        return null;
    }

    private List<String> getMoreRelevantAttributes(TripleObject to, Map<String, Float> stats) {
        List<String> moreRelevant = new ArrayList<>();
        Map<String, Float> statsAux = new TreeMap<>();
        float sumStats = 0f;
        for (Map.Entry<String, Float> stat : stats.entrySet()) {
            if (to.hasAttribute(stat.getKey(),to.getAttributes())) {
                sumStats += stat.getValue();
                statsAux.put(stat.getKey(),stat.getValue());
            }
        }

        statsAux = Utils.sortByValues(statsAux);

        float aggregateValue = 0f;
        for (Map.Entry<String, Float> stat : statsAux.entrySet()) {
            aggregateValue += (stat.getValue()/sumStats);
            moreRelevant.add(stat.getKey());
            if (aggregateValue>=0.75) {
                break;
            }
        }
        return  moreRelevant;
    }
}
