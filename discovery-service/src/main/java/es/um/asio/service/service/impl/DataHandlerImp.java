package es.um.asio.service.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.appstate.DataType;
import es.um.asio.service.model.appstate.State;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.relational.CacheRegistry;
import es.um.asio.service.model.relational.DiscoveryApplication;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.model.stats.StatsHandler;
import es.um.asio.service.repository.relational.CacheRegistryRepository;
import es.um.asio.service.repository.relational.DiscoveryApplicationRepository;
import es.um.asio.service.repository.relational.JobRegistryRepository;
import es.um.asio.service.repository.triplestore.TripleStoreHandler;
import es.um.asio.service.service.DataHandler;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    DataSourcesConfiguration dataSourcesConfiguration;

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


    @PostConstruct
    private void initialize() throws Exception {
        logger.info("Initializing DataHandlerImp");
        handlers = new ArrayList<>();
        DiscoveryApplication discoveryApplication = applicationState.getApplication();
        if (discoveryApplication!=null) {
            discoveryApplication = appRepo.save(discoveryApplication);
            jobRegistryRepository.closeOtherJobRegistryByAppId(discoveryApplication.getId());
            applicationState.setApplication(discoveryApplication);
        }

/*        CacheRegistry cr = new CacheRegistry(discoveryApplication,"nodo","triplestore","clase");
        cacheRegistryRepository.save(cr);
        System.out.println("DOOOOOOONE");*/

    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Boolean> populateData() throws ParseException, IOException, URISyntaxException {
        logger.info("Populate data in DataHandlerImp");
        // 1º Load data in cache from redis if cache is empty or app is uninitialized
        if (!cache.isPopulatedCache() || applicationState.getAppState() == ApplicationState.AppState.UNINITIALIZED) {
            // loadStatsFromRedisToCache();
            loadDataFromRedisToCache();
            cache.updateStats();
            // Update State of Application
            applicationState.setAppState(ApplicationState.AppState.INITIALIZED_WITH_CACHED_DATA);
            applicationState.setDataState(DataType.REDIS, State.CACHED_DATA);
            applicationState.setDataState(DataType.CACHE, State.CACHED_DATA);
        }
        // Update data from triple store (add deltas)
        updateCachedData(); // TODO: quit comment
        // Update elasticSearch
        logger.info("Writing Triple Objects in Elasticsearch");
        updateElasticData();
        logger.info("Completed load data");
        return CompletableFuture.completedFuture(true);
    }

    private void updateElasticData() {
        // Load cache data
        Map<String, Set<String>> savedInES =elasticsearchService.getAllSimplifiedTripleObject();

        Map<String,Map<String,TripleObjectES>> toSaveES = new HashMap<>();
        Set<TripleObject> tos =  cache.getAllTripleObjects(); // Todas las tripletas

        for (TripleObject to : tos) {
            if (!savedInES.containsKey(to.getClassName()) || !savedInES.get(to.getClassName()).contains(to.getId()) ) {
                if (!toSaveES.containsKey(to.getClassName()))
                    toSaveES.put(to.getClassName(), new HashMap<>());
                toSaveES.get(to.getClassName()).put(to.getId(),new TripleObjectES(to));
            }
        }

        for (Map.Entry<String, Map<String, TripleObjectES>> classEntry : toSaveES.entrySet()) {
            List<TripleObjectES> toSave = new ArrayList<>();
            for (Map.Entry<String, TripleObjectES> toEntry : classEntry.getValue().entrySet()) {
                toSave.add(toEntry.getValue());
            }
            Map<String, Map<String, String>> saveResult = elasticsearchService.saveTripleObjectsES(toSave);
            logger.info("Saved in Elasticsearch className:" + classEntry.getKey() +", elements: "+ toSave.size() + ", inserted: "+saveResult.get("INSERTED").size()+" fails: "+saveResult.get("FAILED").size());
            System.out.println();
        }
/*        for (TripleObject to : toToSaveES) {
            cache.addTripleObjectES(to.getTripleStore().getNode().getNode(), to.getTripleStore().getTripleStore(), to);
        }
        cache.saveElasticSearchTriplesMapInCache();*/
        applicationState.setDataState(DataType.ELASTICSEARCH, State.UPLOAD_DATA);
        // List<TripleObjectES> tosESAfter = elasticsearchService.getAll();

    }

    private void loadDataFromRedisToCache() {
        try {
            // Load data from cache
            cache.setTriplesMap(redisService.getTriplesMap());
            if (cache.getTriplesMap().size() == 0) { // Get cache from file in firebase if is empty
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
            e.printStackTrace();
            logger.error("Fail on load data from firebase file: " + e.getMessage());
        }
    }

    private void loadStatsFromRedisToCache() {
        try {
            // Load data from cache
            cache.setStatsHandler(redisService.getEntityStats());
            if (cache.getStatsHandler().getStats().size() == 0) { // Get cache from file in firebase if is empty
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                String content = firebaseStorageStrategy.readFileFromStorage("jEntityStats.json");
                Type type = new TypeToken<Map<String ,Map<String, Map<String, EntityStats>>>>() {
                }.getType();
                Map<String ,Map<String, Map<String,EntityStats>>> map = gson.fromJson(content, type);
                StatsHandler sh = new StatsHandler();
                sh.setStats(map);
                redisService.setEntityStats(sh);
                cache.setStatsHandler(sh);

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Fail on load data from firebase file: " + e.getMessage());
        }
    }

    private void updateCachedData() throws ParseException, IOException, URISyntaxException {
        boolean isChanged = false;
        for (DataSourcesConfiguration.Node node : dataSourcesConfiguration.getNodes()) {
            for (DataSourcesConfiguration.Node.TripleStore ts : node.getTripleStores()) {
                TripleStoreHandler handler = TripleStoreHandler.getHandler(ts.getType(), node.getNodeName(), ts.getBaseURL(), ts.getUser(), ts.getPassword());
                isChanged = isChanged ||  handler.updateData(cache);
            }
        }
        if(isChanged) {
            cache.updateStats();
            cache.saveTriplesMapInCache();
            cache.saveEntityStatsInCache();
        }
        applicationState.setAppState(ApplicationState.AppState.INITIALIZED);
        applicationState.setDataState(DataType.REDIS, State.UPLOAD_DATA);
        applicationState.setDataState(DataType.CACHE, State.UPLOAD_DATA);
    }
}
