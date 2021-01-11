package es.um.asio.service.repository.triplestore;

import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.model.BasicAction;

import es.um.asio.service.service.SchemaService;
import es.um.asio.service.service.impl.CacheServiceImp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

public abstract class TripleStoreHandler {

    public abstract boolean updateData(CacheServiceImp cacheService) throws IOException, URISyntaxException, ParseException;

    public abstract boolean updateTripleObject(CacheServiceImp cacheService,String node, String tripleStore, String className,String localURI, BasicAction basicAction) throws IOException, URISyntaxException, ParseException;

    public static TripleStoreHandler getHandler(SchemaService schemaService,DataSourcesConfiguration dataSourcesConfiguration, DataSourcesConfiguration.Node node, DataSourcesConfiguration.Node.TripleStore ts) {
        if (ts.getType().trim().equalsIgnoreCase("trellis"))
            return new TrellisHandler(node.getNodeName(),ts.getBaseURL(),ts.getUser(),ts.getPassword());
        else if (ts.getType().trim().equalsIgnoreCase("sparql"))
            return new SparqlProxyHandler(schemaService, dataSourcesConfiguration,node,ts);
        else
            throw new IllegalArgumentException("Not exist correct handler for type: "+ ts.getType());
    }
}
