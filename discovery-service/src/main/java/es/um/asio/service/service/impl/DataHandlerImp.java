package es.um.asio.service.service.impl;

import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.repository.triplestore.TripleStoreHandler;
import es.um.asio.service.service.DataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Service
public class DataHandlerImp implements DataHandler {

    List<TripleStoreHandler> handlers;

    @Autowired
    CacheServiceImp cacheService;

    @Autowired
    RedisServiceImp redisService;

    @Autowired
    DataSourcesConfiguration dataSourcesConfiguration;

    @PostConstruct
    private void initialize() throws Exception {
        handlers = new ArrayList<>();
        populateData(new Date());
    }

    @Override
    public void populateData(Date filterDate) {
        if (!dataSourcesConfiguration.isUseCachedData()) {
            for (DataSourcesConfiguration.Node node : dataSourcesConfiguration.getNodes()) {
                for (DataSourcesConfiguration.Node.TripleStore ts : node.getTripleStores()) {
                    TripleStoreHandler handler = TripleStoreHandler.getHandler(ts.getType(), node.getNodeName(), ts.getBaseURL(), ts.getUser(), ts.getPassword(), filterDate);
                    handlers.add(handler);
                    if (!cacheService.isPopulatedCache()) {
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
            cacheService.setTriplesMap(cacheService.loadTiplesMapFromCache());
            cacheService.setFiltered(cacheService.loadFilteredMapFromCache());
        }
        if (cacheService.getEntityStats().isEmpty()) {
            cacheService.generateEntityStats();
            redisService.setEntityStats(cacheService.getEntityStats());
        }
    }
}
