package es.um.asio.service.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.repository.triplestore.TripleStoreHandler;
import es.um.asio.service.service.DataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;
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

    @PostConstruct
    private void initialize() throws Exception {
        logger.info("Initializing DataHandlerImp");
        handlers = new ArrayList<>();
        System.out.println();
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Boolean> populateData() {
        logger.info("Populate data in DataHandlerImp");
        if (!cache.isPopulatedCache()) {
            setDataFromCache();
        }
        System.out.println();
        return CompletableFuture.completedFuture(true);

        /*if (!dataSourcesConfiguration.isUseCachedData()) {
            for (DataSourcesConfiguration.Node node : dataSourcesConfiguration.getNodes()) {
                for (DataSourcesConfiguration.Node.TripleStore ts : node.getTripleStores()) {
                    TripleStoreHandler handler = TripleStoreHandler.getHandler(ts.getType(), node.getNodeName(), ts.getBaseURL(), ts.getUser(), ts.getPassword(), filterDate);
                    handlers.add(handler);
                    if (!cache.isPopulatedCache()) {
                        try {
                            handler.populateData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            cache.setTriplesMap(cache.loadTiplesMapFromCache());
            cache.setFiltered(cache.loadFilteredMapFromCache());
        }
        if (cache.getEntityStats().isEmpty()) {
            cache.generateEntityStats();
            redisService.setEntityStats(cache.getEntityStats());
        }*/
    }

    private void setDataFromCache() {
        try {
            Map<String, Map<String, Map<String, Map<String, TripleObject>>>> ca = redisService.getTriplesMap();
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
            logger.error("Fail on load data from firebase file: " + e.getMessage());
        }
    }
}
