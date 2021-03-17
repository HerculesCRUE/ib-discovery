package es.um.asio.service.repository.triplestore;

import es.um.asio.service.config.Datasources;
import es.um.asio.service.model.BasicAction;

import es.um.asio.service.service.SchemaService;
import es.um.asio.service.service.impl.CacheServiceImp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * Abstract class to Handle request for Trellis LDP Server
 * @see SchemaService
 * @see Datasources
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public abstract class TripleStoreHandler {

    /**
     * Handle request for Update data in Trellis
     * @param cacheService. CacheService. Contains all data to update
     * @return boolean
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    public abstract boolean updateData(CacheServiceImp cacheService) throws IOException, URISyntaxException, ParseException;

    /**
     * Update a specific data in trellis
     * @param cacheService CacheService. Contains all data to update
     * @param node String. Name of node
     * @param tripleStore String. Name of triple Store
     * @param className String. The class name
     * @param localURI String. The local URI
     * @param basicAction BasicAction. The basic Action
     * @return boolean
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    public abstract boolean updateTripleObject(CacheServiceImp cacheService,String node, String tripleStore, String className,String localURI, BasicAction basicAction) throws IOException, URISyntaxException, ParseException;

    /**
     * Select Handler for get data
     * @param schemaService SchemaService
     * @param dataSources DataSourcesConfiguration
     * @param node String. Name of node
     * @param ts TripleStore
     * @return
     */
    public static TripleStoreHandler getHandler(SchemaService schemaService, Datasources dataSources, Datasources.Node node, Datasources.Node.TripleStore ts) {
        if (ts.getType().trim().equalsIgnoreCase("trellis"))
            return new TrellisHandler(node.getNodeName(),ts.getBaseURL(),ts.getUser(),ts.getPassword());
        else if (ts.getType().trim().equalsIgnoreCase("fuseki"))
            return new SparqlProxyHandler(schemaService, dataSources,node,ts);
        else
            throw new IllegalArgumentException("Not exist correct handler for type: "+ ts.getType());
    }
}
