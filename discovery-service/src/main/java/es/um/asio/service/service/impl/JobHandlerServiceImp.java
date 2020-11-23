package es.um.asio.service.service.impl;

import com.google.api.client.util.Charsets;
import com.google.gson.JsonObject;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

@Service
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class JobHandlerServiceImp {

    private final Logger logger = LoggerFactory.getLogger(JobHandlerServiceImp.class);

    Map<String, Map<String,Map<String,Map<String,JobRegistry>>>> jrClassMap;
    Map<String, Map<String,Map<String,Map<String,JobRegistry>>>> jrEntityMap;
    Queue<JobRegistry> qClasses;
    Queue<JobRegistry> qEntities;
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
        qEntities = new LinkedList<>();
        isWorking = false;
        isAppReady = false;
        applicationState.addAppListener(new AppEvents() {
            @Override
            public void onCachedDataIsReady() {

            }

            @Override
            public void onRealDataIsReady() {

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
            boolean propagueInKafka) {
        if (!jrClassMap.containsKey(node))
            jrClassMap.put(node,new LinkedHashMap<>());
        if (!jrClassMap.get(node).containsKey(tripleStore))
            jrClassMap.get(node).put(tripleStore,new LinkedHashMap<>());
        if (!jrClassMap.get(node).get(tripleStore).containsKey(className))
            jrClassMap.get(node).get(tripleStore).put(className,new LinkedHashMap<>());

        // Busco si existe un JobRegistry anterior
        JobRegistry jobRegistry = null;
        boolean isNewJob = false;
        for (Map.Entry<String, JobRegistry> jrClassEntry: jrClassMap.get(node).get(tripleStore).get(className).entrySet()) {
            if (!jrClassEntry.getValue().isCompleted() && (jobRegistry==null || jrClassEntry.getValue().getMaxRequestDate().after(jobRegistry.getMaxRequestDate()))) {
                jobRegistry = jrClassEntry.getValue();
            }
        }
        if (jobRegistry==null) { // Si no existe lo creo
            jobRegistry = new JobRegistry(application,node,tripleStore,className);
            isNewJob = true;
        }
        jobRegistry.setDoSync(doSync);
        RequestRegistry requestRegistry;
        Optional<RequestRegistry> requestRegistryOpt = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId,requestCode, RequestType.ENTITY_LINK_CLASS);
        if (requestRegistryOpt.isEmpty()) {
            requestRegistry = new RequestRegistry(userId,requestCode, RequestType.ENTITY_LINK_CLASS,new Date());
        } else {
            requestRegistry = requestRegistryOpt.get();
        }
        requestRegistry.setWebHook(webHook);
        requestRegistry.setPropagueInKafka(propagueInKafka);
        requestRegistryProxy.save(requestRegistry);

        jobRegistry.addRequestRegistry(requestRegistry);
        jrClassMap.get(node).get(tripleStore).get(className).put(String.valueOf(requestRegistry.hashCode()),jobRegistry);

        jobRegistryRepository.save(jobRegistry);
        for (RequestRegistry rr :jobRegistry.getRequestRegistries()) {
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


    public JobRegistry addJobRegistryForEntity(DiscoveryApplication application,String requestCode,String node, String tripleStore, String className, String body) {
        return null;
    }

    // Gestiona la petición pesada de búsqueda de similaridades en una misma clase
    public JobRegistry findSimilaritiesByClass(JobRegistry jobRegistry) {
        jobRegistry.setStarted(true);
        jobRegistry.setStartedDate(new Date());
        isWorking = true;
        try {
            Set<SimilarityResult> similarities = entitiesHandlerServiceImp.findEntitiesLinksByNodeAndTripleStoreAndClass(jobRegistry.getNode(), jobRegistry.getTripleStore(), jobRegistry.getClassName());
            for (SimilarityResult similarityResult : similarities) { // Por cada similitud encontrada
                ObjectResult objectResult = new ObjectResult(jobRegistry, similarityResult.getTripleObject(), null);
                ObjectResult toUpdate = objectResult;
                Set<ObjectResult> toDelete = new HashSet<>();
                for (EntitySimilarityObj eso : similarityResult.getAutomatic()) { // Para todos las similitudes automáticas
                    ObjectResult objResAuto = new ObjectResult(null, eso.getTripleObject(), eso.getSimilarity());
                    objectResult.addAutomatic(objResAuto);
                    // Merges
                    TripleObject to1 = toUpdate.toTripleObject(jobRegistry);
                    TripleObject to2 = objResAuto.toTripleObject(jobRegistry);
                    TripleObject to3 = to1.merge(to2);

                    ObjectResult toUpdateAux = new ObjectResult(null,toUpdate.toTripleObject(jobRegistry).merge(objResAuto.toTripleObject(jobRegistry)),eso.similarity);
                    if (!toUpdateAux.getEntityId().equals(toUpdate.getEntityId())) {
                        toDelete.remove(toUpdateAux);
                        toDelete.add(toUpdate);
                        toUpdate = toUpdateAux;
                    } else {
                        toDelete.add(objResAuto);
                    }
                }
                for (EntitySimilarityObj eso : similarityResult.getManual()) { // Para todos las similitudes automáticas
                    ObjectResult objResManual = new ObjectResult(null, eso.getTripleObject(), eso.getSimilarity());
                    objectResult.addManual(objResManual);
                }

                // Merges control
                if (objectResult.getAutomatic().size()>0) {
                    if (toUpdate!=null) {
                        ActionResult actionResult = new ActionResult(Action.UPDATE,objectResult);
                        actionResult.addObjectResult(toUpdate);
                        objectResult.getActionResults().add(actionResult);
                        toUpdate.setActionResultParent(actionResult);
                    }
                    if (toDelete!=null && !toDelete.isEmpty()) {
                        ActionResult actionResult = new ActionResult(Action.DELETE,objectResult);
                        for (ObjectResult orDelete : toDelete) {
                            actionResult.addObjectResult(orDelete);
                            orDelete.setActionResultParent(actionResult);
                        }
                        // actionResultRepository.save(actionResult);
                        objectResult.getActionResults().add(actionResult);
                    }
                }

                //objectResultRepository.save(objectResult);
                jobRegistry.getObjectResults().add(objectResult);
                objectResultRepository.save(objectResult);
            }
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.COMPLETED);
            jobRegistryRepository.save(jobRegistry);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Fail on findSimilaritiesByClass: "+e.getMessage());
            e.printStackTrace();
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
                return CompletableFuture.supplyAsync(()->findSimilaritiesByClass(qClasses.poll()));
            } else if (!qEntities.isEmpty()) {
                return CompletableFuture.supplyAsync(()->findSimilaritiesByClass(qEntities.poll()));
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
                    .header("content-type","application/json")
                    .build();
            try {
                logger.info("Send POST Callback at URL: "+webHook);
                HttpResponse<String> response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());
                logger.info("Response Callback: "+ response.body());
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Error in callback at URL: "+webHook);
            }
        }
    }

    private void propagueKafkaActions(JobRegistry jobRegistry) {
        for (ObjectResult or : jobRegistry.getObjectResults()) { // Por todas las acciones
            for (ActionResult ar : or.getActionResults()) { // Por todos las Acciones
                kafkaHandlerService.sendMessageAction(ar);
            }
        }
    }
}
