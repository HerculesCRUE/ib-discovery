package es.um.asio.service.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.comparators.entities.EntityComparator;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.config.LodConfiguration;
import es.um.asio.service.exceptions.CustomDiscoveryException;
import es.um.asio.service.model.SimilarityResult;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.rdf.TripleObjectLink;
import es.um.asio.service.model.stats.StatsHandler;
import es.um.asio.service.service.EntitiesHandlerService;
import es.um.asio.service.util.Utils;
import org.asynchttpclient.*;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
public class EntitiesHandlerServiceImp implements EntitiesHandlerService {

    @Autowired
    CacheServiceImp cache;

    @Autowired
    ElasticsearchServiceImp es;

    @Autowired
    DataSourcesConfiguration dataSourcesConfiguration;

    @Autowired
    LodConfiguration lodConfiguration;

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
            logger.info("For [Node: {}, TripleStore: {}, ClassName: {}], founds {} similarities in Elasticsearch for id: {}", to1.getTripleStore().getNode().getNodeName(), to1.getTripleStore().getName(), to1.getClassName(), matches.size(), to1.getId());
            if (matches.size()>1) {
                Map<String, List<EntitySimilarityObj>> similarity = calculateSimilarities(to1, stats, matches);
                logger.info("Completed ({}/{}) --> For [Node: {}, TripleStore: {}, ClassName: {}], founds {} automatic similarities and {} manuals similarities in Elasticsearch for id: {}%n", ++counter, tripleObjects.values().size(), to1.getTripleStore().getNode().getNodeName(), to1.getTripleStore().getName(), to1.getClassName(), similarity.get(AUTOMATIC_KEY).size(), similarity.get(MANUAL_KEY).size(), to1.getId());
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
                logger.info("Completed ({}/{}) --> For [Node: {}, TripleStore: {}, ClassName: {}], founds {} automatic similarities and {} manuals similarities in Elasticsearch for id: {}%n", ++counter, tripleObjects.values().size(), to1.getTripleStore().getNode().getNodeName(), to1.getTripleStore().getName(), to1.getClassName(), 0, 0, to1.getId());
            }
        }
        return similarities;
    }

    @Override
    public SimilarityResult findEntitiesLinksByNodeAndTripleStoreAndTripleObject(TripleObject to, boolean searchInOtherNodes) {
        Map<String, TripleObject> tripleObjects = cache.getTripleObjects(to.getTripleStore().getNode().getNodeName(),to.getTripleStore().getName(),to.getClassName());
        if (tripleObjects.isEmpty())
            throw new CustomDiscoveryException(String.format("Not found for [ Node: %s, TripleStore: %s, ClassName: %s]",to.getTripleStore().getNode().getNodeName(),to.getTripleStore().getName(), to.getClassName()));
        StatsHandler statsHandler = cache.getStatsHandler();

        Map<String, Float> stats = statsHandler.generateMoreRelevantAttributesMap(to.getTripleStore().getNode().getNodeName(),to.getTripleStore().getName(),to.getClassName());
        List<TripleObject> matches = getSimilarEntitiesFromElasticsearch(to, stats, searchInOtherNodes);
        logger.info("For [Node: {}, TripleStore: {}, ClassName: {}], founds {} similarities in Elasticsearch for id: {}", to.getTripleStore().getNode().getNodeName(), to.getTripleStore().getName(), to.getClassName(), matches.size(), to.getId());
        if (!matches.isEmpty()) {
            Map<String, List<EntitySimilarityObj>> similarity = calculateSimilarities(to, stats, matches);
            logger.info("Completed --> For [Node: {}, TripleStore: {}, ClassName: {}], founds {} automatic similarities and {} manuals similarities in Elasticsearch for id: {} ",  to.getTripleStore().getNode().getNodeName(), to.getTripleStore().getName(), to.getClassName(), similarity.get(AUTOMATIC_KEY).size(), similarity.get(MANUAL_KEY).size(), to.getId());
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
            logger.info("Completed --> For [Node: {}, TripleStore: {}, ClassName: {}], founds {} automatic similarities and {} manuals similarities in Elasticsearch for id: {} ", to.getTripleStore().getNode().getNodeName(), to.getTripleStore().getName(), to.getClassName(), 0, 0, to.getId());
            SimilarityResult sr = new SimilarityResult(to);
            sr.addAutomatics(new ArrayList<>());
            sr.addManuals(new ArrayList<>());
            return sr;

        }
    }

    @Override
    public Set<SimilarityResult> findEntitiesLinksByNodeAndTripleStoreAndClassInLOD(String node, String tripleStore, String className, Date deltaDate) {
        Set<SimilarityResult> similarities = new HashSet<>();
        Map<String, TripleObject> tripleObjects = cache.getTripleObjects(node,tripleStore,className);
        if (deltaDate!=null) { // Filtrar por delta
            tripleObjects = tripleObjects.entrySet()
                    .stream()
                    .filter(map -> map.getValue().getLastModification() >= deltaDate.getTime())
                    .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
        } else {
            if (tripleObjects.isEmpty())
                throw new CustomDiscoveryException(String.format("Not found for [ Node: %s, TripleStore: %s, ClassName: %s]", node, tripleStore, className));

        }
        Map<TripleObject,List<TripleObjectLink>> links = handleRequestLodSearch(tripleObjects);
        for (Map.Entry<TripleObject, List<TripleObjectLink>> linkEntry : links.entrySet()) { // TripleObject --> List<TripleObjectLink>
            Map<String, List<EntitySimilarityObj>> similarity = new HashMap<>();
            similarity.put(MANUAL_KEY,new ArrayList<>());
            similarity.put(AUTOMATIC_KEY,new ArrayList<>());
            for (TripleObjectLink link :linkEntry.getValue()) { // List TripleObjectLink
                TripleObject to = new TripleObject(link);
                LinkedTreeMap<String, Object> attrs = to.getAttributesChangedByMapper(link.getMapper(), to.getAttributes());
                to.setAttributes(attrs);
                EntitySimilarityObj eso = linkEntry.getKey().compareLazzy(cache,to);
                eso.setTripleObject(to);
                eso.setDataSource(link.getRemoteName());
                if (eso.getSimilarity()>=dataSourcesConfiguration.getThresholds().getAutomaticThreshold()) {
                    similarity.get(AUTOMATIC_KEY).add(eso);
                } else if (eso.getSimilarity()>=dataSourcesConfiguration.getThresholds().getManualThreshold()) {
                    similarity.get(MANUAL_KEY).add(eso);
                }
            }
            if (!similarity.get(MANUAL_KEY).isEmpty() || !similarity.get(AUTOMATIC_KEY).isEmpty()) {
                SimilarityResult sr = new SimilarityResult(linkEntry.getKey());
                sr.addAutomatics(similarity.get(AUTOMATIC_KEY));
                sr.addManuals(similarity.get(MANUAL_KEY));
                similarities.add(sr);
            } else {
                SimilarityResult sr = new SimilarityResult(linkEntry.getKey());
                sr.addAutomatics(new ArrayList<>());
                sr.addManuals(new ArrayList<>());
                similarities.add(sr);
            }
        }
        return similarities;
    }
    /*
            if (!matches.isEmpty()) {
            Map<String, List<EntitySimilarityObj>> similarity = calculateSimilarities(to, stats, matches);
            logger.info("Completed --> For [Node: {}, TripleStore: {}, ClassName: {}], founds {} automatic similarities and {} manuals similarities in Elasticsearch for id: {} ",  to.getTripleStore().getNode().getNodeName(), to.getTripleStore().getName(), to.getClassName(), similarity.get(AUTOMATIC_KEY).size(), similarity.get(MANUAL_KEY).size(), to.getId());
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
            logger.info("Completed --> For [Node: {}, TripleStore: {}, ClassName: {}], founds {} automatic similarities and {} manuals similarities in Elasticsearch for id: {} ", to.getTripleStore().getNode().getNodeName(), to.getTripleStore().getName(), to.getClassName(), 0, 0, to.getId());
            SimilarityResult sr = new SimilarityResult(to);
            sr.addAutomatics(new ArrayList<>());
            sr.addManuals(new ArrayList<>());
            return sr;

        }
     */

    private Map<TripleObject,List<TripleObjectLink>> handleRequestLodSearch(Map<String, TripleObject> tripleObjects) {
        Map<TripleObject,List<TripleObjectLink>> links = new HashMap<>();

        Map<TripleObject,CompletableFuture<Response>> futures = new HashMap<>();
        List<TripleObject> triplesSplit = new ArrayList<>();
        for (Map.Entry<String, TripleObject> toEntry : tripleObjects.entrySet()) {
            if (triplesSplit.size()< 10) {
                triplesSplit.add(toEntry.getValue());
            } else {
                for (TripleObject to : triplesSplit) {
                    futures.put(to,doRequestInLod(to));
                }

                for (Map.Entry<TripleObject, CompletableFuture<Response>> future : futures.entrySet()) {
                    Response response = future.getValue().join();
                    if (response.getStatusCode() == 200) {
                        String body = response.getResponseBody();
                        List<TripleObjectLink> toLinks = new ArrayList<>();
                        JsonArray jTripleObjectLink = new JsonParser().parse(response.getResponseBody()).getAsJsonArray();
                        for (JsonElement jeTripleObjectLink : jTripleObjectLink) {
                            TripleObjectLink tol = new TripleObjectLink(jeTripleObjectLink.getAsJsonObject());
                            tol.setOrigin(future.getKey());
                            toLinks.add(tol);
                        }
                        if (toLinks.size()>0)  // Si encontre algun link lo añado
                            links.put(future.getKey(),toLinks);
                    }
                    response.getResponseBody();
                }
                triplesSplit = new ArrayList<>();

            }
        }
        return links;
    }

    private CompletableFuture<Response> doRequestInLod(TripleObject tripleObject) {
        AsyncHttpClient asyncHttpClient = Dsl.asyncHttpClient();
        DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config();
        String queryParams = "?dataSets=SCOPUS";//+ String.join(","+lodConfiguration.getLodDatasets());
        BoundRequestBuilder postRequest = asyncHttpClient.preparePost(lodConfiguration.buildCompleteURI()+queryParams)
                .addHeader("Content-Type","application/json")
                .setBody(tripleObject.toJson().toString());
        ListenableFuture<Response> responseFuture = postRequest.execute();
        return responseFuture.toCompletableFuture();
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
            if (value!=null && !((List<Object>) value).isEmpty()) {
                params.add(new Pair<>(relevantParam,value));
            }
        }
        if (to.getAttributes()!=null && to.getAttributes().size()>0) {
            List<TripleObjectES> matches = es.getTripleObjectsESByFilterAndAttributes("triple-object", !otherNodes?to.getTripleStore().getNode().getNodeName():null, !otherNodes?to.getTripleStore().getName():null, to.getClassName(), params)
                    .stream().filter(toInner -> !( // Quito el propio elemento de el resultado
                            toInner.getEntityId().equals(to.getId())
                            && toInner.getTripleStore().getName().equals(to.getTripleStore().getName())
                            && toInner.getTripleStore().getNode().getNodeName().equals(to.getTripleStore().getNode().getNodeName())
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
