package es.um.asio.service.repository.triplestore;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import es.um.asio.service.config.Datasources;
import es.um.asio.service.model.BasicAction;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import es.um.asio.service.model.URIComponent;
import es.um.asio.service.service.SchemaService;
import es.um.asio.service.service.impl.CacheServiceImp;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

/**
 * Handle request for Trellis LDP Server
 * @see SchemaService
 * @see Datasources
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public class SparqlProxyHandler extends TripleStoreHandler {

    private final Logger logger = LoggerFactory.getLogger(SparqlProxyHandler.class);
    private String domain;
    private String nodeName;
    private String baseURL;
    private String serviceName;
    private String user;
    private String password;
    private TripleStore tripleStore;

    private static final String TRIPLE_STORE = "fuseki";

    SchemaService schemaService;

    Datasources dataSources;

    /**
     * Constructor
     * @see SchemaService
     * @see Datasources
     * @see Datasources.Node
     * @see Datasources.Node.TripleStore
     * @param schemaService SchemaService. The service for handle URIS factory Schema
     * @param dataSources DataSourcesConfiguration. The data sources configuration
     * @param node Node. The node
     * @param ts TripleStore. The triple Store
     */
    public SparqlProxyHandler(String domain,SchemaService schemaService, Datasources dataSources, Datasources.Node node, Datasources.Node.TripleStore ts) {
        this.domain = domain;
        this.nodeName = node.getNodeName();
        this.baseURL = ts.getBaseURL();
        if (this.baseURL.charAt(baseURL.length()-1) == '/')
            this.baseURL = this.baseURL.substring(0,baseURL.length()-1);
        this.serviceName = node.getServiceName();
        this.user = ts.getUser();
        this.password = ts.getPassword();
        this.tripleStore = new TripleStore(ts.getName(),node.getNodeName(),this.baseURL,this.user,this.password);
        this.schemaService = schemaService;
        this.dataSources = dataSources;
    }

    /**
     * Handle request for Update data in Trellis
     * @param cacheService. CacheService. Contains all data to update
     * @return boolean
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    @Override
    public boolean updateData(CacheServiceImp cacheService) throws IOException, URISyntaxException, ParseException {
        logger.info("Start Update data from SPARQL with baseURL: {}", this.baseURL);
        Set<TripleObject> triplesMapCached = cacheService.getAllTripleObjects(this.nodeName,this.tripleStore.getName());
        int instancesCounter = 0;
        int changes = 0;
        Map<String,String> headers = new HashMap<>();
        //headers.put("accept","application/json");
        Map<String,String> queryParams = new HashMap<>();
        queryParams.put("domain",domain);
        queryParams.put("node",nodeName);
        queryParams.put("service",serviceName);
        queryParams.put("tripleStore",tripleStore.getName());
        // Get all class
        JsonElement jeResponse = doRequest(new URL(this.baseURL + "/data-fetcher/objects"), Connection.Method.GET, headers,null,queryParams); // Request Classes
        logger.info("Response Objects: {}",jeResponse.toString());
        if (jeResponse!=null && jeResponse.isJsonArray()) { // Si la respuesta es correcta
            int classesCounter = 0;
            JsonArray jResponse = jeResponse.getAsJsonArray();
            for (JsonElement jClass : jResponse) {
                ++classesCounter;
                URIComponent uriComponent = schemaService.getURIComponentFromCanonicalLocalURI(jClass.getAsString());
                String className = uriComponent.getConcept();
                logger.info("Searching updated instances by {} class in Node {}",className,nodeName);
                int classChanges = 0;
                int classInstances = 0;
                if (className!=null) {
                    queryParams.put("className",className);
                    try {
                        JsonElement jeInstancesResponse = doRequest(new URL(this.baseURL + "/data-fetcher/instances"), Connection.Method.GET, headers, null, queryParams); // Request Instances by class
                        if (jeInstancesResponse != null && jeInstancesResponse.isJsonArray()) {
                            int instancesInClass = 0;
                            JsonArray jInstancesResponse = jeInstancesResponse.getAsJsonArray();
                            for (JsonElement jInstance : jInstancesResponse) {
                                ++classInstances;
                                TripleObject to = new TripleObject(jInstance.getAsJsonObject());
                                TripleObject toStored = cacheService.getTripleObject(nodeName, this.tripleStore.getName(), className, to.getId());
                                if (!to.equals(toStored)) { // Si hay cambios lo actualizo
                                    changes++;
                                    classChanges++;
                                    cacheService.addTripleObject(nodeName, tripleStore.getName(), to);
                                    String nText = ((toStored == null) ? "(New) " : " ");
                                    logger.info("		Processing Node {} Instances: {} ({}/{}): {}	,class ({}/{}):{}	,id: {}	,data:{}", nText, ++instancesCounter, ++instancesInClass, jInstancesResponse.size(), nodeName, classesCounter, jResponse.size(), className, to.getId(), to);
                                } else { // Si no hay cambios lo elimino
                                    triplesMapCached.remove(toStored);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
                logger.info("Updating cache instances by {} class in Node {}: Changes ({}/{})",className,nodeName,classChanges,classInstances);
                if (classChanges>0) // Si hay cambios en la clase, actualizo la cache
                    cacheService.saveTriplesMapInCache(nodeName,tripleStore.getName(),className);
            }

            if (changes > 0 || !triplesMapCached.isEmpty()) {
                for (TripleObject to : triplesMapCached) {// Si quedan elementos cacheados, que no aparecen en la consulta
                    cacheService.removeTripleObject(this.nodeName, this.tripleStore.getName(), to); // los eliminamos
                }
                logger.info("Found {} changes in "+ tripleStore.getName() +" and {} instances will be deleted. Saving in Cache and Redis. The cache is updated", changes,triplesMapCached.size());
            } else {
                logger.info("No changes Found in "+ tripleStore.getName() +". The cache is updated");
                return false;
            }
            return true;
        }
        return false;
    }

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
    @Override
    public boolean updateTripleObject(CacheServiceImp cacheService, String node, String tripleStore, String className, String localURI, BasicAction basicAction) throws IOException, URISyntaxException, ParseException {
        String [] uriChunks = (localURI.charAt(localURI.length()-1)=='/')?localURI.substring(0,localURI.length()-1).split("/"):localURI.split("/");
        String instanceId = uriChunks[uriChunks.length-1];
        Map<String,String> headers = new HashMap<>();
        if (basicAction.equals(BasicAction.DELETE)) { // Si se elimino, lo eliminamos de la cache
            TripleObject to = cacheService.getTripleObject(nodeName,this.tripleStore.getName(),className,instanceId);
            cacheService.removeTripleObject(node, tripleStore, to);
        } else { // en otro caso, se actualiza la cache
            Map<String,String> qParams = ImmutableMap.of("localURI",localURI);
            JsonElement jeResponse = doRequest(new URL(dataSources.getUrisFactoryHost() + "uri-factory/local"), Connection.Method.GET,headers,null,qParams);
            if (jeResponse!=null && jeResponse.isJsonArray() && jeResponse.getAsJsonArray().size()>0) {
                String canonicalLocalURI = jeResponse.getAsJsonArray().get(0).getAsJsonObject().get("fullURI").getAsString();
                if (canonicalLocalURI!=null) {
                    Map<String,String> qParamsInstance = ImmutableMap.of(
                            "className",className,
                            "node",node,
                            "service", "sparql-proxy",
                            "tripleStore", tripleStore,
                            "uri",canonicalLocalURI);
                    qParamsInstance.put("domain",domain);
                    JsonElement jeInstance = doRequest(new URL(this.baseURL + "/data-fetcher/instance/find"),Connection.Method.GET,headers,null,qParamsInstance);
                    TripleObject to = new TripleObject(jeInstance.getAsJsonObject());
                    cacheService.addTripleObject(nodeName,this.tripleStore.getName(), to);
                    cacheService.saveTriplesMapInCache(nodeName,this.tripleStore.getName(),className);
                    return true;
                }
            }
        }
        return false;
    }

    private JsonElement doRequest(URL url, Connection.Method method, Map<String,String> headers, Map<String,String> params, Map<String,String> queryParams) throws IOException {
        if (queryParams!=null) {
         url = buildQueryParams(url,queryParams);
        }
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(1800000);
        con.setRequestMethod(method.toString());
        if (headers!=null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                con.setRequestProperty(headerEntry.getKey(),headerEntry.getValue());
            }
        }
        if (params!=null) {
            for (Map.Entry<String, String> paramEntry : params.entrySet()) {
                con.setRequestProperty(paramEntry.getKey(),paramEntry.getValue());
            }
        }
        con.setDoOutput(true);
        StringBuilder response;
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        JsonElement jResponse = new Gson().fromJson(response.toString(), JsonElement.class);
        return jResponse;

    }

    private URL buildQueryParams(URL baseURL, Map<String,String> queryParams) throws MalformedURLException, UnsupportedEncodingException {
        StringBuffer base = new StringBuffer();
        base.append(baseURL.toString());
        if (queryParams!=null && queryParams.size()>0) {
            base.append("?");
            List<String> qpList = new ArrayList<>();
            for (Map.Entry<String, String> qpEntry : queryParams.entrySet()) {
                qpList.add(qpEntry.getKey()+"="+ URLEncoder.encode(qpEntry.getValue(), StandardCharsets.UTF_8.toString()));
            }
            base.append(String.join("&",qpList));
        }
        return new URL(base.toString());
    }
}
