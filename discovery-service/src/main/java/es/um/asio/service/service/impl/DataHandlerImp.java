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
import es.um.asio.service.repository.triplestore.TripleStoreHandler;
import es.um.asio.service.service.DataHandler;
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

    @PostConstruct
    private void initialize() throws Exception {
        logger.info("Initializing DataHandlerImp");
        handlers = new ArrayList<>();
        System.out.println();
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Boolean> populateData() throws ParseException, IOException, URISyntaxException {
        logger.info("Populate data in DataHandlerImp");
        // 1º Load data in cache from redis if cache is empty or app is uninitialized
        if (!cache.isPopulatedCache() || applicationState.getAppState() == ApplicationState.AppState.UNINITIALIZED) {
            loadDataFromRedisToCache();
            // Update State of Application
            applicationState.setAppState(ApplicationState.AppState.INITIALIZED_WITH_CACHED_DATA);
            applicationState.setDataState(DataType.REDIS, State.CACHED_DATA);
            applicationState.setDataState(DataType.CACHE, State.CACHED_DATA);
        }
        // Update data from triple store (add deltas)
        updateCachedData();
        // Update elasticSearch
        updateElasticData();
        return CompletableFuture.completedFuture(true);
    }

    private void updateElasticData() {
        // Load cache data
        cache.setEsTriplesMap(redisService.getElasticSearchTriplesMap());
        Set<TripleObject> tosES =  cache.getEsTriplesMapAsSet(); // Tripletas cacheadas
        Set<TripleObject> tos =  cache.getAllTripleObjects(); // Todas las tripletas
        tos.removeAll(tosES); // Elimino de todas, las ya cacheadas
        List<TripleObject> toToSaveES = new ArrayList<>(tos); // Creo una lista con las pendientes
        elasticsearchService.saveTripleObjects(toToSaveES); // Guardo en elastic
        for (TripleObject to : toToSaveES) {
            cache.addTripleObjectES(to.getTripleStore().getNode().getNode(), to.getTripleStore().getTripleStore(), to);
        }
        cache.saveElasticSearchTriplesMapInCache();
        applicationState.setDataState(DataType.ELASTICSEARCH, State.UPLOAD_DATA);
        // List<TripleObjectES> tosESAfter = elasticsearchService.getAll();

    }

    private void loadDataFromRedisToCache() {
        try {
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
                redisService.setTriplesMap(triplesMap);
                cache.setTriplesMap(triplesMap);

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Fail on load data from firebase file: " + e.getMessage());
        }
    }

    private void updateCachedData() throws ParseException, IOException, URISyntaxException {
        for (DataSourcesConfiguration.Node node : dataSourcesConfiguration.getNodes()) {
            for (DataSourcesConfiguration.Node.TripleStore ts : node.getTripleStores()) {
                TripleStoreHandler handler = TripleStoreHandler.getHandler(ts.getType(), node.getNodeName(), ts.getBaseURL(), ts.getUser(), ts.getPassword());
                handler.updateData(cache);
            }
        }
        applicationState.setAppState(ApplicationState.AppState.INITIALIZED);
        applicationState.setDataState(DataType.REDIS, State.UPLOAD_DATA);
        applicationState.setDataState(DataType.CACHE, State.UPLOAD_DATA);
    }
}
