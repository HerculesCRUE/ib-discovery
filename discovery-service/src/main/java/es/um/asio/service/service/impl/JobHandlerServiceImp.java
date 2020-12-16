package es.um.asio.service.service.impl;

import com.google.gson.JsonObject;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import es.um.asio.service.exceptions.CustomDiscoveryException;
import es.um.asio.service.listener.AppEvents;
import es.um.asio.service.model.SimilarityResult;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.relational.*;
import es.um.asio.service.proxy.RequestRegistryProxy;
import es.um.asio.service.repository.relational.ActionResultRepository;
import es.um.asio.service.repository.relational.JobRegistryRepository;
import es.um.asio.service.repository.relational.ObjectResultRepository;
import es.um.asio.service.repository.relational.RequestRegistryRepository;
import es.um.asio.service.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class JobHandlerServiceImp {

    private final Logger logger = LoggerFactory.getLogger(JobHandlerServiceImp.class);

    Map<String, Map<String, Map<String, Map<String, JobRegistry>>>> jrClassMap;
    Map<String, Map<String, Map<String, Map<String, JobRegistry>>>> jrEntityMap;
    Queue<JobRegistry> qClasses;
    Queue<JobRegistry> qInstances;
    boolean isWorking;
    boolean isAppReady;

    @Autowired
    RequestRegistryProxy requestRegistryProxy;

    @Autowired
    RequestRegistryRepository requestRegistryRepository;

    @Autowired
    JobRegistryRepository jobRegistryRepository;

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


    @PostConstruct
    public void init() {
        jrClassMap = new LinkedHashMap<>();
        jrEntityMap = new LinkedHashMap<>();
        qClasses = new LinkedList<>();
        qInstances = new LinkedList<>();
        isWorking = false;
        isAppReady = false;
        applicationState.addAppListener(new AppEvents() {
            /*
             * This method not is user
             */
            @Override
            public void onCachedDataIsReady() {
                logger.info("onCachedDataIsReady");
            }

            @Override
            public void onRealDataIsReady() {
                logger.info("onRealDataIsReady");
            }

            @Override
            public void onElasticSearchIsReady() {
                isAppReady = true;
                handleQueueFindSimilarities();
            }
        });

    }

    //@Override
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
        RequestRegistry requestRegistry;
        Optional<RequestRegistry> requestRegistryOpt = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, RequestType.ENTITY_LINK_CLASS);
        if (requestRegistryOpt.isEmpty()) {
            requestRegistry = new RequestRegistry(userId, requestCode, RequestType.ENTITY_LINK_CLASS, new Date());
        } else {
            requestRegistry = requestRegistryOpt.get();
        }
        requestRegistry.setWebHook(webHook);
        requestRegistry.setPropagueInKafka(propagueInKafka);
        if (Utils.isValidString(jobRegistry.getId())) {
            requestRegistry.setJobRegistry(jobRegistry);
            requestRegistryProxy.save(requestRegistry);
        }

        jobRegistry.addRequestRegistry(requestRegistry);
        jrClassMap.get(node).get(tripleStore).get(className).put(String.valueOf(requestRegistry.hashCode()), jobRegistry);

        jobRegistryRepository.save(jobRegistry);
        for (RequestRegistry rr : jobRegistry.getRequestRegistries()) {
            requestRegistryProxy.save(rr);
        }
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
        Optional<RequestRegistry> requestRegistryOpt = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, RequestType.ENTITY_LINK_INSTANCE);
        if (requestRegistryOpt.isEmpty()) {
            requestRegistry = new RequestRegistry(userId, requestCode, RequestType.ENTITY_LINK_INSTANCE, new Date());
        } else {
            requestRegistry = requestRegistryOpt.get();
        }
        requestRegistry.setWebHook(webHook);
        requestRegistry.setPropagueInKafka(propagueInKafka);
        requestRegistry.setJobRegistry(jobRegistry);
        if (Utils.isValidString(jobRegistry.getId())) {
            requestRegistry.setJobRegistry(jobRegistry);
            // requestRegistryProxy.save(requestRegistry);
        }

        jobRegistry.addRequestRegistry(requestRegistry);
        jrEntityMap.get(node).get(tripleStore).get(className).put(String.valueOf(requestRegistry.hashCode()), jobRegistry);

        jobRegistryRepository.save(jobRegistry);
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

    // Gestiona la petición pesada de búsqueda de similaridades en una misma clase
    public JobRegistry findSimilaritiesByInstance(JobRegistry jobRegistry) {
        TripleObject to = jobRegistry.getTripleObject();
        if (to == null) {
            logger.error("Triple Object can´t be null");
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.FAIL);
            jobRegistryRepository.save(jobRegistry);
        }
        jobRegistry.setStarted(true);
        jobRegistry.setStartedDate(new Date());
        isWorking = true;
        try {
            SimilarityResult similarityResult = entitiesHandlerServiceImp.findEntitiesLinksByNodeAndTripleStoreAndTripleObject(to, jobRegistry.isSearchLinks());

            ObjectResult objectResult = new ObjectResult(jobRegistry, similarityResult.getTripleObject(), null);
            ObjectResult toUpdate = objectResult;
            Set<ObjectResult> toDelete = new HashSet<>();
            Set<ObjectResult> toLink = new HashSet<>();
            for (EntitySimilarityObj eso : similarityResult.getAutomatic()) { // Para todos las similitudes automáticas
                ObjectResult objResAuto = new ObjectResult(null, eso.getTripleObject(), eso.getSimilarity());
                objectResult.addAutomatic(objResAuto);
                // Merges
                if (objectResult.getNode().equals(objResAuto.getNode())
                        && objectResult.getTripleStore().equals(objResAuto.getTripleStore())) { // Si es el mismo nodo y triple store ACCIÓN = UPDATE o DELETE
                    ObjectResult toUpdateAux = new ObjectResult(null, toUpdate.toTripleObject(jobRegistry).merge(objResAuto.toTripleObject(jobRegistry)), eso.getSimilarity());
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
            for (EntitySimilarityObj eso : similarityResult.getManual()) { // Para todos las similitudes automáticas
                ObjectResult objResManual = new ObjectResult(null, eso.getTripleObject(), eso.getSimilarity());
                objectResult.addManual(objResManual);
            }

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
            objectResultRepository.save(objectResult);

            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.COMPLETED);
            jobRegistryRepository.save(jobRegistry);
        } catch (Exception e) {
            logger.error("Fail on findSimilaritiesByClass: {}", e.getMessage());
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.FAIL);
            jobRegistryRepository.save(jobRegistry);
        }
        isWorking = false;
        handleQueueFindSimilarities();
        sendWebHooks(jobRegistry);
        if (jobRegistry.isPropagatedInKafka())
            propagueKafkaActions(jobRegistry);
        return jobRegistry;
    }

    // Gestiona la petición pesada de búsqueda de similaridades en una misma clase
    public JobRegistry findSimilaritiesByClass(JobRegistry jobRegistry) {
        jobRegistry.setStarted(true);
        jobRegistry.setStartedDate(new Date());
        isWorking = true;
        try {
            Set<SimilarityResult> similarities = entitiesHandlerServiceImp.findEntitiesLinksByNodeAndTripleStoreAndClass(jobRegistry.getNode(), jobRegistry.getTripleStore(), jobRegistry.getClassName(), jobRegistry.isSearchLinks(), jobRegistry.getSearchFromDelta());
            for (SimilarityResult similarityResult : similarities) { // Por cada similitud encontrada
                ObjectResult objectResult = new ObjectResult(jobRegistry, similarityResult.getTripleObject(), null);
                ObjectResult toUpdate = objectResult;
                Set<ObjectResult> toDelete = new HashSet<>();
                Set<ObjectResult> toLink = new HashSet<>();
                for (EntitySimilarityObj eso : similarityResult.getAutomatic()) { // Para todos las similitudes automáticas
                    ObjectResult objResAuto = new ObjectResult(null, eso.getTripleObject(), eso.getSimilarity());
                    objectResult.addAutomatic(objResAuto);
                    // Merges
                    if (objectResult.getNode().equals(objResAuto.getNode())
                            && objectResult.getTripleStore().equals(objResAuto.getTripleStore())) { // Si es el mismo nodo y triple store ACCIÓN = UPDATE o DELETE
                        ObjectResult toUpdateAux = new ObjectResult(null, toUpdate.toTripleObject(jobRegistry).merge(objResAuto.toTripleObject(jobRegistry)), eso.getSimilarity());
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
                for (EntitySimilarityObj eso : similarityResult.getManual()) { // Para todos las similitudes automáticas
                    ObjectResult objResManual = new ObjectResult(null, eso.getTripleObject(), eso.getSimilarity());
                    objectResult.addManual(objResManual);
                }

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
                            actionResult.addObjectResult(orDelete);
                            orDelete.setActionResultParent(actionResult);
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
                objectResultRepository.save(objectResult);
            }
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.COMPLETED);
            jobRegistryRepository.save(jobRegistry);
        } catch (Exception e) {
            logger.error("Fail on findSimilaritiesByClass: {}", e.getMessage());
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.FAIL);
            jobRegistryRepository.save(jobRegistry);
        }
        isWorking = false;
        handleQueueFindSimilarities();
        sendWebHooks(jobRegistry);
        if (jobRegistry.isPropagatedInKafka())
            propagueKafkaActions(jobRegistry);
        return jobRegistry;
    }

    public CompletableFuture<JobRegistry> handleQueueFindSimilarities() {
        isWorking = false;
        if (isAppReady) {
            if (!qClasses.isEmpty()) {
                return CompletableFuture.supplyAsync(() -> findSimilaritiesByClass(qClasses.poll()));
            } else if (!qInstances.isEmpty()) {
                return CompletableFuture.supplyAsync(() -> findSimilaritiesByClass(qInstances.poll()));
            }
        }
        return CompletableFuture.completedFuture(null);
    }

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
                logger.info("Response Callback: {}", response.body());
            } catch (Exception e) {
                logger.error("Error in callback at URL: {}", webHook);
            }
        }
    }

    private void propagueKafkaActions(JobRegistry jobRegistry) {
        for (ObjectResult or : jobRegistry.getObjectResults()) { // Por todas las acciones
            for (ActionResult ar : or.getActionResults()) { // Por todos las Acciones
                kafkaHandlerService.sendMessageAction(ar, or.getRecursiveJobRegistry().getNode(), or.getRecursiveJobRegistry().getTripleStore(), or.getClassName());
            }
        }
    }
}
