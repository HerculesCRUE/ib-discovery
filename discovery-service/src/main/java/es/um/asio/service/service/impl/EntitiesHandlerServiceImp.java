package es.um.asio.service.service.impl;

import es.um.asio.service.comparators.entities.EntityComparator;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.exceptions.CustomDiscoveryException;
import es.um.asio.service.model.SimilarityResult;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
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
    public Set<SimilarityResult> findEntitiesLinksByNodeAndTripleStoreAndClass(String node, String tripleStore, String className, boolean searchInOtherNodes, Date deltaDate) {
        Set<SimilarityResult> similarities = new HashSet<>();
        Map<String, TripleObject> tripleObjects = cache.getTripleObjects(node,tripleStore,className);
        if (deltaDate!=null) {
            tripleObjects = tripleObjects.entrySet()
                    .stream()
                    .filter(map -> map.getValue().getLastModification() >= deltaDate.getTime())
                    .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
        } else {
            if (tripleObjects.isEmpty())
                throw new CustomDiscoveryException(String.format("Not found for [ Node: %s, TripleStore: %s, ClassName: %s]", node, tripleStore, className));

        }

        StatsHandler statsHandler = cache.getStatsHandler();
        Map<String, Float> stats = statsHandler.generateMoreRelevantAttributesMap(node,tripleStore,className);
        int counter = 0;
        Set<String> foundsSimilarities = new HashSet<>();
        for (TripleObject to1 : tripleObjects.values()) {
            if (foundsSimilarities.contains(to1.getId())) {
                ++counter;
                continue;
            }
            // TODO : Hacer que pueda buscar similitudes o no en otro nodo
            List<TripleObject> matches = getSimilarEntitiesFromElasticsearch(to1, stats, searchInOtherNodes);
            logger.info(String.format("For [Node: %s, TripleStore: %s, ClassName: %s], founds %d similarities in Elasticsearch for id: %s", to1.getTripleStore().getNode().getNode(), to1.getTripleStore().getTripleStore(), to1.getClassName(), matches.size(), to1.getId()));
            if (matches.size()>1) {
                Map<String, List<EntitySimilarityObj>> similarity = calculateSimilarities(to1, stats, matches);
                logger.info(String.format("Completed (%d/%d) --> For [Node: %s, TripleStore: %s, ClassName: %s], founds %d automatic similarities and %d manuals similarities in Elasticsearch for id: %s\n", ++counter, tripleObjects.values().size(), to1.getTripleStore().getNode().getNode(), to1.getTripleStore().getTripleStore(), to1.getClassName(), similarity.get(AUTOMATIC_KEY).size(), similarity.get(MANUAL_KEY).size(), to1.getId()));
                if (similarity.get(MANUAL_KEY).size()>0 || similarity.get(AUTOMATIC_KEY).size()>0) {
                    SimilarityResult sr = new SimilarityResult(to1);
                    sr.addAutomatics(similarity.get(AUTOMATIC_KEY));
                    sr.addManuals(similarity.get(MANUAL_KEY));
                    similarities.add(sr);
                    if (sr.getAutomatic().size()>0) {
                        for (EntitySimilarityObj entitySimilarityObj: sr.getAutomatic()) {
                            foundsSimilarities.add(entitySimilarityObj.getTripleObject().getId());
                        }
                        System.out.println();
                    }
                }
            } else {
                logger.info(String.format("Completed (%d/%d) --> For [Node: %s, TripleStore: %s, ClassName: %s], founds %d automatic similarities and %d manuals similarities in Elasticsearch for id: %s\n", ++counter, tripleObjects.values().size(), to1.getTripleStore().getNode().getNode(), to1.getTripleStore().getTripleStore(), to1.getClassName(), 0, 0, to1.getId()));
            }
        }
        return similarities;
    }

    @Override
    public SimilarityResult findEntitiesLinksByNodeAndTripleStoreAndTripleObject(TripleObject to, boolean searchInOtherNodes) {
        Map<String, TripleObject> tripleObjects = cache.getTripleObjects(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to.getClassName());
        if (tripleObjects.isEmpty())
            throw new CustomDiscoveryException(String.format("Not found for [ Node: %s, TripleStore: %s, ClassName: %s]",to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(), to.getClassName()));
        StatsHandler statsHandler = cache.getStatsHandler();

        Map<String, Float> stats = statsHandler.generateMoreRelevantAttributesMap(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to.getClassName());
        List<TripleObject> matches = getSimilarEntitiesFromElasticsearch(to, stats, searchInOtherNodes);
        logger.info(String.format("For [Node: %s, TripleStore: %s, ClassName: %s], founds %d similarities in Elasticsearch for id: %s", to.getTripleStore().getNode().getNode(), to.getTripleStore().getTripleStore(), to.getClassName(), matches.size(), to.getId()));
        if (matches.size()>=1) {
            Map<String, List<EntitySimilarityObj>> similarity = calculateSimilarities(to, stats, matches);
            logger.info(String.format("Completed --> For [Node: %s, TripleStore: %s, ClassName: %s], founds %d automatic similarities and %d manuals similarities in Elasticsearch for id: %s ",  to.getTripleStore().getNode().getNode(), to.getTripleStore().getTripleStore(), to.getClassName(), similarity.get(AUTOMATIC_KEY).size(), similarity.get(MANUAL_KEY).size(), to.getId()));
            if (similarity.get(MANUAL_KEY).size()>0 || similarity.get(AUTOMATIC_KEY).size()>0) {
                SimilarityResult sr = new SimilarityResult(to);
                sr.addAutomatics(similarity.get(AUTOMATIC_KEY));
                sr.addManuals(similarity.get(MANUAL_KEY));
                return sr;
            }
        } else {
            logger.info(String.format("Completed --> For [Node: %s, TripleStore: %s, ClassName: %s], founds %d automatic similarities and %d manuals similarities in Elasticsearch for id: %s ", to.getTripleStore().getNode().getNode(), to.getTripleStore().getTripleStore(), to.getClassName(), 0, 0, to.getId()));
        }
        return null;
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
                similarities.get(MANUAL_KEY).add(eso);
            }
        }
        return  similarities;
    }


    private List<TripleObject> getSimilarEntitiesFromElasticsearch(TripleObject to,Map<String, Float> stats, boolean otherNodes) {
        List<String> moreRelevant = getMoreRelevantAttributes(to,stats);
        List<Pair<String,Object>> params = new ArrayList<>();
        for (String relevantParam : moreRelevant) {
            Object value = to.getAttributeValue(relevantParam,to.getAttributes());
            if (value!=null) {
                params.add(new Pair<>(relevantParam,value));
            }
        }
        if (to.getAttributes()!=null && to.getAttributes().size()>0) {
            List<TripleObjectES> matches = es.getTripleObjectsESByFilterAndAttributes("triple-object", !otherNodes?to.getTripleStore().getNode().getNode():null, !otherNodes?to.getTripleStore().getTripleStore():null, to.getClassName(), params)
                    .stream().filter(toInner -> !( // Quito el propio elemento de el resultado
                            toInner.getEntityId().equals(to.getId())
                            && toInner.getTripleStore().getTripleStore().equals(to.getTripleStore().getTripleStore())
                            && toInner.getTripleStore().getNode().getNode().equals(to.getTripleStore().getNode().getNode())
                            )
                    ).collect(Collectors.toList()
                    );
            if (matches.size()>=dataSourcesConfiguration.getThresholds().getElasticSearchMaxDesirableNumbersOfResults()) {
                float filterScore = matches.get(matches.size()-1).getScore()+ ((matches.get(0).getScore()-matches.get(matches.size()-1).getScore())*(float) dataSourcesConfiguration.getThresholds().getElasticSearchCutOffAccordPercentile());
                matches = matches.stream().filter(toEs -> toEs.getScore() >= filterScore).collect(Collectors.toList());
            }
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
            counter += 1;
            moreRelevant.add(stat.getKey());
            if (to.checkIsSimpleObject()) {
                if (aggregateValue >= dataSourcesConfiguration.getThresholds().getElasticSearchAttributesThresholdSimple()
                        && ((Double.valueOf(counter)/Double.valueOf(statsAux.size()))>=dataSourcesConfiguration.getThresholds().getElasticSearchAttributesNumberRatioSimple())
                        && counter>1
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
