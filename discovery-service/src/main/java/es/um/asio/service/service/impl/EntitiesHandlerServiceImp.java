package es.um.asio.service.service.impl;

import es.um.asio.service.comparators.entities.EntityComparator;
import es.um.asio.service.comparators.entities.EntitySimilarityHandler;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.exceptions.CustomDiscoveryException;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.model.stats.StatsHandler;
import es.um.asio.service.service.EntitiesHandlerService;
import es.um.asio.service.util.Utils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntitiesHandlerServiceImp implements EntitiesHandlerService {

    @Autowired
    CacheServiceImp cache;

    @Autowired
    ElasticsearchServiceImp es;

    @Autowired
    DataSourcesConfiguration dataSourcesConfiguration;

    private final Logger logger = LoggerFactory.getLogger(EntitiesHandlerServiceImp.class);
    private final static String MANUAL_KEY="MANUAL";
    private final static String AUTOMATIC_KEY="AUTOMATIC";

    @Override
    public Map<String, List<EntitySimilarityObj>> findEntitiesLinksByNodeAndTripleStoreAndClass(String node, String tripleStore, String className) {
        Map<String, List<EntitySimilarityObj>> similarities = null;
        Map<String, TripleObject> tripleObjects = cache.getTripleObjects(node,tripleStore,className);
        if (tripleObjects.isEmpty())
            throw new CustomDiscoveryException(String.format("Not found for [ Node: %s, TripleStore: %s, ClassName: %s]",node,tripleStore, className));
        StatsHandler statsHandler = cache.getStatsHandler();
        Map<String, Float> stats = statsHandler.generateMoreRelevantAttributesMap(node,tripleStore,className);
        int counter = 0;
        for (TripleObject to1 : tripleObjects.values()) {
            List<TripleObject> matches = getSimilarEntitiesFromElasticsearch(to1, stats);
            logger.info(String.format("For [Node: %s, TripleStore: %s, ClassName: %s], founds %d similarities in Elasticsearch for id: %s", to1.getTripleStore().getNode().getNode(), to1.getTripleStore().getTripleStore(), to1.getClassName(), matches.size(), to1.getId()));
            if (matches.size()>1) {
                System.out.println();
                Map<String, List<EntitySimilarityObj>> similarity = calculateSimilarities(to1, stats, matches);
                logger.info(String.format("Completed (%d/%d) --> For [Node: %s, TripleStore: %s, ClassName: %s], founds %d automatic similarities and %d manuals similarities in Elasticsearch for id: %s", ++counter, tripleObjects.values().size(), to1.getTripleStore().getNode().getNode(), to1.getTripleStore().getTripleStore(), to1.getClassName(), similarity.get(AUTOMATIC_KEY).size(), similarity.get(MANUAL_KEY).size(), to1.getId()));
            } else {
                logger.info(String.format("Completed (%d/%d) --> For [Node: %s, TripleStore: %s, ClassName: %s], founds %d automatic similarities and %d manuals similarities in Elasticsearch for id: %s", ++counter, tripleObjects.values().size(), to1.getTripleStore().getNode().getNode(), to1.getTripleStore().getTripleStore(), to1.getClassName(), 0, 0, to1.getId()));
            }
        }
        return similarities;
    }

    private Map<String, List<EntitySimilarityObj>>  calculateSimilarities(TripleObject to, Map<String, Float> stats, List<TripleObject> matches) {
        Map<String, List<EntitySimilarityObj>> similarities = new HashMap<>();
        similarities.put(MANUAL_KEY,new ArrayList<>());
        similarities.put(AUTOMATIC_KEY,new ArrayList<>());
        EntitySimilarityObj maxSimilarityObj;
        for (TripleObject other: matches) {
            EntitySimilarityObj eso = EntityComparator.compare(to,other,stats);
            if (eso.similarity >= dataSourcesConfiguration.getThresholds().getAutomaticThreshold()) {
                similarities.get(AUTOMATIC_KEY).add(eso);
            } else if (eso.similarity >= dataSourcesConfiguration.getThresholds().getManualThreshold()) {
                similarities.get(AUTOMATIC_KEY).add(eso);
            }
        }
        return  similarities;
    }


    private List<TripleObject> getSimilarEntitiesFromElasticsearch(TripleObject to,Map<String, Float> stats) {
        List<String> moreRelevant = getMoreRelevantAttributes(to,stats);
        List<Pair<String,Object>> params = new ArrayList<>();
        for (String relevantParam : moreRelevant) {
            Object value = to.getAttributeValue(relevantParam,to.getAttributes());
            if (value!=null) {
                params.add(new Pair<>(relevantParam,value));
            }
        }
        if (to.getAttributes()!=null && to.getAttributes().size()>0) {
            List<TripleObjectES> matches = es.getTripleObjectsESByFilterAndAttributes("triple-object", to.getTripleStore().getNode().getNode(), to.getTripleStore().getTripleStore(), to.getClassName(), params)
                    .stream().filter(toInner -> !toInner.getId().equals(to.getId())).collect(Collectors.toList());
            return TripleObjectES.getTripleObjects(matches);
        }
        return new ArrayList<>();
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
        float sumStatsOther = stats.values().stream().reduce(0f, Float::sum);
        float sumStatsAux = statsAux.values().stream().reduce(0f, Float::sum);

        float aggregateValue = 0f;
        int counter = 0;
        for (Map.Entry<String, Float> stat : statsAux.entrySet()) {
            aggregateValue += (stat.getValue()/sumStats);
            moreRelevant.add(stat.getKey());
            if (to.checkIsSimpleObject()) {
                if (aggregateValue >= dataSourcesConfiguration.getThresholds().getElasticSearchAttributesThresholdSimple()
                        && ((Double.valueOf(++counter)/Double.valueOf(statsAux.size()))>=dataSourcesConfiguration.getThresholds().getElasticSearchAttributesNumberRatioSimple())
                ) {
                    break;
                }
            } else {
                if (aggregateValue >= dataSourcesConfiguration.getThresholds().getElasticSearchAttributesThresholdComplex()
                        && ((Double.valueOf(++counter)/Double.valueOf(statsAux.size()))>=dataSourcesConfiguration.getThresholds().getElasticSearchAttributesNumberRatioComplex())
                ) {
                    break;
                }
            }
        }
        return  moreRelevant;
    }

}
