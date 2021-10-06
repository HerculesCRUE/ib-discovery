package es.um.asio.service.service.impl;

import com.google.gson.JsonObject;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import es.um.asio.service.config.DataBehaviour;
import es.um.asio.service.config.Hierarchies;
import es.um.asio.service.constants.Constants;
import es.um.asio.service.exceptions.CustomDiscoveryException;
import es.um.asio.service.listener.AppEvents;
import es.um.asio.service.model.MergeType;
import es.um.asio.service.model.SimilarityResult;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.URIComponent;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.rdf.TripleObjectLink;
import es.um.asio.service.model.relational.*;
import es.um.asio.service.proxy.JobRegistryProxy;
import es.um.asio.service.proxy.RequestRegistryProxy;
import es.um.asio.service.repository.relational.ActionResultRepository;
import es.um.asio.service.repository.relational.JobRegistryRepository;
import es.um.asio.service.repository.relational.ObjectResultRepository;
import es.um.asio.service.repository.relational.RequestRegistryRepository;
import es.um.asio.service.service.trellis.TrellisCache;
import es.um.asio.service.service.trellis.TrellisOperations;
import es.um.asio.service.util.Utils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Job Handler implementation.
 * Let handle Jobs in asynchronous or synchronous mode
 * Prevents two identical jobs from being processed multiple times
 * Manage the execution of Jobs
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class JobHandlerServiceImp {

    private final Logger logger = LoggerFactory.getLogger(JobHandlerServiceImp.class);

    Map<String, Map<String, Map<String, Map<String, JobRegistry>>>> jrClassMap;
    Map<String, Map<String, Map<String, Map<String, JobRegistry>>>> jrEntityMap;
    Map<String, Map<String, Map<String, Map<String, JobRegistry>>>> jrLODMap;
    Queue<JobRegistry> qClasses;
    Queue<JobRegistry> qInstances;
    Queue<JobRegistry> qLod;
    volatile boolean isWorking;
    boolean isAppReady;

    @Autowired
    RequestRegistryProxy requestRegistryProxy;

    @Autowired
    RequestRegistryRepository requestRegistryRepository;

    @Autowired
    JobRegistryRepository jobRegistryRepository;

    @Autowired
    JobRegistryProxy jobRegistryProxy;

    @Autowired
    EntitiesHandlerServiceImp entitiesHandlerServiceImp;

    @Autowired
    ObjectResultRepository objectResultRepository;

    @Autowired
    ActionResultRepository actionResultRepository;

    @Autowired
    ApplicationState applicationState;

    @Autowired
    KafkaHandlerService kafkaHandlerService;

    @Autowired
    TrellisOperations trellisOperations;

    @Autowired
    TrellisCache trellisCache;

    @Autowired
    Hierarchies hierarchies;

    @Autowired
    CacheServiceImp cache;

    @Autowired
    SchemaServiceImp schemaServiceImp;

    @Value("${app.domain}")
    String domain;

    @Value("${app.subdomain}")
    String subDomain;

    @Value("${app.language}")
    String language;

    @Autowired
    DataBehaviour dataBehaviour;


    /**
     * Initialization
     * Initialize the data structures and manages the application listeners
     */
    @PostConstruct
    public void init() {
        jrClassMap = new LinkedHashMap<>();
        jrEntityMap = new LinkedHashMap<>();
        jrLODMap = new LinkedHashMap<>();
        qClasses = new LinkedList<>();
        qInstances = new LinkedList<>();
        qLod = new LinkedList<>();
        isWorking = false;
        isAppReady = false;
        applicationState.addAppListener(new AppEvents() {
            /*
             * This method not is user
             */
            @Override
            public void onCachedDataIsReady() {
                isAppReady = true;
                logger.info("onCachedDataIsReady");
            }

            @Override
            public void onRealDataIsReady() {
                isAppReady = true;
                logger.info("onRealDataIsReady");
            }

            @Override
            public void onElasticSearchIsReady() {
                isAppReady = true;
                handleQueueFindSimilarities();
            }
        });

    }


    /**
     * Handle a new Job for search similitudes by class
     * @see DiscoveryApplication
     * @see JobRegistry
     * @param application DiscoveryApplication The discovery application to which the job belongs
     * @param userId String. The userId for handle the response
     * @param requestCode String. The requestCode for handle the response
     * @param node String. The node where we want search similitudes
     * @param tripleStore String. The triple store where we want search similitudes
     * @param className String. The class name where we want search similitudes
     * @param doSync boolean. Do or not the operation in synchronous mode
     * @param webHook String. Web Hook where the call back will be done
     * @param propagueInKafka boolean. Do or not the response propagation in kafka
     * @param searchLinks boolean. Do or not the search of similarities in other nodes
     * @param applyDelta boolean. Do or not the search only with deltas since the last modification
     * @return JobRegistry with the results id is synchronous, else Job registry with the request id for get the response when the Job will be finished
     */
    public JobRegistry addJobRegistryForClass(
            DiscoveryApplication application,
            String userId, String requestCode,
            String node,
            String tripleStore,
            String className,
            boolean doSync,
            String webHook,
            boolean propagueInKafka,
            boolean searchLinks,
            boolean applyDelta
    ) {
        if (!jrClassMap.containsKey(node))
            jrClassMap.put(node, new LinkedHashMap<>());
        if (!jrClassMap.get(node).containsKey(tripleStore))
            jrClassMap.get(node).put(tripleStore, new LinkedHashMap<>());
        if (!jrClassMap.get(node).get(tripleStore).containsKey(className))
            jrClassMap.get(node).get(tripleStore).put(className, new LinkedHashMap<>());

        // Busco si existe un JobRegistry anterior
        JobRegistry jobRegistry = null;
        boolean isNewJob = false;
        for (Map.Entry<String, JobRegistry> jrClassEntry : jrClassMap.get(node).get(tripleStore).get(className).entrySet()) {
            if (!jrClassEntry.getValue().isCompleted() && (jobRegistry == null || (jrClassEntry.getValue().getMaxRequestDate().after(jobRegistry.getMaxRequestDate()) && jrClassEntry.getValue().isSearchLinks() == jobRegistry.isSearchLinks()))) {
                jobRegistry = jrClassEntry.getValue();
            }
        }
        if (jobRegistry == null) { // Si no existe lo creo
            jobRegistry = new JobRegistry(application, node, tripleStore, className, searchLinks);
            isNewJob = true;
        }
        jobRegistry.setDoSync(doSync);

        if (applyDelta) {
            Date deltaDate = jobRegistryRepository.getLastDateFromNodeAndTripleStoreAndClassName(node, tripleStore, className, RequestType.ENTITY_LINK_CLASS.toString());
            jobRegistry.setSearchFromDelta(deltaDate);
        }

        // Request Registry

        RequestRegistry requestRegistry;
        Optional<RequestRegistry> requestRegistryOpt = Optional.empty();
        try {
            requestRegistryOpt = requestRegistryProxy.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, RequestType.ENTITY_LINK_CLASS);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (requestRegistryOpt.isEmpty()) {
            requestRegistry = new RequestRegistry(userId, requestCode, RequestType.ENTITY_LINK_CLASS, new Date());
        } else {
            requestRegistry = requestRegistryOpt.get();
        }
        requestRegistry.setWebHook(webHook);
        requestRegistry.setPropagueInKafka(propagueInKafka);
        if (Utils.isValidString(jobRegistry.getId())) {
            requestRegistry.setJobRegistry(jobRegistry);
            // requestRegistryProxy.save(requestRegistry);
        }

        jobRegistry.addRequestRegistry(requestRegistry);
        jrClassMap.get(node).get(tripleStore).get(className).put(String.valueOf(requestRegistry.hashCode()), jobRegistry);

        try {
            jobRegistry = jobRegistryRepository.saveAndFlush(jobRegistry);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        /*
        for (RequestRegistry rr : jobRegistry.getRequestRegistries()) {
            requestRegistryProxy.save(rr);
        }
         */
        if (doSync) {
            if (isAppReady)
                jobRegistry = findSimilaritiesByClass(jobRegistry);
            else
                return null;
        } else {
            if (isNewJob) {
                qClasses.add(jobRegistry);
            }
        }
        handleQueueFindSimilarities();
        return jobRegistry;
    }



    /**
     * Handle a new Job for search similitudes for the instance pass in parameter, with her class
     * @see DiscoveryApplication
     * @see JobRegistry
     * @param application DiscoveryApplication The discovery application to which the job belongs
     * @param userId String. The userId for handle the response
     * @param requestCode String. The requestCode for handle the response
     * @param node String. The node where we want search similitudes
     * @param tripleStore String. The triple store where we want search similitudes
     * @param className String. The class name where we want search similitudes
     * @param entityId String. The entity Id
     * @param jBodyStr String. The attributes of the object to search
     * @param doSync boolean. Do or not the operation in synchronous mode
     * @param webHook String. Web Hook where the call back will be done
     * @param propagueInKafka boolean. Do or not the response propagation in kafka
     * @param searchLinks boolean. Do or not the search of similarities in other nodes
     * @return JobRegistry with the results id is synchronous, else Job registry with the request id for get the response when the Job will be finished
     */
    public JobRegistry addJobRegistryForInstance(
            DiscoveryApplication application,
            String userId,
            String requestCode,
            String node,
            String tripleStore,
            String className,
            String entityId,
            String jBodyStr,
            boolean doSync,
            String webHook,
            boolean propagueInKafka,
            boolean searchLinks
    ) {
        if (!jrEntityMap.containsKey(node))
            jrEntityMap.put(node, new LinkedHashMap<>());
        if (!jrEntityMap.get(node).containsKey(tripleStore))
            jrEntityMap.get(node).put(tripleStore, new LinkedHashMap<>());
        if (!jrEntityMap.get(node).get(tripleStore).containsKey(className))
            jrEntityMap.get(node).get(tripleStore).put(className, new LinkedHashMap<>());

        // Busco si existe un JobRegistry anterior

        TripleObject tripleObject;
        try {
            JSONObject jData = new JSONObject(jBodyStr);
            tripleObject = new TripleObject(node, tripleStore, className, jData);
            tripleObject.setId(entityId);
        } catch (Exception e) {
            throw new CustomDiscoveryException("Object data parse error");
        }
        JobRegistry jobRegistry = null;
        boolean isNewJob = false;
        // Recupero los jobEntry si hubiese
        for (Map.Entry<String, JobRegistry> jrClassEntry : jrEntityMap.get(node).get(tripleStore).get(className).entrySet()) {
            if (!jrClassEntry.getValue().isCompleted() && (jobRegistry == null || (jrClassEntry.getValue().getMaxRequestDate().after(jobRegistry.getMaxRequestDate()) && jrClassEntry.getValue().isSearchLinks() == jobRegistry.isSearchLinks()))) {
                jobRegistry = jrClassEntry.getValue();
            }
        }
        if (jobRegistry == null) { // Si no existe lo creo
            jobRegistry = new JobRegistry(application, node, tripleStore, className, searchLinks);
            isNewJob = true;
        }
        jobRegistry.setDoSync(doSync);
        jobRegistry.setBodyRequest(jBodyStr);
        jobRegistry.setTripleObject(tripleObject);
        RequestRegistry requestRegistry;
        Optional<List<RequestRegistry>> requestRegistryOpt = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, RequestType.ENTITY_LINK_INSTANCE);
        if (requestRegistryOpt.isEmpty() || requestRegistryOpt.get().size() == 0) {
            requestRegistry = new RequestRegistry(userId, requestCode, RequestType.ENTITY_LINK_INSTANCE, new Date());
        } else {
            requestRegistry = requestRegistryOpt.get().get(0);
        }
        requestRegistry.setWebHook(webHook);
        requestRegistry.setPropagueInKafka(propagueInKafka);
        requestRegistry.setJobRegistry(jobRegistry);
        if (Utils.isValidString(jobRegistry.getId())) {
            requestRegistry.setJobRegistry(jobRegistry);
        }

        jobRegistry.addRequestRegistry(requestRegistry);
        jrEntityMap.get(node).get(tripleStore).get(className).put(String.valueOf(requestRegistry.hashCode()), jobRegistry);

        jobRegistryRepository.saveAndFlush(jobRegistry);
        for (RequestRegistry rr : jobRegistry.getRequestRegistries()) {
            requestRegistryProxy.save(rr);
        }
        if (doSync) {
            if (isAppReady)
                jobRegistry = findSimilaritiesByInstance(jobRegistry);
            else
                return null;
        } else {
            if (isNewJob) {
                qInstances.add(jobRegistry);
            }
        }
        handleQueueFindSimilarities();
        return jobRegistry;
    }

    /**
     * Handle a new Job for search similitudes by class in the LOD cloud
     * @see DiscoveryApplication
     * @see JobRegistry
     * @param application DiscoveryApplication The discovery application to which the job belongs
     * @param userId String. The userId for handle the response
     * @param requestCode String. The requestCode for handle the response
     * @param node String. The node where we want search similitudes
     * @param tripleStore String. The triple store where we want search similitudes
     * @param className String. The class name where we want search similitudes
     * @param doSync boolean. Do or not the operation in synchronous mode
     * @param webHook String. Web Hook where the call back will be done
     * @param propagueInKafka boolean. Do or not the response propagation in kafka
     * @param applyDelta boolean. Do or not the search only with deltas since the last modification
     * @param applyDelta String. The name of the dataset where will be done the search
     * @return JobRegistry with the results id is synchronous, else Job registry with the request id for get the response when the Job will be finished
     */
    public JobRegistry addJobRegistryForLOD(
            DiscoveryApplication application,
            String userId, String requestCode,
            String node,
            String tripleStore,
            String className,
            boolean doSync,
            String webHook,
            boolean propagueInKafka,
            boolean applyDelta,
            String dataSource
    ) {
        if (!jrLODMap.containsKey(node))
            jrLODMap.put(node, new LinkedHashMap<>());
        if (!jrLODMap.get(node).containsKey(tripleStore))
            jrLODMap.get(node).put(tripleStore, new LinkedHashMap<>());
        if (!jrLODMap.get(node).get(tripleStore).containsKey(className))
            jrLODMap.get(node).get(tripleStore).put(className, new LinkedHashMap<>());

        // Busco si existe un JobRegistry anterior
        JobRegistry jobRegistry = null;
        boolean isNewJob = false;
        for (Map.Entry<String, JobRegistry> jrClassEntry : jrLODMap.get(node).get(tripleStore).get(className).entrySet()) {
            if (!jrClassEntry.getValue().isCompleted() && (jobRegistry == null || (jrClassEntry.getValue().getMaxRequestDate().after(jobRegistry.getMaxRequestDate()) && jrClassEntry.getValue().isSearchLinks() == jobRegistry.isSearchLinks()))) {
                jobRegistry = jrClassEntry.getValue();
            }
        }
        if (jobRegistry == null) { // Si no existe lo creo
            jobRegistry = new JobRegistry(application, node, tripleStore, className, false);
            isNewJob = true;
        }
        jobRegistry.setDoSync(doSync);
        if (applyDelta) {
            Date deltaDate = jobRegistryRepository.getLastDateFromNodeAndTripleStoreAndClassName(node, tripleStore, className, RequestType.LOD_SEARCH.toString());
            jobRegistry.setSearchFromDelta(deltaDate);
        }
        RequestRegistry requestRegistry;
        Optional<List<RequestRegistry>> requestRegistryOpt = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, RequestType.LOD_SEARCH);
        if (requestRegistryOpt.isEmpty() || requestRegistryOpt.get().size() == 0) {
            requestRegistry = new RequestRegistry(userId, requestCode, RequestType.LOD_SEARCH, new Date());
        } else {
            requestRegistry = requestRegistryOpt.get().get(0);
        }
        requestRegistry.setWebHook(webHook);
        requestRegistry.setPropagueInKafka(propagueInKafka);
        if (Utils.isValidString(jobRegistry.getId())) {
            requestRegistry.setJobRegistry(jobRegistry);
            requestRegistryProxy.save(requestRegistry);
        }

        jobRegistry.addRequestRegistry(requestRegistry);
        jrLODMap.get(node).get(tripleStore).get(className).put(String.valueOf(requestRegistry.hashCode()), jobRegistry);
        jobRegistry.setDataSource(dataSource);
        jobRegistryRepository.saveAndFlush(jobRegistry);
        for (RequestRegistry rr : jobRegistry.getRequestRegistries()) {
            requestRegistryProxy.save(rr);
        }
        if (doSync) {
            if (isAppReady)
                jobRegistry = findSimilaritiesInLod(jobRegistry); // TODO: Change
            else
                return null;
        } else {
            if (isNewJob) {
                qLod.add(jobRegistry);
            }
        }
        handleQueueFindSimilarities();
        return jobRegistry;
    }

    /**
     * Manages the heavy request to search for similarities by instance in the same class
     * @param jobRegistry
     * @return JobRegistry with the search completed
     */
    public JobRegistry findSimilaritiesByInstance(JobRegistry jobRegistry) {
        isWorking = true;
        TripleObject to = jobRegistry.getTripleObject();
        if (to == null) {
            logger.error("Triple Object can´t be null");
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.FAIL);
            jobRegistryRepository.saveAndFlush(jobRegistry);
        }
        jobRegistry.setStarted(true);
        jobRegistry.setStartedDate(new Date());
        jobRegistryRepository.saveAndFlush(jobRegistry); // Add for front interface
        try {
            SimilarityResult similarityResult = entitiesHandlerServiceImp.findEntitiesLinksByNodeAndTripleStoreAndTripleObject(to, jobRegistry.isSearchLinks());

            ObjectResult objectResult = new ObjectResult(Origin.ASIO,State.OPEN,jobRegistry, similarityResult.getTripleObject(), null,null);
            ObjectResult toUpdate = objectResult;
            Set<ObjectResult> toDelete = new HashSet<>();
            Set<ObjectResult> toLink = new HashSet<>();
            for (EntitySimilarityObj eso : similarityResult.getAutomatic()) { // Para todos las similitudes automáticas
                ObjectResult objResAuto = new ObjectResult(Origin.ASIO,State.CLOSED,null, eso.getTripleObject(), eso.getSimilarity(), eso.getSimilarityWithoutId(dataBehaviour, true));
                objectResult.addAutomatic(objResAuto);
                // Merges
                if (objectResult.getNode().equals(objResAuto.getNode())
                        && objectResult.getTripleStore().equals(objResAuto.getTripleStore())) { // Si es el mismo nodo y triple store ACCIÓN = UPDATE o DELETE
                    ObjectResult toUpdateAux = new ObjectResult(Origin.ASIO,State.CLOSED,null, toUpdate.toTripleObject(jobRegistry).merge(objResAuto.toTripleObject(jobRegistry),hierarchies, cache, MergeType.FREE), eso.getSimilarity(), eso.getSimilarityWithoutId(dataBehaviour, true));
                    if (!toUpdateAux.getEntityId().equals(toUpdate.getEntityId())) {
                        toDelete.remove(toUpdateAux);
                        toDelete.add(toUpdate);
                        toUpdate = toUpdateAux;
                    } else {
                        toDelete.add(objResAuto);
                    }
                } else { // Si es distinto nodo
                    toLink.add(objResAuto);
                }
            }
            for (EntitySimilarityObj eso : similarityResult.getManual()) { // Para todos las similitudes manuales
                ObjectResult objResManual = new ObjectResult(Origin.ASIO,State.OPEN,null, eso.getTripleObject(), eso.getSimilarity(), eso.getSimilarityWithoutId(dataBehaviour, true));
                objectResult.addManual(objResManual);
            }
            objectResult.setStateFromChild();

            // Merges control
            if (!objectResult.getAutomatic().isEmpty()) {
                if (toUpdate != null) {
                    ActionResult actionResult = new ActionResult(Action.UPDATE, objectResult);
                    actionResult.addObjectResult(toUpdate);
                    objectResult.getActionResults().add(actionResult);
                    toUpdate.setActionResultParent(actionResult);
                }
                if (toDelete != null && !toDelete.isEmpty()) {
                    ActionResult actionResult = new ActionResult(Action.DELETE, objectResult);
                    for (ObjectResult orDelete : toDelete) {
                        if (Utils.isValidString(orDelete.getLocalURI())) {
                            actionResult.addObjectResult(orDelete);
                            orDelete.setActionResultParent(actionResult);
                        }
                    }
                    objectResult.getActionResults().add(actionResult);
                }

                if (toLink != null && !toLink.isEmpty()) {
                    ActionResult actionResult = new ActionResult(Action.LINK, objectResult);
                    for (ObjectResult orLink : toLink) {
                        actionResult.addObjectResult(orLink);
                        orLink.setActionResultParent(actionResult);
                    }
                    objectResult.getActionResults().add(actionResult);
                }
            }

            jobRegistry.getObjectResults().add(objectResult);
            objectResultRepository.saveAndFlush(objectResult);

            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.COMPLETED);
            jobRegistryRepository.saveAndFlush(jobRegistry);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Fail on findSimilaritiesByInstance: {}", e.getMessage());
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.FAIL);
            jobRegistryRepository.saveAndFlush(jobRegistry);
        }
        isWorking = false;
        handleQueueFindSimilarities();
        sendWebHooks(jobRegistry);
        if (jobRegistry.isPropagatedInKafka())
            propagueKafkaActions(jobRegistry);
        return jobRegistry;
    }

    /**
     * Manages the heavy request to search for similarities in the same class
     * @param jobRegistry
     * @return JobRegistry with the search completed
     */
    public JobRegistry findSimilaritiesByClass(JobRegistry jr) { // Aqui
        isWorking = true;
        // jr.setStarted(true);
        // jr.setStartedDate(new Date());
        JobRegistry jobRegistry = jr; // jobRegistryRepository.save(jr); // Add for front interface
        // JobRegistry jobRegistry = jobRegistryProxy.save(jr);
        try {
            Set<SimilarityResult> similarities = entitiesHandlerServiceImp.findEntitiesLinksByNodeAndTripleStoreAndClass(jobRegistry.getNode(), jobRegistry.getTripleStore(), jobRegistry.getClassName(), jobRegistry.isSearchLinks(), jobRegistry.getSearchFromDelta());
            for (SimilarityResult similarityResult : similarities) { // Por cada similitud encontrada
                TripleObject toHead = cache.getTripleObject(similarityResult.getTripleObject());
                ObjectResult objectResult = new ObjectResult(Origin.ASIO,State.OPEN,jobRegistry, toHead, null,null);
                ObjectResult toUpdate = objectResult;
                Set<ObjectResult> toDelete = new HashSet<>();
                Set<ObjectResult> toLink = new HashSet<>();
                for (EntitySimilarityObj eso : similarityResult.getAutomatic()) { // Para todos las similitudes automáticas
                    TripleObject toOther = cache.getTripleObject(eso.getTripleObject());
                    ObjectResult objResAuto = new ObjectResult(Origin.ASIO,State.CLOSED,null, toOther, eso.getSimilarity(), eso.getSimilarityWithoutId(dataBehaviour, true));
                    objectResult.addAutomatic(objResAuto); // Añadimos objResult
                    // Merges
                    if (objectResult.getNode().equals(objResAuto.getNode())
                            && objectResult.getTripleStore().equals(objResAuto.getTripleStore())) { // Si es el mismo nodo y triple store ACCIÓN = UPDATE o DELETE
                        ObjectResult toUpdateAux = new ObjectResult(Origin.ASIO,State.CLOSED,null, toUpdate.toTripleObject(jobRegistry).merge(objResAuto.toTripleObject(jobRegistry),hierarchies, cache, MergeType.FREE), eso.getSimilarity(), eso.getSimilarityWithoutId(dataBehaviour, true));
                        if (!toUpdateAux.getEntityId().equals(toUpdate.getEntityId())) { // Si cambio el orden después del merge
                            toDelete.remove(toUpdateAux);
                            toDelete.add(toUpdate);
                        } else { // Si no cambio
                            toDelete.add(objResAuto);
                        }
                        toUpdate = toUpdateAux;
                    } else { // Si es distinto nodo
                        toLink.add(objResAuto);
                    }

                }
                for (EntitySimilarityObj eso : similarityResult.getManual()) { // Para todos las similitudes manuales
                    ObjectResult objResManual = new ObjectResult(Origin.ASIO,State.OPEN,null, eso.getTripleObject(), eso.getSimilarity(), eso.getSimilarityWithoutId(dataBehaviour, true));
                    objectResult.addManual(objResManual);
                }

                objectResult.setStateFromChild();
                // Merges control
                ActionResult actionResultUpdate = new ActionResult(Action.UPDATE, objectResult);
                if (!objectResult.getAutomatic().isEmpty()) {
                    if (toUpdate != null) {
                        actionResultUpdate.addObjectResult(toUpdate);
                        objectResult.getActionResults().add(actionResultUpdate);
                        toUpdate.setActionResultParent(actionResultUpdate);
                    }

                    if (toDelete != null && !toDelete.isEmpty()) {
                        ActionResult actionResult = new ActionResult(Action.DELETE, objectResult);
                        for (ObjectResult orDelete : toDelete) {
                            actionResult.addObjectResult(orDelete);
                            orDelete.setActionResultParent(actionResult);
                            Map<String, org.javatuples.Pair<String,TripleObject>> dependencies = cache.getLinksToTripleObject(orDelete.toTripleObject(jobRegistry));
                            if (dependencies.size()>0) { // Para los borrados hay que mover los enlaces
                                for (Map.Entry<String, org.javatuples.Pair<String,TripleObject>> dep :dependencies.entrySet()) {
                                    TripleObject toUpdateLink = dep.getValue().getValue1();
                                    String key = dep.getValue().getValue0();
                                    String toUpdateCanonicalURI = toUpdate.getCanonicalURI();
                                    String toRemoveCanonicalURI = orDelete.getCanonicalURI();
                                    toUpdateLink.replaceAttValue(key,toRemoveCanonicalURI,toUpdateCanonicalURI); // Cambio los enlaces
                                    // Creo el objectResult para actualizaciones de dependencias
                                    ObjectResult orUpdateLink = new ObjectResult(Origin.ASIO,State.CLOSED,jobRegistry, toUpdateLink, null,null);
                                    actionResultUpdate.addObjectResult(orUpdateLink);
                                    orUpdateLink.setActionResultParent(actionResultUpdate);
                                    /*
                                    ObjectResult orUpdateLink = new ObjectResult(Origin.ASIO,State.CLOSED,jobRegistry, toUpdateLink, null,null);
                                    ActionResult actionResultUpdateLink = new ActionResult(Action.UPDATE, objectResult);
                                    actionResultUpdateLink.addObjectResult(orUpdateLink);
                                    objectResult.getActionResults().add(actionResultUpdateLink);
                                    orUpdateLink.setActionResultParent(actionResultUpdateLink);
                                    if (!actionResultUpdateLink.getObjectResults().isEmpty())
                                        objectResult.getActionResults().add(actionResultUpdateLink);
                                     */
                                }
                            }
                        }
                        if (!actionResult.getObjectResults().isEmpty())
                            objectResult.getActionResults().add(actionResult);
                    }

                    if (toLink != null && !toLink.isEmpty()) {
                        ActionResult actionResult = new ActionResult(Action.LINK, objectResult);
                        for (ObjectResult orLink : toLink) {
                            actionResult.addObjectResult(orLink);
                            orLink.setActionResultParent(actionResult);
                        }
                        objectResult.getActionResults().add(actionResult);
                    }
                }

                jobRegistry.getObjectResults().add(objectResult);
                try {
                    objectResultRepository.save(objectResult);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.COMPLETED);
            try {
                jobRegistryRepository.save(jobRegistry);
                // jobRegistryProxy.save(jobRegistry);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Fail on findSimilaritiesByClass: {}", e.getMessage());
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.FAIL);
            try {
                // jobRegistryRepository.saveAndFlush(jobRegistry);
                jobRegistryProxy.save(jobRegistry);
            } catch (Exception e2) {
                logger.error(e2.getMessage());
            }
        }
        isWorking = false;
        handleQueueFindSimilarities();
        sendWebHooks(jobRegistry);
        if (jobRegistry.isPropagatedInKafka())
            propagueKafkaActions(jobRegistry); // Propagar acciones en kafka
        return jobRegistry;
    }

    /**
     * Manages the heavy request to search for similarities by class in the LOD Cloud
     * @param jobRegistry
     * @return JobRegistry with the search completed
     */
    public JobRegistry findSimilaritiesInLod(JobRegistry jobRegistry) {
        isWorking = true;
        jobRegistry.setStarted(true);
        jobRegistry.setStartedDate(new Date());
        jobRegistryRepository.saveAndFlush(jobRegistry); // Add for front interface
        try {
            Set<SimilarityResult> similarities = entitiesHandlerServiceImp.findEntitiesLinksByNodeAndTripleStoreAndClassInLOD(jobRegistry.getDataSource(),jobRegistry.getNode(), jobRegistry.getTripleStore(), jobRegistry.getClassName(), jobRegistry.getSearchFromDelta());
            for (SimilarityResult similarityResult : similarities) { // Por cada similitud encontrada

                ObjectResult objectResult = new ObjectResult(Origin.ASIO, State.OPEN,jobRegistry, similarityResult.getTripleObject(), null,null);
                for (EntitySimilarityObj eso : similarityResult.getAutomatic()) { // Para todos las similitudes automáticas
                    try {
                        ObjectResult objResAuto = new ObjectResult(Origin.LOD, State.CLOSED,null, eso.getTripleObject(), eso.getSimilarity(), eso.getSimilarityWithoutId(dataBehaviour, true));
                        objectResult.addAutomatic(objResAuto);

                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }


                }
                for (EntitySimilarityObj eso : similarityResult.getManual()) { // Para todos las similitudes automáticas
                    ObjectResult objResManual = new ObjectResult(Origin.ASIO, State.OPEN,null, eso.getTripleObject(), eso.getSimilarity(), eso.getSimilarityWithoutId(dataBehaviour, true));
                    objectResult.addManual(objResManual);
                }

                objectResult.setStateFromChild();


                Set<ObjectResult> toLink = new HashSet<>();

                for (EntitySimilarityObj eso : similarityResult.getAutomatic()) {
                    TripleObject tripleObjectAutomatic = eso.getTripleObject();
                    // Creo los contenedores si no existen
                    if (trellisCache.find("lod-links",Constants.CACHE_TRELLIS_CONTAINER) == null) {
                        String containerLod = trellisOperations.saveContainer(null,"lod-links",false);
                        if (containerLod!=null) {
                            trellisCache.saveInCache("lod-links",containerLod,Constants.CACHE_TRELLIS_CONTAINER);
                        }
                    }
                    String pathContainer = "lod-links";
                    if (trellisCache.find("lod-links/" + eso.getDataSource(),Constants.CACHE_TRELLIS_CONTAINER) == null) {
                        String containerLodDataSource = trellisOperations.saveContainer(pathContainer,eso.getDataSource(),false);
                        if (containerLodDataSource!=null) {
                            trellisCache.saveInCache("lod-links/" + eso.getDataSource(),containerLodDataSource,Constants.CACHE_TRELLIS_CONTAINER);
                        }
                    }
                    pathContainer = String.format("%s/%s", pathContainer, eso.getDataSource());
                    if (trellisCache.find(pathContainer + "/"+ tripleObjectAutomatic.getClassName(),Constants.CACHE_TRELLIS_CONTAINER) == null) {
                        String containerLodDataSourceClass = trellisOperations.saveContainer(pathContainer,tripleObjectAutomatic.getClassName(),false);
                        if (containerLodDataSourceClass!=null) {
                            trellisCache.saveInCache(pathContainer + "/"+ tripleObjectAutomatic.getClassName(),containerLodDataSourceClass,Constants.CACHE_TRELLIS_CONTAINER);
                        }
                    }
                    pathContainer = String.format("%s/%s", pathContainer, tripleObjectAutomatic.getClassName());
                    // Inserto el elemento a linkar

                    List<Pair<String,String>> properties = new ArrayList<>();
                    properties.add(new MutablePair<>("skos:closeMatch",similarityResult.getTripleObject().getLocalURI()));
                    for (TripleObjectLink tripleObjectLink : tripleObjectAutomatic.getTripleObjectLink()) { // AQUI
                        String locationTripleObjectAutomatic = trellisOperations.addPropertyToEntity(pathContainer, tripleObjectLink, properties, false);
                        if (locationTripleObjectAutomatic != null) {
                            // objectResult.setLocalURI(locationTripleObjectAutomatic);
                            tripleObjectAutomatic.setLocalURI(locationTripleObjectAutomatic);
                            ObjectResult objectResultLink = new ObjectResult(Origin.LOD, State.CLOSED,jobRegistry, tripleObjectAutomatic, null,null);
                            toLink.add(objectResultLink);
                        }
                    }
                }

                if (toLink != null && !toLink.isEmpty()) {
                    ActionResult actionResult = new ActionResult(Action.LINK, objectResult);
                    for (ObjectResult orLink : toLink) {
                        actionResult.addObjectResult(orLink);
                        orLink.setActionResultParent(actionResult);
                    }
                    objectResult.getActionResults().add(actionResult);
                    jobRegistry.getObjectResults().add(objectResult);
                    objectResultRepository.saveAndFlush(objectResult);
                }


                // Write object In Triple Store


                jobRegistry.getObjectResults().add(objectResult);
                objectResultRepository.saveAndFlush(objectResult);
            }
/*            if (similarities.size()>0 && false) {
                for (SimilarityResult sr : similarities) {
                    ObjectResult objectResult = new ObjectResult(jobRegistry, sr.getTripleObject(), null);
                    Set<ObjectResult> toLink = new HashSet<>();
                    TripleObjectLink baseTripleObjectLink = new TripleObjectLink(sr.getTripleObject().toJson()); // Object base to link


                    for (EntitySimilarityObj eso : sr.getAutomatic()) {
                        TripleObject tripleObjectAutomatic = eso.getTripleObject();
                        ObjectResult objectResultInner = new ObjectResult(jobRegistry, tripleObjectAutomatic, null);
                        // Creo los contenedores si no existen
                        if (trellisCache.find("lod-links",Constants.CACHE_TRELLIS_CONTAINER) == null) {
                            String containerLod = trellisOperations.saveContainer(null,"lod-links",false);
                            if (containerLod!=null) {
                                trellisCache.saveInCache("lod-links",containerLod,Constants.CACHE_TRELLIS_CONTAINER);
                            }
                        }
                        String pathContainer = "lod-links";
                        if (trellisCache.find("lod-links/" + eso.getDataSource(),Constants.CACHE_TRELLIS_CONTAINER) == null) {
                            String containerLodDataSource = trellisOperations.saveContainer(pathContainer,eso.getDataSource(),false);
                            if (containerLodDataSource!=null) {
                                trellisCache.saveInCache("lod-links/" + eso.getDataSource(),containerLodDataSource,Constants.CACHE_TRELLIS_CONTAINER);
                            }
                        }
                        pathContainer = pathContainer + "/" +eso.getDataSource();
                        if (trellisCache.find(pathContainer + "/"+ tripleObjectAutomatic.getClassName(),Constants.CACHE_TRELLIS_CONTAINER) == null) {
                            String containerLodDataSourceClass = trellisOperations.saveContainer(pathContainer,tripleObjectAutomatic.getClassName(),false);
                            if (containerLodDataSourceClass!=null) {
                                trellisCache.saveInCache(pathContainer + "/"+ tripleObjectAutomatic.getClassName(),containerLodDataSourceClass,Constants.CACHE_TRELLIS_CONTAINER);
                            }
                        }
                        pathContainer = pathContainer + "/" + tripleObjectAutomatic.getClassName();
                        // Inserto el elemento a linkar

                        List<Pair<String,String>> properties = new ArrayList<>();
                        properties.add(new MutablePair<>("skos:closeMatch",sr.getTripleObject().getLocalURI()));
                        String locationTripleObjectAutomatic = trellisOperations.addPropertyToEntity(pathContainer,tripleObjectAutomatic.getTripleObjectLink(),properties,false);
                        if (locationTripleObjectAutomatic!=null) {
                            objectResultInner.setLocalURI(locationTripleObjectAutomatic);

                            toLink.add(objectResultInner);
                        }
                    }

                    if (toLink != null && !toLink.isEmpty()) {
                        ActionResult actionResult = new ActionResult(Action.LINK, objectResult);
                        for (ObjectResult orLink : toLink) {
                            actionResult.addObjectResult(orLink);
                            orLink.setActionResultParent(actionResult);
                        }
                        objectResult.getActionResults().add(actionResult);
                        jobRegistry.getObjectResults().add(objectResult);
                        objectResultRepository.save(objectResult);
                    }

                    // Inserto el elemento Base
*//*                    if (links.size()>0) {

                        List<Pair<String, String>> properties = new ArrayList<>();
                        for (String link : links) {
                            properties.add(new MutablePair<>("skos:closeMatch", link));
                        }
                        String locationTripleObject = trellisOperations.addPropertyToEntity(sr.getTripleObject().getLocalURI(), sr.getTripleObject().getTripleObjectLink(), properties, true);
                        System.out.println();
                    }*//*
                }
            }*/
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.COMPLETED);
            jobRegistryRepository.saveAndFlush(jobRegistry);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Fail on findSimilaritiesByClass: {}", e.getMessage());
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.FAIL);
            jobRegistryRepository.saveAndFlush(jobRegistry);
        }
        isWorking = false;
        handleQueueFindSimilarities();
        sendWebHooks(jobRegistry);
        if (jobRegistry.isPropagatedInKafka())
            propagueKafkaActions(jobRegistry);
        return jobRegistry;
    }

    /**
     * Handle the queues of Jobs
     * @return CompletableFuture<JobRegistry>
     */
    public CompletableFuture<JobRegistry> handleQueueFindSimilarities() {
        // isWorking = false;
        if (isAppReady && !isWorking) {
            if (!qClasses.isEmpty()) {
                isWorking = true;
                return CompletableFuture.supplyAsync(() -> findSimilaritiesByClass(qClasses.poll()));
            } else if (!qInstances.isEmpty()) {
                isWorking = true;
                return CompletableFuture.supplyAsync(() -> findSimilaritiesByInstance(qInstances.poll()));
            } else if (!qLod.isEmpty()) {
                isWorking = true;
                return CompletableFuture.supplyAsync(() -> findSimilaritiesInLod(qLod.poll()));
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Handle send call backs in Web Hooks defined  for a jobRegistry
     * @param jobRegistry
     */
    private void sendWebHooks(JobRegistry jobRegistry) {
        Set<String> webHooks = jobRegistry.getWebHooks();
        HttpClient client = HttpClient.newHttpClient();
        JsonObject jResponse = jobRegistry.toSimplifiedJson();
        for (String webHook : webHooks) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webHook))
                    .POST(HttpRequest.BodyPublishers.ofString(jResponse.toString()))
                    .header("content-type", "application/json")
                    .build();
            try {
                logger.info("Send POST Callback at URL: {}", webHook);
                HttpResponse<String> response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());
                String body = response.body();
                logger.info("Response Callback: {}", body);
            } catch (InterruptedException e) {
                logger.error("Error InterruptedException in callback at URL: {}", webHook);
            } catch (IOException e) {
                logger.error("Error IOException in callback at URL: {}", webHook);
            }
        }
    }

    /**
     * Handle send the responses in Kafka for a jobRegistry
     * @param jobRegistry
     */
    private void propagueKafkaActions(JobRegistry jobRegistry) {
        for (ObjectResult or : jobRegistry.getObjectResults()) { // Por todas las acciones
            for (ActionResult ar : or.getActionResults()) { // Por todos las Acciones
                kafkaHandlerService.sendMessageAction(ar, or.getRecursiveJobRegistry().getNode(), or.getRecursiveJobRegistry().getTripleStore(), or.getClassName());
            }
        }
    }

    public void propagueKafkaActions(ObjectResult or) {
        for (ActionResult ar : or.getActionResults()) { // Por todos las Acciones
            kafkaHandlerService.sendMessageAction(ar, or.getRecursiveJobRegistry().getNode(), or.getRecursiveJobRegistry().getTripleStore(), or.getClassName());
        }
    }

    public void propagueKafkaActions(Map<ObjectResult,List<ActionResult>> toPropague) {
        for (Map.Entry<ObjectResult, List<ActionResult>> tpEntry : toPropague.entrySet()) { // Por todos las Acciones
            for (ActionResult ar : tpEntry.getValue()) {
                kafkaHandlerService.sendMessageAction(ar, tpEntry.getKey().getRecursiveJobRegistry().getNode(), tpEntry.getKey().getRecursiveJobRegistry().getTripleStore(), tpEntry.getKey().getClassName());
            }
        }
    }
}
