package es.um.asio.service.service.impl;

import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import es.um.asio.service.listener.AppEvents;
import es.um.asio.service.model.SimilarityResult;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.relational.*;
import es.um.asio.service.proxy.RequestRegistryProxy;
import es.um.asio.service.repository.relational.JobRegistryRepository;
import es.um.asio.service.repository.relational.ObjectResultRepository;
import es.um.asio.service.repository.relational.RequestRegistryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    ApplicationState applicationState;

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
                if (!isWorking && isAppReady) {
                    if (!qClasses.isEmpty()) {
                        CompletableFuture<CompletableFuture<JobRegistry>> future = CompletableFuture.supplyAsync(()->findSimilaritiesByClass(qClasses.poll()));
                    } else if (!qEntities.isEmpty()) {
                        CompletableFuture<CompletableFuture<JobRegistry>> future = CompletableFuture.supplyAsync(()->findSimilaritiesByClass(qEntities.poll()));

                    }
                }
            }
        });

    }

    //@Override
    public JobRegistry addJobRegistryForClass(
            DiscoveryApplication application,
            String userId, String requestCode,
            String node,
            String tripleStore,
            String className) {
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
        RequestRegistry requestRegistry;
        Optional<RequestRegistry> requestRegistryOpt = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId,requestCode, RequestType.ENTITY_LINK_CLASS);
        if (requestRegistryOpt.isEmpty()) {
            requestRegistry = new RequestRegistry(userId,requestCode, RequestType.ENTITY_LINK_CLASS,new Date());
        } else {
            requestRegistry = requestRegistryOpt.get();
        }

        jobRegistry.addRequestRegistry(requestRegistry);
        jrClassMap.get(node).get(tripleStore).get(className).put(String.valueOf(requestRegistry.hashCode()),jobRegistry);
        if (isNewJob) {
            qClasses.add(jobRegistry);
        }
        jobRegistryRepository.save(jobRegistry);
        for (RequestRegistry rr :jobRegistry.getRequestRegistries()) {
            requestRegistryProxy.save(rr);
        }
        if (!isWorking && isAppReady) {
            if (!qClasses.isEmpty()) {
                CompletableFuture<CompletableFuture<JobRegistry>> future = CompletableFuture.supplyAsync(()->findSimilaritiesByClass(qClasses.poll()));
            } else if (!qEntities.isEmpty()) {
                CompletableFuture<CompletableFuture<JobRegistry>> future = CompletableFuture.supplyAsync(()->findSimilaritiesByClass(qEntities.poll()));

            }
        }
        return jobRegistry;
    }


    public JobRegistry addJobRegistryForEntity(DiscoveryApplication application,String requestCode,String node, String tripleStore, String className, String body) {
        return null;
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<JobRegistry> findSimilaritiesByClass(JobRegistry jobRegistry) {
        try {
            Set<SimilarityResult> similarities = entitiesHandlerServiceImp.findEntitiesLinksByNodeAndTripleStoreAndClass(jobRegistry.getNode(), jobRegistry.getTripleStore(), jobRegistry.getClassName());
            for (SimilarityResult similarityResult : similarities) { // Por cada similitud encontrada
                ObjectResult objectResult = new ObjectResult(jobRegistry, similarityResult.getTripleObject(), null);
                for (EntitySimilarityObj eso : similarityResult.getAutomatic()) { // Para todos las similitudes automáticas
                    ObjectResult objResAuto = new ObjectResult(null, eso.getTripleObject(), eso.getSimilarity());
                    objectResult.addAutomatic(objResAuto);
                }
                for (EntitySimilarityObj eso : similarityResult.getManual()) { // Para todos las similitudes automáticas
                    ObjectResult objResManual = new ObjectResult(null, eso.getTripleObject(), eso.getSimilarity());
                    objectResult.addManual(objResManual);
                }
                //objectResultRepository.save(objectResult);
                jobRegistry.getObjectResults().add(objectResult);
            }
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.COMPLETED);
            jobRegistryRepository.save(jobRegistry);
        } catch (Exception e) {
            logger.error("Fail on findSimilaritiesByClass: "+e.getMessage());
            e.printStackTrace();
            jobRegistry.setCompleted(true);
            jobRegistry.setCompletedDate(new Date());
            jobRegistry.setStatusResult(StatusResult.FAIL);
            jobRegistryRepository.save(jobRegistry);
        }
        isWorking = false;
        if (isAppReady) {
            if (!qClasses.isEmpty()) {
                CompletableFuture<CompletableFuture<JobRegistry>> future = CompletableFuture.supplyAsync(()->findSimilaritiesByClass(qClasses.poll()));
            } else if (!qEntities.isEmpty()) {
                CompletableFuture<CompletableFuture<JobRegistry>> future = CompletableFuture.supplyAsync(()->findSimilaritiesByClass(qEntities.poll()));

            }
        }
        return CompletableFuture.completedFuture(null);
    }
}
