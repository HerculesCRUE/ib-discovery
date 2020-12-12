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
    private static final String MANUAL_KEY="MANUAL";
    private static final String AUTOMATIC_KEY="AUTOMATIC";

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
            List<TripleObject> matches = getSimilarEntitiesFromElasticsearch(to1, stats, searchInOtherNodes);
            logger.info("For [Node: {}, TripleStore: {}, ClassName: {}], founds {} similarities in Elasticsearch for id: {}", to1.getTripleStore().getNode().getNode(), to1.getTripleStore().getTripleStore(), to1.getClassName(), matches.size(), to1.getId());
            if (matches.size()>1) {
                Map<String, List<EntitySimilarityObj>> similarity = calculateSimilarities(to1, stats, matches);
                logger.info("Completed ({}/{}) --> For [Node: {}, TripleStore: {}, ClassName: {}], founds {} automatic similarities and {} manuals similarities in Elasticsearch for id: {}%n", ++counter, tripleObjects.values().size(), to1.getTripleStore().getNode().getNode(), to1.getTripleStore().getTripleStore(), to1.getClassName(), similarity.get(AUTOMATIC_KEY).size(), similarity.get(MANUAL_KEY).size(), to1.getId());
                if (!similarity.get(MANUAL_KEY).isEmpty() || !similarity.get(AUTOMATIC_KEY).isEmpty()) {
                    SimilarityResult sr = new SimilarityResult(to1);
                    sr.addAutomatics(similarity.get(AUTOMATIC_KEY));
                    sr.addManuals(similarity.get(MANUAL_KEY));
                    similarities.add(sr);
                    if (!sr.getAutomatic().isEmpty()) {
                        for (EntitySimilarityObj entitySimilarityObj: sr.getAutomatic()) {
                            foundsSimilarities.add(entitySimilarityObj.getTripleObject().getId());
                        }
                    }
                }
            } else {
                logger.info("Completed ({}/{}) --> For [Node: {}, TripleStore: {}, ClassName: {}], founds {} automatic similarities and {} manuals similarities in Elasticsearch for id: {}%n", ++counter, tripleObjects.values().size(), to1.getTripleStore().getNode().getNode(), to1.getTripleStore().getTripleStore(), to1.getClassName(), 0, 0, to1.getId());
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
        logger.info("For [Node: {}, TripleStore: {}, ClassName: {}], founds {} similarities in Elasticsearch for id: {}", to.getTripleStore().getNode().getNode(), to.getTripleStore().getTripleStore(), to.getClassName(), matches.size(), to.getId());
        if (!matches.isEmpty()) {
            Map<String, List<EntitySimilarityObj>> similarity = calculateSimilarities(to, stats, matches);
            logger.info("Completed --> For [Node: {}, TripleStore: {}, ClassName: {}], founds {} automatic similarities and {} manuals similarities in Elasticsearch for id: {} ",  to.getTripleStore().getNode().getNode(), to.getTripleStore().getTripleStore(), to.getClassName(), similarity.get(AUTOMATIC_KEY).size(), similarity.get(MANUAL_KEY).size(), to.getId());
            if (!similarity.get(MANUAL_KEY).isEmpty() || !similarity.get(AUTOMATIC_KEY).isEmpty()) {
                SimilarityResult sr = new SimilarityResult(to);
                sr.addAutomatics(similarity.get(AUTOMATIC_KEY));
                sr.addManuals(similarity.get(MANUAL_KEY));
                return sr;
            } else {
                SimilarityResult sr = new SimilarityResult(to);
                sr.addAutomatics(new ArrayList<>());
                sr.addManuals(new ArrayList<>());
                return sr;
            }
        } else {
            logger.info("Completed --> For [Node: {}, TripleStore: {}, ClassName: {}], founds {} automatic similarities and {} manuals similarities in Elasticsearch for id: {} ", to.getTripleStore().getNode().getNode(), to.getTripleStore().getTripleStore(), to.getClassName(), 0, 0, to.getId());
            SimilarityResult sr = new SimilarityResult(to);
            sr.addAutomatics(new ArrayList<>());
            sr.addManuals(new ArrayList<>());
            return sr;

        }
    }

    private Map<String, List<EntitySimilarityObj>>  calculateSimilarities(TripleObject to, Map<String, Float> stats, List<TripleObject> matches) {
        Map<String, List<EntitySimilarityObj>> similarities = new HashMap<>();
        similarities.put(MANUAL_KEY,new ArrayList<>());
        similarities.put(AUTOMATIC_KEY,new ArrayList<>());
        Map<String,Float> statsAux = new TreeMap<>();
        for (TripleObject other: matches) {
            if (stats.size()>1 && other.getAttributes().containsKey("id") && !to.getAttributes().containsKey("id")) {
                for (Map.Entry<String, Float> statsEntry: stats.entrySet()) {
                    if (!statsEntry.getKey().equalsIgnoreCase("id"))
                        statsAux.put(statsEntry.getKey(),statsEntry.getValue());
                }
            } else {
                statsAux = stats;
            }

            EntitySimilarityObj eso = EntityComparator.compare(to,other,statsAux);
            if (eso.getSimilarity() >= dataSourcesConfiguration.getThresholds().getAutomaticThreshold()) {
                similarities.get(AUTOMATIC_KEY).add(eso);
            } else if (eso.getSimilarity() >= dataSourcesConfiguration.getThresholds().getManualThreshold()) {
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
                statsAux.put(String.valueOf(stat.getKey()),stat.getValue());
            }
        }

        statsAux = Utils.sortByValues(statsAux);

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
