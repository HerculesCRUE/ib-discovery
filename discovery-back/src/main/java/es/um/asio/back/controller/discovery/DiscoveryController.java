package es.um.asio.back.controller.discovery;

import com.google.gson.*;
import es.um.asio.service.config.Datasources;
import es.um.asio.service.exceptions.CustomDiscoveryException;
import es.um.asio.service.model.BasicAction;
import es.um.asio.service.model.Decision;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.relational.*;
import es.um.asio.service.proxy.JobRegistryProxy;
import es.um.asio.service.proxy.RequestRegistryProxy;
import es.um.asio.service.repository.relational.RequestRegistryRepository;
import es.um.asio.service.service.EntitiesHandlerService;
import es.um.asio.service.service.impl.CacheServiceImp;
import es.um.asio.service.service.impl.DataHandlerImp;
import es.um.asio.service.service.impl.JobHandlerServiceImp;
import es.um.asio.service.service.impl.OpenSimilaritiesHandlerImpl;
import es.um.asio.service.util.Utils;
import es.um.asio.service.validation.group.Create;
import io.swagger.annotations.*;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


/**
 * Message controller.
 */
@RestController
@RequestMapping(DiscoveryController.Mappings.BASE)
public class DiscoveryController {


    @Autowired
    ApplicationState applicationState;

    @Autowired
    CacheServiceImp cache;

    @Autowired
    EntitiesHandlerService entitiesHandlerService;

    @Autowired
    Datasources dataSources;

    @Autowired
    JobHandlerServiceImp jobHandlerServiceImp;

    @Autowired
    DataHandlerImp dataHandler;

    @Autowired
    RequestRegistryRepository requestRegistryRepository;

    @Autowired
    RequestRegistryProxy requestRegistryProxy;

    @Autowired
    JobRegistryProxy jobRegistryProxy;

    @Autowired
    OpenSimilaritiesHandlerImpl openSimilaritiesHandler;

    @Value("${app.node}")
    String localNode;

    @Value("${lod.host}")
    String lodHost;

    @Value("${lod.port}")
    String lodPort;

    Set<String> tempRequestCode = new HashSet<>();

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryController.class);

    /**
     * Status.
     *
     * @return Get the App status
     */
    @GetMapping(Mappings.STATUS)
    @ApiOperation(value = "Get status of the Application", tags = "control")
    public ApplicationState status() {
        return applicationState;
    }

    @ControllerAdvice
    public class GlobalDefaultExceptionHandler {

        @ExceptionHandler(Exception.class)
        public String exception(Exception e) {

            return "error";
        }
    }


    /**
     * Get Entity Stats.
     *
     * @return Get the Entty Stats
     */
    @GetMapping(Mappings.ENTITY_STATS)
    //@Secured(Role.ANONYMOUS_ROLE)
    @ApiOperation(value = "Get Entity Stats", tags = "control")
    public Map<String, Object> getEntityStats(
            @ApiParam(name = "node", value = "um", defaultValue = "um", required = false)
            @RequestParam(required = false, defaultValue = "um") @Validated(Create.class) final String node,
            @ApiParam(name = "tripleStore", value = "The triple store", defaultValue = "fuseki", required = false)
            @RequestParam(required = true, defaultValue = "fuseki") @Validated(Create.class) final String tripleStore,
            @ApiParam(name = "className", value = "Class Name", required = false)
            @RequestParam(required = true) @Validated(Create.class) final String className
    ) {
        Map<String,Object> stats = new HashMap<>();
        stats.put("status", applicationState);
        stats.put("stats",cache.getStatsHandler().buildStats(node,tripleStore,className));
        return stats;
    }

    /**
     * Find similarities by Class Name.
     *
     * @return Get the Entty Stats
     */

    @PostMapping(Mappings.ENTITY_LINK)
    @ApiOperation(value = "Find Similarities between entities by class", tags = "search")
    public Map<String,Object> findEntityLinkByNodeTripleStoreAndClass(
            @ApiParam(name = "userId", value = "1", defaultValue = "1", required = true)
            @RequestParam(required = true, defaultValue = "1") @Validated(Create.class) final String userId,
            @ApiParam(name = "requestCode", value = "request code. If not present, a random request code will be created", required = false)
            @RequestParam(required = false) @Validated(Create.class) String requestCode,
            @ApiParam(name = "node", value = "um", defaultValue = "um", required = false)
            @RequestParam(required = true, defaultValue = "um") @Validated(Create.class) final String node,
            @ApiParam(name = "tripleStore", value = "The triple store", defaultValue = "fuseki", required = false)
            @RequestParam(required = true, defaultValue = "fuseki") @Validated(Create.class) final String tripleStore,
            @ApiParam(name = "className", value = "Class Name", required = false)
            @RequestParam(required = true) @Validated(Create.class) final String className,
            @ApiParam(name = "doSynchronous", value = "Handle request as synchronous request", defaultValue = "false", required = false)
            @RequestParam(required = false, defaultValue = "false") @Validated(Create.class) final boolean doSynchronous,
            @ApiParam(name = "webHook", value = "Web Hook, URL Callback with response", required = false)
            @RequestParam(required = false) @Validated(Create.class) final String webHook,
            @ApiParam(name = "propague_in_kafka", value = "Propague result in Kafka", defaultValue = "true", required = false)
            @RequestParam(required = false, defaultValue = "true") @Validated(Create.class) final boolean propagueInKafka,
            @ApiParam(name = "linkEntities", value = "Search also in other Nodes and Triple Stores for link", defaultValue = "false", required = true)
            @RequestParam(required = true) @Validated(Create.class) final boolean linkEntities,
            @ApiParam(name = "applyDelta", value = "SearchindEntityLinkByEntityAndNodeTripleStoreAndClass only from last date in similar request", defaultValue = "true",required = true)
            @RequestParam(required = true) @Validated(Create.class) final boolean applyDelta,
            @ApiParam(name = "email", value = "Email to send at the conclusion of the request",required = false)
            @RequestParam(required = false) @Validated(Create.class) final String email
    ) {

        if (!doSynchronous && ((!Utils.isValidString(webHook) || !Utils.isValidURL(webHook)) && !propagueInKafka) ) {
            throw new CustomDiscoveryException("The request must be synchronous or web hook or/and propague in kafka must be valid" );
        }
        if (requestCode == null) {
            do {
                requestCode = UUID.randomUUID().toString();
            } while (requestRegistryRepository.existRequestCode(requestCode)!=0);
        }
        if (!requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId,requestCode, RequestType.ENTITY_LINK_CLASS).isEmpty())
            throw new CustomDiscoveryException("UserId and RequestCode for type ENTITY_LINK_CLASS must be unique");

        JobRegistry jobRegistry = jobHandlerServiceImp.addJobRegistryForClass(applicationState.getApplication(),userId,requestCode,node,tripleStore,className,doSynchronous,webHook,propagueInKafka,linkEntities,applyDelta, (Utils.isValidEmailAddress(email)?email:null) );
        JsonObject jResponse = new JsonObject();
        jResponse.add("state",applicationState.toSimplifiedJson());
        if (jobRegistry!=null) {
            JsonObject jJobRegistry = jobRegistry.toSimplifiedJson(cache);
            jJobRegistry.addProperty("userId", userId);
            jJobRegistry.addProperty("requestCode", requestCode);
            jJobRegistry.addProperty("requestType", RequestType.ENTITY_LINK_CLASS.toString());
            jResponse.add("response", jJobRegistry);
        } else {
            jResponse.addProperty("message","Application is not ready, please retry late");
        }
        return new Gson().fromJson(jResponse,Map.class);
    }


    /**
     * Find similarities for all class.
     *
     * @return Get the Entty Stats
     */

    @PostMapping(Mappings.ENTITY_LINK_ALL)
    @ApiOperation(value = "Find Similarities between entities for all class", tags = "search")
    public List<Map<String,Object>> findEntityLinkByNodeTripleStoreForAllClass(
            @ApiParam(name = "userId", value = "1", defaultValue = "1", required = true)
            @RequestParam(required = true, defaultValue = "1") @Validated(Create.class) final String userId,
            @ApiParam(name = "requestCode", value = "request code. If not present, a random request code will be created", required = false)
            @RequestParam(required = false) @Validated(Create.class)  String requestCode,
            @ApiParam(name = "tripleStore", value = "The triple store", defaultValue = "fuseki", required = false)
            @RequestParam(required = true, defaultValue = "fuseki") @Validated(Create.class) final String tripleStore,
            @ApiParam(name = "webHook", value = "Web Hook, URL Callback with response", required = false)
            @RequestParam(required = false) @Validated(Create.class) final String webHook,
            @ApiParam(name = "propague_in_kafka", value = "Propague result in Kafka", defaultValue = "true", required = false)
            @RequestParam(required = false, defaultValue = "true") @Validated(Create.class) final boolean propagueInKafka,
            @ApiParam(name = "linkEntities", value = "Search also in other Nodes and Triple Stores for link", defaultValue = "false", required = true)
            @RequestParam(required = true) @Validated(Create.class) final boolean linkEntities,
            @ApiParam(name = "applyDelta", value = "SearchindEntityLinkByEntityAndNodeTripleStoreAndClass only from last date in similar request", defaultValue = "true",required = true)
            @RequestParam(required = true) @Validated(Create.class) final boolean applyDelta,
            @ApiParam(name = "email", value = "Email to send at the conclusion of the request",required = false)
            @RequestParam(required = false) @Validated(Create.class) final String email
    ) {

        if (((!Utils.isValidString(webHook) || !Utils.isValidURL(webHook)) && !propagueInKafka) ) {
            throw new CustomDiscoveryException("The request must be synchronous or web hook or/and propague in kafka must be valid" );
        }
        if (requestCode == null) {
            do {
                requestCode = UUID.randomUUID().toString();
            } while (requestRegistryRepository.existRequestCode(requestCode)!=0);
        }

        if (!requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId,requestCode, RequestType.ENTITY_LINK_CLASS).isEmpty())
            throw new CustomDiscoveryException("UserId and RequestCode for type ENTITY_LINK_CLASS must be unique");

        List<Map<String,Object>> responses = new ArrayList<>();
        for (String className : cache.getAllClassesByNodeAndTripleStore(localNode,tripleStore)) {

            try {
                Map<String, Object> response = findEntityLinkByNodeTripleStoreAndClass(userId, requestCode + "/" + className, localNode, tripleStore, className, false, webHook, propagueInKafka, linkEntities, applyDelta, email);
                responses.add(response);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }
        return responses;
    }

    /**
     * Get Entity Stats.
     *
     * @return Get the Entty Stats
     */
    @PostMapping(Mappings.ENTITY_LINK_ENTITY)
    @ApiOperation(value = "Find Similarities between entities by instance in body", tags = "search")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "object",
                    dataType = "TripleObject",
                    examples = @io.swagger.annotations.Example(
                            value = {
                                    @ExampleProperty(value = "’object‘：{'id': '1','className': 'Actividad','attributes':{" +
                                            "'codTipoActividad': '00',"+
                                            "'codTipoMoneda': 'EUR',"+
                                            "'fechaInicioActividad': '2008/04/28 00:00:00',"+
                                            "'idActividad': '5192',"+
                                            "'idGrupoActividades': '3470',"+
                                            "'idTercero': '146051',"+
                                            "'importeBase': '2400.0',"+
                                            "'importeRepercutido': '384.0',"+
                                            "'importeTotal': '2784.0',"+
                                            "'terceroConfidencial': 'N',"+
                                            "'titulo': 'INVESTIGACIÓN DE PLAN DE ACTUACIÓN PARA PREVENIR Y/O CONTROLAR UN BROTE DE BOTULISMO EN EL HONGO. VERANO 2008',"+
                                            "}" +
                                            "}", mediaType = "application/json")
                            }))
    })
    @ResponseBody
    public Map<String,Object> findEntityLinkByEntityAndNodeTripleStoreAndClass(
            @ApiParam(name = "userId", value = "1", defaultValue = "1", required = true)
            @RequestParam(required = true, defaultValue = "1") @Validated(Create.class) final String userId,
            @ApiParam(name = "requestCode", value = "request code. If not present, a random request code will be created", required = false)
            @RequestParam(required = false) @Validated(Create.class) String requestCode,
            @ApiParam(name = "node", value = "um", defaultValue = "um", required = false)
            @RequestParam(required = false, defaultValue = "um") @Validated(Create.class) final String node,
            @ApiParam(name = "tripleStore", value = "The triple store", defaultValue = "fuseki", required = false)
            @RequestParam(required = true, defaultValue = "fuseki") @Validated(Create.class) final String tripleStore,
            @ApiParam(name = "className", value = "Class Name", required = true)
            @RequestParam(required = true) @Validated(Create.class) final String className,
            @ApiParam(name = "entityId", value = "12345", required = true)
            @RequestParam(required = true) @Validated(Create.class) final String entityId,
            @ApiParam(name = "doSynchronous", value = "false", required = false)
            @RequestParam(required = false, defaultValue = "false") @Validated(Create.class) final boolean doSynchronous,
            @ApiParam(name = "webHook", value = "Web Hook, URL Callback with response", required = false)
            @RequestParam(required = false) @Validated(Create.class) final String webHook,
            @ApiParam(name = "propague_in_kafka", value = "false", required = false)
            @RequestParam(required = false, defaultValue = "false") @Validated(Create.class) final boolean propagueInKafka,
            @ApiParam(name = "linkEntities", value = "true", required = true)
            @RequestParam(required = true) @Validated(Create.class) final boolean linkEntities,
            @NotNull @RequestBody final Object object
    ) {
        if (status().getAppState().getOrder() == 0) {
            throw new CustomDiscoveryException("App not initialized. State: " + applicationState.getAppState().name());
        }
        JSONObject jsonData = new JSONObject((LinkedHashMap) object);
        String jBodyStr = jsonData.toString();
        if (requestCode == null) {
            do {
                requestCode = UUID.randomUUID().toString();
            } while (requestRegistryRepository.existRequestCode(requestCode)!=0 && !tempRequestCode.contains(requestCode));
        }
        tempRequestCode.add(requestCode);
        if (!requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId,requestCode, RequestType.ENTITY_LINK_INSTANCE).isEmpty())
            throw new CustomDiscoveryException("UserId and RequestCode for type ENTITY_LINK_CLASS must be unique");

        if (!doSynchronous && ((!Utils.isValidString(webHook) || !Utils.isValidURL(webHook)) && !propagueInKafka) ) {
            throw new CustomDiscoveryException("The request must be synchronous or web hook or/and propague in kafka must be valid" );
        }
        try {
            JobRegistry jobRegistry = jobHandlerServiceImp.addJobRegistryForInstance(
                    applicationState.getApplication(),
                    userId,
                    requestCode,
                    node,
                    tripleStore,
                    className,
                    entityId,
                    jBodyStr,
                    doSynchronous,webHook,
                    propagueInKafka,
                    linkEntities
            );
            JsonObject jResponse = new JsonObject();
            jResponse.add("state",applicationState.toSimplifiedJson());
            if (jobRegistry!=null) {
                JsonObject jJobRegistry = jobRegistry.toSimplifiedJson(cache);
                jJobRegistry.addProperty("userId", userId);
                jJobRegistry.addProperty("requestCode", requestCode);
                jJobRegistry.addProperty("requestType", RequestType.ENTITY_LINK_INSTANCE.toString());
                jResponse.add("response", jJobRegistry);
            } else {
                jResponse.addProperty("message","Application is not ready, please retry late");
            }
            tempRequestCode.remove(requestCode);
            return new Gson().fromJson(jResponse,Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomDiscoveryException(e.getMessage());
        }
    }

    /**
     * Find similarities by Class Name.
     *
     * @return Get the Entty Stats
     */
    @PostMapping(Mappings.LOD_SEARCH)
    @ApiOperation(value = "Find Similarities between entities in cloud LOD", tags = "search")
    public Map<String,Object> findFindLinkInLODByNodeTripleStoreAndClass(
            @ApiParam(name = "userId", value = "1", defaultValue = "1", required = true)
            @RequestParam(required = true, defaultValue = "1") @Validated(Create.class) final String userId,
            @ApiParam(name = "requestCode", value = "request code. If not present, a random request code will be created", required = false)
            @RequestParam(required = false) @Validated(Create.class) String requestCode,
            @ApiParam(name = "dataSource", value = "Datasources to search. * is wildcard. Then search in all data sources", defaultValue = "*", required = false)
            @RequestParam(required = false, defaultValue = "*") @Validated(Create.class) final String dataSource,
            @ApiParam(name = "node", value = "um", defaultValue = "um", required = false)
            @RequestParam(required = true, defaultValue = "um") @Validated(Create.class) final String node,
            @ApiParam(name = "tripleStore", value = "The triple store", defaultValue = "fuseki", required = false)
            @RequestParam(required = true, defaultValue = "fuseki") @Validated(Create.class) final String tripleStore,
            @ApiParam(name = "className", value = "Class Name", required = false)
            @RequestParam(required = true) @Validated(Create.class) final String className,
            @ApiParam(name = "doSynchronous", value = "Handle request as synchronous request", defaultValue = "false", required = false)
            @RequestParam(required = false, defaultValue = "false") @Validated(Create.class) final boolean doSynchronous,
            @ApiParam(name = "webHook", value = "Web Hook, URL Callback with response", required = false)
            @RequestParam(required = false) @Validated(Create.class) final String webHook,
            @ApiParam(name = "propague_in_kafka", value = "Propague result in Kafka", defaultValue = "true", required = false)
            @RequestParam(required = false, defaultValue = "true") @Validated(Create.class) final boolean propagueInKafka,
            @ApiParam(name = "applyDelta", value = "SearchindEntityLinkByEntityAndNodeTripleStoreAndClass only from last date in similar request", defaultValue = "true",required = true)
            @RequestParam(required = true) @Validated(Create.class) final boolean applyDelta,
            @ApiParam(name = "email", value = "Email to send at the conclusion of the request",required = false)
            @RequestParam(required = false) @Validated(Create.class) final String email
    ) {

        if (!doSynchronous && ((!Utils.isValidString(webHook) || !Utils.isValidURL(webHook)) && !propagueInKafka) ) {
            throw new CustomDiscoveryException("The request must be synchronous or web hook or/and propague in kafka must be valid" );
        }
        if (requestCode == null) {
            do {
                requestCode = UUID.randomUUID().toString();
            } while (requestRegistryRepository.existRequestCode(requestCode)!=0);
        }
        if (!requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId,requestCode, RequestType.LOD_SEARCH).isEmpty())
            throw new CustomDiscoveryException("UserId and RequestCode for type ENTITY_LINK_CLASS must be unique");
        JobRegistry jobRegistry = jobHandlerServiceImp.addJobRegistryForLOD(applicationState.getApplication(),userId,requestCode,node,tripleStore,className,doSynchronous,webHook,propagueInKafka,applyDelta, dataSource, email);
        JsonObject jResponse = new JsonObject();
        jResponse.add("state",applicationState.toSimplifiedJson());
        if (jobRegistry!=null) {
            JsonObject jJobRegistry = jobRegistry.toSimplifiedJson(cache);
            jJobRegistry.addProperty("userId", userId);
            jJobRegistry.addProperty("requestCode", requestCode);
            jJobRegistry.addProperty("requestType", RequestType.LOD_SEARCH.toString());
            jResponse.add("response", jJobRegistry);
        } else {
            jResponse.addProperty("message","Application is not ready, please retry late");
        }
        return new Gson().fromJson(jResponse,Map.class);
    }


    /**
     * Find similarities by Class Name.
     *
     * @return Get the Entty Stats
     */
    @PostMapping(Mappings.LOD_SEARCH_ALL)
    @ApiOperation(value = "Find Similarities between entities in cloud LOD for all class", tags = "search")
    public List<Map<String,Object>> findFindLinkInLODByNodeTripleStore(
            @ApiParam(name = "userId", value = "1", defaultValue = "1", required = true)
            @RequestParam(required = true, defaultValue = "1") @Validated(Create.class) final String userId,
            @ApiParam(name = "requestCode", value = "request code. If not present, a random request code will be created", required = false)
            @RequestParam(required = false) @Validated(Create.class) String requestCode,
            @ApiParam(name = "tripleStore", value = "The triple store", defaultValue = "fuseki", required = false)
            @RequestParam(required = true, defaultValue = "fuseki") @Validated(Create.class) final String tripleStore,
            @ApiParam(name = "webHook", value = "Web Hook, URL Callback with response", required = false)
            @RequestParam(required = false) @Validated(Create.class) final String webHook,
            @ApiParam(name = "propague_in_kafka", value = "Propague result in Kafka", defaultValue = "true", required = false)
            @RequestParam(required = false, defaultValue = "true") @Validated(Create.class) final boolean propagueInKafka,
            @ApiParam(name = "applyDelta", value = "SearchindEntityLinkByEntityAndNodeTripleStoreAndClass only from last date in similar request", defaultValue = "true",required = true)
            @RequestParam(required = true) @Validated(Create.class) final boolean applyDelta,
            @ApiParam(name = "email", value = "Email to send at the conclusion of the request",required = false)
            @RequestParam(required = false) @Validated(Create.class) final String email
    ) throws IOException {

        if ( ((!Utils.isValidString(webHook) || !Utils.isValidURL(webHook)) && !propagueInKafka) ) {
            throw new CustomDiscoveryException("The request must be synchronous or web hook or/and propague in kafka must be valid" );
        }
        if (requestCode == null) {
            do {
                requestCode = UUID.randomUUID().toString();
            } while (requestRegistryRepository.existRequestCode(requestCode)!=0);
        }
        if (!requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId,requestCode, RequestType.LOD_SEARCH).isEmpty())
            throw new CustomDiscoveryException("UserId and RequestCode for type ENTITY_LINK_CLASS must be unique");
        List<Map<String,Object>> responses = new ArrayList<>();
        JsonElement jLodNames = Utils.doRequest(new URL(lodHost+":"+lodPort+"/lod/datasets"), Connection.Method.GET,null,null,null,true);
        List<String> dataSetNames = new Gson().fromJson(jLodNames.getAsJsonArray(), ArrayList.class);
        JsonElement jClassNames = Utils.doRequest(new URL(lodHost+":"+lodPort+"/lod/clases"), Connection.Method.GET,null,null,null,true);
        List<String> classNames = new Gson().fromJson(jClassNames.getAsJsonArray(), ArrayList.class);
        List<String> classNamesFiltered = cache.getAllClassesByNodeAndTripleStore(localNode,tripleStore)
                .stream()
                .filter(c -> Utils.machClassName(classNames,c))
                .collect(Collectors.toList());
        for (String className : classNamesFiltered) {
            if (Utils.machClassName(classNames,className)) {
                try {
                    Map<String, Object> response = findFindLinkInLODByNodeTripleStoreAndClass(
                            userId,
                            requestCode + "/" + className,
                            String.join(",", dataSetNames),
                            localNode,
                            tripleStore,
                            className,
                            false,
                            webHook,
                            propagueInKafka,
                            applyDelta,
                            Utils.isValidEmailAddress(email)?email:null
                    );
                    if (response != null) {
                        responses.add(response);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

        return responses;
    }


    /**
     * Get Entity Stats.
     *
     * @return Get the Entty Stats
     */
    @PostMapping(Mappings.ENTITY_CHANGE)
    @ApiOperation(value = "Notifications on changes over instances", tags = "control")
    public ResponseEntity<String> entityChange(
            @ApiParam(name = "node", value = "um", defaultValue = "um", required = true)
            @RequestParam(required = true, defaultValue = "um") @Validated(Create.class) final String node,
            @ApiParam(name = "tripleStore", value = "The triple store", defaultValue = "fuseki", required = false)
            @RequestParam(required = true, defaultValue = "fuseki") @Validated(Create.class) String tripleStore,
            @ApiParam(name = "className", value = "Class Name", required = true)
            @RequestParam(required = true) @Validated(Create.class) final String className,
            @ApiParam(name = "entityLocalURI", required = true)
            @RequestParam(required = true) @Validated(Create.class) final String entityLocalURI,
            @ApiParam(name = "action", value = "", required = true)
            @RequestParam(required = true) @Validated(Create.class) final String action

    ) {
        if (BasicAction.fromString(action) == null) {
            new ResponseEntity<String>("Action not valid: "+ action+ ". Values allowed are [INSERT,UPDATE,DELETE]",HttpStatus.NOT_ACCEPTABLE);
        }
        if (applicationState.getAppState() != ApplicationState.AppState.INITIALIZED) {
            new ResponseEntity<String>("Application not initialized yet. The entity will be updated from cache on app start",HttpStatus.CONFLICT);
        }
        boolean result = false;
        tripleStore = (tripleStore.equals("trellis"))?"fuseki":tripleStore;
        try {
            CompletableFuture<Boolean> future = dataHandler.actualizeData(node, tripleStore, className, entityLocalURI, BasicAction.fromString(action));
            result = future.join();
        } catch (Exception e) {
            return new ResponseEntity<>("FAIL",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (result)
            return new ResponseEntity<>("DONE",HttpStatus.ACCEPTED);
        else
            return new ResponseEntity<>("FAIL",HttpStatus.NOT_ACCEPTABLE);
    }

    @PostMapping(Mappings.RELOAD_CACHE)
    @ApiOperation(value = "Force reload cache from Triple Store Data", tags = "control")
    public ResponseEntity<String> forceReloadCache() {
        try {
            if (applicationState.getAppState().getOrder() >= ApplicationState.AppState.INITIALIZED.getOrder()) {
                dataHandler.populateData();
                return new ResponseEntity<>("DONE",HttpStatus.OK);
            } else {
                return new ResponseEntity<>("IN PROCESS",HttpStatus.OK);
            }
        } catch (ParseException e) {
            logger.error("ParseException: {}",e.getMessage());
        } catch (IOException e) {
            logger.error("IOException: {}",e.getMessage());
        } catch (URISyntaxException e) {
            logger.error("URISyntaxException: {}",e.getMessage());
        }
        return new ResponseEntity<>("FAIL",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(Mappings.GET_RESULT)
    @ApiOperation(value = "Get Job Result by UserId, RequestCode and RequestType", tags = "search")
    public Map<String,Object> getResult(
            @ApiParam(name = "userId", value = "The User Id of the request", required = true)
            @RequestParam(required = true) @Validated(Create.class) final String userId,
            @ApiParam(name = "requestCode", value = "The Request Code of the request", required = true)
            @RequestParam(required = true) @Validated(Create.class) String requestCode,
            @ApiParam(name = "requestType", value = "The Request Type of the request", required = true, allowableValues = "ENTITY_LINK_CLASS, ENTITY_LINK_INSTANCE, LOD_SEARCH")
            @RequestParam(required = true) @Validated(Create.class) RequestType requestType
    ) {
        JsonObject jResponse = new JsonObject();
        //JobRegistry jobRegistry = requestRegistryProxy.findJobRegistryByUserIdAndRequestCodeAndRequestType(userId,requestCode,requestType);
        JobRegistry jobRegistry = jobRegistryProxy.findJobRegistryByUserIdAndRequestCodeAndRequestTypeNoNested(userId,requestCode,requestType);
        if (jobRegistry != null) {
            JsonObject jJobRegistry = jobRegistry.toSimplifiedJson(cache);
            jJobRegistry.addProperty("userId", userId);
            jJobRegistry.addProperty("requestCode", requestCode);
            jJobRegistry.addProperty("requestType", requestType.toString());
            jJobRegistry.addProperty("status", jobRegistry.getStatusResult().toString());
            jResponse.add("response", jJobRegistry);
        } else {
            JsonObject jJobRegistry = new JsonObject();
            jJobRegistry.addProperty("userId", userId);
            jJobRegistry.addProperty("requestCode", requestCode);
            jJobRegistry.addProperty("requestType", requestType.toString());
            jJobRegistry.addProperty("status", "not found");
            jResponse.add("response", jJobRegistry);
        }
        return new Gson().fromJson(jResponse,Map.class);
    }

    @GetMapping(Mappings.GET_RESULT + "/{userId}")
    @ApiOperation(value = "Get Job Result by UserId", tags = "search")
    public Map<String,Object> getResultByUserId(
            @ApiParam(name = "userId", value = "The User Id of the request", required = true)
            @PathVariable(required = true,value = "userId") @Validated(Create.class) final String userId
    ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        JsonObject jResponse = new JsonObject();
        Optional<List<RequestRegistry>> requestRegistries = requestRegistryRepository.findByUserIdOrderByRequestDateDesc(userId);
        if (requestRegistries.isPresent()) {
            for (RequestRegistry rr: requestRegistries.get()) {
                if (!jResponse.has(rr.getRequestType().toString())) {
                    jResponse.add(rr.getRequestType().toString(), new JsonObject());
                }
                if (!jResponse.get(rr.getRequestType().toString()).getAsJsonObject().has(rr.getJobRegistry().getClassName())) {
                    jResponse.get(rr.getRequestType().toString()).getAsJsonObject().add(rr.getJobRegistry().getClassName(), new JsonArray());
                }
                JsonObject jItem = new JsonObject();
                jItem.addProperty("requestCode",rr.getRequestCode());
                jItem.addProperty("requestDate",sdf.format(rr.getRequestDate())+" UTC");
                jResponse.get(rr.getRequestType().toString()).getAsJsonObject().get(rr.getJobRegistry().getClassName()).getAsJsonArray().add(jItem);
            }
        }
        return new Gson().fromJson(jResponse,Map.class);
    }

    @GetMapping(Mappings.GET_OPEN_OBJECT_RESULT)
    @ApiOperation(value = "Get all Open Objects Result", tags = "search",
            produces = "application/json")
    public String getAllOpenObjectResult(
            @ApiParam(name = "node", value = "The node to search", required = true, defaultValue = "um")
            @RequestParam(required = true) @Validated(Create.class) final String node,
            @ApiParam(name = "tripleStore", value = "The triple store to search", required = true, defaultValue = "fuseki")
            @RequestParam(required = true) @Validated(Create.class) String tripleStore
    ) {
        List<ObjectResult> results = openSimilaritiesHandler.getOpenObjectResults(node,tripleStore);
        Collections.sort(results,Collections.reverseOrder());
        JsonArray response = new JsonArray();
        for (ObjectResult or : results) {
            response.add(or.toSimplifiedJson(true,cache));
        }
        return new GsonBuilder().setPrettyPrinting().create().toJson(response);
    }

    @PostMapping(Mappings.ACTION_OVER_OBJECT_RESULT)
    @ApiOperation(value = "Apply Decision over Object Result", tags = "search",
            produces = "application/json")
    public String decisionOverObjectResult(
            @ApiParam(name = "className", value = "The class name of the Object Result", required = true)
            @RequestParam(required = true) @Validated(Create.class) final String className,
            @ApiParam(name = "entityIdMainObject", value = "The entity Id of the main object", required = true)
            @RequestParam(required = true) @Validated(Create.class) String entityIdMainObject,
            @ApiParam(name = "entityIdRelatedObject", value = "The entity Id of the related object", required = true)
            @RequestParam(required = true) @Validated(Create.class) String entityIdRelatedObject,
            @ApiParam(name = "decision", value = "The decision over objects", required = true, allowableValues = "ACCEPTED, DISCARDED, INVERTED")
            @RequestParam(required = true) @Validated(Create.class) Decision decision
    ) {
        Map<ObjectResult,List<ActionResult>> result = openSimilaritiesHandler.decisionOverObjectResult(className,entityIdMainObject,entityIdRelatedObject,decision);
        JsonArray response = new JsonArray();
        for (ObjectResult or : result.keySet()) {
            response.add(or.toSimplifiedJson(true,cache));
        }
        JsonArray jActionResults = new JsonArray();
        for (List<ActionResult> arList : result.values()) {
            for (ActionResult ar : arList) {
                JsonObject jAction = new JsonObject();
                jAction.addProperty("action", ar.getAction().toString());
                JsonArray jObjectResultActionsArray = new JsonArray();
                for (ObjectResult or : ar.getObjectResults()) {
                    jObjectResultActionsArray.add(or.toSimplifiedJson(false,cache));
                }
                jAction.add("items", jObjectResultActionsArray);
                jActionResults.add(jAction);
            }
        }
        return new GsonBuilder().setPrettyPrinting().create().toJson(jActionResults);
    }


    static final class Mappings {

        private Mappings(){}

        /**
         * Controller request mapping.
         */
        protected static final String BASE = "/discovery/";

        protected static final String STATUS = "/status";

        protected static final String ENTITY_STATS = "/entity/stats";

        protected static final String RELOAD_CACHE = "/cache/force-reload";

        protected static final String ENTITY_LINK = "/entity-link";

        protected static final String ENTITY_LINK_ALL = "/entity-link/all";

        protected static final String ENTITY_LINK_ENTITY = "/entity-link/instance";

        protected static final String ENTITY_CHANGE = "/entity/change";

        protected static final String LOD_SEARCH = "/lod/search";

        protected static final String LOD_SEARCH_ALL = "/lod/search/all";

        protected static final String GET_RESULT = "/result";


        protected static final String GET_OPEN_OBJECT_RESULT = "/object-result/open";

        protected static final String ACTION_OVER_OBJECT_RESULT = "/object-result/action";

    }
}
