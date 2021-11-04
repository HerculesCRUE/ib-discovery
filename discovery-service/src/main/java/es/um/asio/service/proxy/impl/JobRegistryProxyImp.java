package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.*;
import es.um.asio.service.proxy.*;
import es.um.asio.service.repository.relational.JobRegistryRepository;
import es.um.asio.service.repository.relational.custom.JobRegistryCustomRepository;
import es.um.asio.service.repository.relational.custom.imp.JobRegistryCustomRepositoryImp;
import es.um.asio.service.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.util.*;

/**
 * Proxy for JobRegistryProxyImp repository implementation.
 * @implNote JobRegistryProxyImp
 * @see JobRegistry
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public class JobRegistryProxyImp implements JobRegistryProxy {

    private final Logger logger = LoggerFactory.getLogger(JobRegistryProxyImp.class);

    @Autowired
    JobRegistryRepository jobRegistryRepository;

    @Autowired
    RequestRegistryProxy requestRegistryProxy;

    @Autowired
    ObjectResultProxy objectResultProxy;

    @Autowired
    DiscoveryApplicationProxy discoveryApplicationProxy;

    @Autowired
    JobRegistryCustomRepository jobRegistryCustomRepository;

    @Autowired
    ActionResultProxy actionResultProxy;


    @Override
    public JobRegistry save(JobRegistry jra) throws CloneNotSupportedException {
        String id = jobRegistryCustomRepository.persist(jra,true);
        jra.setId(id);
        return jra;
    }

    @Override
    public JobRegistry saveRequests(JobRegistry jr) throws CloneNotSupportedException {
        /*
        if (jr.getDiscoveryApplication()!=null) {
            discoveryApplicationProxy.save(jr.getDiscoveryApplication());
        }

        String id = null;
        boolean isValidUUID = false;
        while (!Utils.isValidString(id) || !isValidUUID ) {
            id = UUID.randomUUID().toString().replaceAll("-","");
            isValidUUID = findById(id).isEmpty();
        }
        jr.setId(id);
        jobRegistryRepository.insertNoNested(
                jr.getId(),
                jr.getVersion(),
                jr.getDiscoveryApplication().getId(),
                jr.getNode(),
                jr.getTripleStore(),
                jr.getClassName(),
                jr.getDataSource(),
                jr.getCompletedDate(),
                jr.getStartedDate(),
                jr.getStatusResult().toString(),
                jr.isCompleted(),
                jr.isStarted(),
                jr.isDoSync(),
                jr.isSearchLinks(),
                jr.getSearchFromDelta(),
                jr.getBodyRequest()
        );


        if (jr.getRequestRegistries()!=null) {
            for (RequestRegistry rr : jr.getRequestRegistries()) {
                rr.setJobRegistry(jr);
                requestRegistryProxy.save(rr);
            }
        }
         */
        String id = jobRegistryCustomRepository.persist(jr,true);
        logger.info("Complete save in database");
        jr.setId(id);
        return jr;
    }

    @Override
    public Optional<JobRegistry> findById(String id) {
        Optional<JobRegistry> requestRegistries = jobRegistryRepository.findById(id);
        if (!requestRegistries.isEmpty()) {
            return Optional.of(requestRegistries.get());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public JobRegistry findJobRegistryByUserIdAndRequestCodeAndRequestTypeNoNested(String userId, String requestCode, RequestType requestType) {

        logger.info("Getting data from Dependent Object Results...");
        Map<Long,Set<ObjectResult>> dependentObjectResults = objectResultProxy.getDependentObjectResultByRequestRegistry(userId,requestCode,requestType);
        logger.info("...Done get data from Dependent Object Results");
        logger.info("Getting data from Dependent Action Results...");
        Map<Long,Set<ActionResult>> dependentActionResults = actionResultProxy.getActionsResultByRequestRegistry(userId,requestCode,requestType);
        logger.info("...Done get data from Dependent Action Results");
        logger.info("Getting data from Dependent Action Results...");
        List<Tuple> results = jobRegistryCustomRepository.getResultsByUserIdAndRequestCodeAndRequestTypeNoNested(userId,requestCode,requestType);
        logger.info("...Done get data from Dependent Action Results");
        JobRegistry jr = null;
        Map<Long,RequestRegistry> jrRequestRegistryMap = new HashMap<>();
        Map<Long,ObjectResult> jrObjectResultMainMap = new HashMap<>();
        Map<Long,ObjectResult> jrObjectResultDependentMap = new HashMap<>();
        for (Set<ObjectResult> dependents : dependentObjectResults.values()) {
            for (ObjectResult or : dependents) {
                jrObjectResultDependentMap.put(or.getId(),or);
            }
        }
        Map<Long,Attribute> jrAttributesMainMap = new HashMap<>();
        Map<Long,Value> jrValuesMainMap = new HashMap<>();
        for (Tuple t : results) {
            if (jr == null) {
                jr = new JobRegistry(t);
            }
            // Recovery Request Results
            long idRequestRegistry = ((t.get("rr_id")!=null)?(Long.valueOf(t.get("rr_id").toString())):0);//t.get("rr_id")?null
            if (idRequestRegistry>0) {
                if (!jrRequestRegistryMap.containsKey(idRequestRegistry)) {
                    RequestRegistry rr = new RequestRegistry(jr,t);
                    jrRequestRegistryMap.put(rr.getId(),rr);
                    jr.getRequestRegistries().add(rr);
                }
            }

            // Recovery Object Results Mains
            long idObject= ((t.get("or_id")!=null)?(Long.valueOf(t.get("or_id").toString())):0);
            ObjectResult or = null;
            if (idObject>0) {
                if (!jrObjectResultMainMap.containsKey(idObject)) {
                    or = new ObjectResult(jr,t);
                    jrObjectResultMainMap.put(or.getId(),or);

                } else {
                    or = jrObjectResultMainMap.get(idObject);
                }
                // Si contiene object result dependientes
                if (dependentObjectResults.containsKey(or.getId())) {
                    for (ObjectResult orInner : dependentObjectResults.get(or.getId())) {
                        if (orInner.isAutomatic()) {
                            orInner.setParentAutomatic(or);
                            or.getAutomatic().add(orInner);
                        } else if (orInner.isManual()) {
                            orInner.setParentManual(or);
                            or.getManual().add(orInner);
                        } else if (orInner.isLink()) {
                            orInner.setParentLink(or);
                            or.getLink().add(orInner);
                        }
                    }
                }
                // Si contiene Acciones
                if (dependentActionResults.containsKey(or.getId())) {
                    for (ActionResult ar : dependentActionResults.get(or.getId())) {
                        Set<ObjectResult> objectResults = new HashSet<>();
                        for (ObjectResult orInner : ar.getObjectResults()) {
                            ObjectResult orAux = orInner;
                            if (jrObjectResultMainMap.containsKey(orInner.getId())) {
                                orAux = jrObjectResultMainMap.get(orInner.getId());
                            } else if (jrObjectResultDependentMap.containsKey(orInner)) {
                                orAux = jrObjectResultDependentMap.get(orInner.getId());
                            }
                            orAux.setActionResultParent(ar);
                            objectResults.add(orAux);
                        }
                        ar.setObjectResults(objectResults);
                        or.getActionResults().add(ar);
                    }
                }
                jr.getObjectResults().add(or);

                long idAttribute= ((t.get("at_id")!=null)?(Long.valueOf(t.get("at_id").toString())):0);
                Attribute att = null;
                if (idAttribute>0) {
                    if (!jrAttributesMainMap.containsKey(idAttribute)) {
                        att = new Attribute(or,null, t);
                        jrAttributesMainMap.put(att.getId(), att);
                    } else {
                        att = jrAttributesMainMap.get(idAttribute);
                    }
                    or.getAttributes().add(att);
                }

                long idVal= (long) ((t.get("va_id")!=null)?(Long.valueOf(t.get("va_id").toString())):0);
                Value v = null;
                if (idVal>0) {
                    if (!jrValuesMainMap.containsKey(idVal)) {
                        v = new Value(att, t);
                        jrAttributesMainMap.put(att.getId(), att);
                    } else {
                        v = jrValuesMainMap.get(idAttribute);
                    }
                    att.getValues().add(v);
                }
            }
        }
        return jr;
    }
}
