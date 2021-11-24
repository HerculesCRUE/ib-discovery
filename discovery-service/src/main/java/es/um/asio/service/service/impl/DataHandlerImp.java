package es.um.asio.service.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import es.um.asio.service.config.DataProperties;
import es.um.asio.service.config.Datasources;
import es.um.asio.service.config.Hierarchies;
import es.um.asio.service.config.LodConfiguration;
import es.um.asio.service.model.BasicAction;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.appstate.DataState;
import es.um.asio.service.model.appstate.DataType;
import es.um.asio.service.model.appstate.State;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.relational.DiscoveryApplication;
import es.um.asio.service.repository.relational.CacheRegistryRepository;
import es.um.asio.service.repository.relational.DiscoveryApplicationRepository;
import es.um.asio.service.repository.relational.JobRegistryRepository;
import es.um.asio.service.repository.triplestore.TripleStoreHandler;
import es.um.asio.service.service.DataHandler;
import es.um.asio.service.service.SchemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * DataHandler implementation. For handle fetch data
 * @see TripleObject
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DataHandlerImp implements DataHandler {

    private final Logger logger = LoggerFactory.getLogger(DataHandlerImp.class);

    List<TripleStoreHandler> handlers;

    @Autowired
    CacheServiceImp cache;

    @Autowired
    RedisServiceImp redisService;

    @Autowired
    FirebaseStorageStrategy firebaseStorageStrategy;


    @Autowired
    Datasources dataSources;

    @Autowired
    DataProperties dataProperties;

    @Autowired
    ApplicationState applicationState;

    @Autowired
    ElasticsearchServiceImp elasticsearchService;

    @Autowired
    DiscoveryApplicationRepository appRepo;

    @Autowired
    JobRegistryRepository jobRegistryRepository;

    @Autowired
    CacheRegistryRepository cacheRegistryRepository;

    @Autowired
    SchemaService schemaService;

    @Autowired
    LodConfiguration lodConfiguration;

    @Autowired
    Hierarchies hierarchies;

    @Value("${app.domain}")
    String domain;

    @Value("${app.debug}")
    boolean debug;


    @PostConstruct
    private void initialize() {
        logger.info("datasources: {}", dataSources);
        logger.info("Initializing DataHandlerImp");
        handlers = new ArrayList<>();
        DiscoveryApplication discoveryApplication = applicationState.getApplication();
        if (discoveryApplication!=null) {
            discoveryApplication = appRepo.save(discoveryApplication);
            jobRegistryRepository.closeOtherJobRegistryByAppId(discoveryApplication.getId());
            applicationState.setApplication(discoveryApplication);
        }

    }

    /**
     * Call to handle populate data from REDIS cache, Triple Stores and save in ELASTICSEARCH
     * @see CompletableFuture
     * @return CompletableFuture, in async way. True is is completed else False
     * @throws ParseException Exception if error in parse data
     * @throws IOException Exception on In Out operations
     * @throws URISyntaxException Exception launch on URIs syntax exception
     */
    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Boolean> populateData() throws ParseException, IOException, URISyntaxException {
        logger.info("Populate data in DataHandlerImp");
        // 1º Load data in cache from redis if cache is empty or app is uninitialized
        if (!cache.isPopulatedCache() || applicationState.getAppState() == ApplicationState.AppState.UNINITIALIZED) {
            loadDataFromRedisToCache();
            cache.updateStats();
            // Update State of Application
            applicationState.setAppState(ApplicationState.AppState.INITIALIZED_WITH_CACHED_DATA);
            applicationState.setDataState(DataType.REDIS, State.CACHED_DATA);
            updateState(DataType.REDIS,cache.getTriplesMap());
            applicationState.setDataState(DataType.CACHE, State.CACHED_DATA);
            updateState(DataType.CACHE,cache.getTriplesMap());
        }
        // Update data from triple store (add deltas)
        if(!debug) // Quitar (poner a true)
            updateCachedData(); //  quit comment
        applicationState.setDataState(DataType.CACHE, State.UPLOAD_DATA);
        // Update elasticSearch
        logger.info("Writing Triple Objects in Elasticsearch");
        applicationState.setDataState(DataType.ELASTICSEARCH, State.CACHED_DATA);
        updateElasticData();
        applicationState.setDataState(DataType.ELASTICSEARCH, State.UPLOAD_DATA);
        logger.info("Generating inverse dependencies...");
        try {
            cache.generateInverseMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Completed load data");
        return CompletableFuture.completedFuture(true);
    }

    /**
     * Call when a new data is available
     * @param nodeName String. Node of the data to update
     * @param tripleStore String. Triple Store of the data to update
     * @param className String. Class of the data to update
     * @param entityURI String. localUri where the data is stored
     * @param basicAction String. One of this INSERT, UPDATE or DELETE
     * @return CompletableFuture
     * @throws ParseException Exception if error in parse data
     * @throws IOException Exception on In Out operations
     * @throws URISyntaxException Exception launch on URIs syntax exception
     */
    @Override
    public CompletableFuture<Boolean> actualizeData(String nodeName, String tripleStore, String className,String entityURL, BasicAction basicAction) throws ParseException, IOException, URISyntaxException {
        logger.info("Received notification on data change: Node: {}, TripleStore: {},  ClassName: {}, EntityURL: {], BasicAction: {}", nodeName,tripleStore,className,entityURL, basicAction.toString());
        Datasources.Node node = dataSources.getNodeByName(nodeName);
        if (applicationState.getAppState().getOrder() > 0 && node != null) {
            Datasources.Node.TripleStore ts = node.getTripleStoreByType(tripleStore);
            if (ts != null) {
                TripleStoreHandler handler = TripleStoreHandler.getHandler(domain,schemaService, dataSources,node,ts);
                if (handler != null) {
                    try {
                        handler.updateTripleObject(cache, nodeName, tripleStore, className, entityURL, basicAction);
                    } catch (Exception e) {

                    } finally {
                        cache.saveTriplesMapInCache(nodeName,tripleStore,className);
                        logger.info("Entity with URL: {} was be updated in cache",entityURL);
                        updateElasticData();
                        logger.info("Entity with URL: {} was be updated in Elasticsearch",entityURL);
                        return CompletableFuture.completedFuture(true);
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(false);
    }

    private void updateElasticData() {
        for (String node : cache.getAllNodes()) {
            for (String tripleStore : cache.getAllTripleStoreByNode(node)) {
                Map<String,Map<String,TripleObjectES>> toSaveES = new HashMap<>();
                // Cargo los datos actuales de ES
                Map<String, Set<String>> savedInES = elasticsearchService.getAllSimplifiedTripleObject(node,tripleStore);
                Set<TripleObject> tos = cache.getAllTripleObjects(node,tripleStore); // Todas las tripletas

                int index = 0;
                for (TripleObject to : tos) {
                    try {
                        if (!savedInES.containsKey(to.getClassName()) || !savedInES.get(to.getClassName()).contains(to.getId())) {
                            if (!toSaveES.containsKey(to.getClassName()))
                                toSaveES.put(to.getClassName(), new HashMap<>());
                            toSaveES.get(to.getClassName()).put(to.getId(), new TripleObjectES(to));

                            if (++index % 1000 == 0)
                                logger.info("Prepare for elasticsearch {}/{}",index,tos.size());

                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }

                for (Map.Entry<String, Map<String, TripleObjectES>> classEntry : toSaveES.entrySet()) {
                    List<TripleObjectES> toSave = new ArrayList<>();
                    for (Map.Entry<String, TripleObjectES> toEntry : classEntry.getValue().entrySet()) {
                        toSave.add(toEntry.getValue());
                    }
                    try {
                        Map<String, Map<String, String>> saveResult = elasticsearchService.saveTripleObjectsES(toSave);
                        logger.info("Saved in Elasticsearch className:{}, elements: {}, inserted: {} fails: {}",classEntry.getKey(), toSave.size(), saveResult.get("INSERTED").size(), saveResult.get("FAILED").size());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    }
                }
            }
        }

        updateState(DataType.ELASTICSEARCH,cache.getTriplesMap());

    }

    private void loadDataFromRedisToCache() {
        try {
            // Load data from cache
            cache.setTriplesMap(redisService.getTriplesMap());
            if (cache.getTriplesMap().size() == 0 && dataProperties.isReadCacheFromFirebase()) { // Get cache from file in firebase if is empty
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                String content = firebaseStorageStrategy.readFileFromStorage("jTriplesMap.json");
                Type type = new TypeToken<Map<String, Map<String, Map<String, Map<String, TripleObject>>>>>() {
                }.getType();
                Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap = gson.fromJson(content, type);
                redisService.setTriplesMap(triplesMap,true,true);
                logger.info("Load Data From cache complete");
                cache.setTriplesMap(triplesMap);
                logger.info("Set triples map complete");

            }
        } catch (Exception e) {
            logger.error("Fail on load data from firebase file: {}", e.getMessage());
        }
    }

    private void updateCachedData() throws ParseException, IOException, URISyntaxException {
        logger.info("Start update real data");
        boolean isChanged = false;
        for (Datasources.Node node : dataSources.getNodes()) {
            for (Datasources.Node.TripleStore ts : node.getTripleStores()) {
                TripleStoreHandler handler = TripleStoreHandler.getHandler(domain,schemaService, dataSources, node,ts);
                boolean dataChanged = handler.updateData(cache);
                isChanged = isChanged || dataChanged;
            }
        }
        if(isChanged) {
            cache.updateStats();
            cache.saveTriplesMapInCache();
            cache.saveEntityStatsInCache();
        }
        if (applicationState.getAppState().getOrder()< ApplicationState.AppState.INITIALIZED.getOrder()) {
            applicationState.setAppState(ApplicationState.AppState.INITIALIZED);
            applicationState.setDataState(DataType.REDIS, State.UPLOAD_DATA);
            updateState(DataType.REDIS, cache.getTriplesMap());
            applicationState.setDataState(DataType.CACHE, State.UPLOAD_DATA);
            updateState(DataType.CACHE, cache.getTriplesMap());
        }
    }

    private void updateState(DataType dataType, Map<String, Map<String, Map<String, Map<String, TripleObject>>>> data) {
        DataState dataState = applicationState.getDataState(dataType);
        for (Map.Entry<String, Map<String, Map<String, Map<String, TripleObject>>>> nEntry : data.entrySet()) {
            for (Map.Entry<String, Map<String, Map<String, TripleObject>>> tsEntry : nEntry.getValue().entrySet()) {
                for (Map.Entry<String, Map<String, TripleObject>> cnEntry : tsEntry.getValue().entrySet()) {
                    dataState.addDataStats(nEntry.getKey(),tsEntry.getKey(),cnEntry.getKey(),cnEntry.getValue().size());
                }
            }
        }
    }
}
