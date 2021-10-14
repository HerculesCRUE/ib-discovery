package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.ObjectResult;
import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.proxy.DiscoveryApplicationProxy;
import es.um.asio.service.proxy.JobRegistryProxy;
import es.um.asio.service.proxy.ObjectResultProxy;
import es.um.asio.service.proxy.RequestRegistryProxy;
import es.um.asio.service.repository.relational.JobRegistryRepository;
import es.um.asio.service.repository.relational.custom.JobRegistryCustomRepository;
import es.um.asio.service.repository.relational.custom.imp.JobRegistryCustomRepositoryImp;
import es.um.asio.service.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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


    @Override
    public JobRegistry save(JobRegistry jra) throws CloneNotSupportedException {
        String id = jobRegistryCustomRepository.persist(jra,true);
        jra.setId(id);
        return jra;
        /*
        if (!Utils.isValidString(jra.getId())) {
            jra = saveRequests(jra);
        }
        jobRegistryRepository.updateNoNested(
                jra.getId(),
                jra.getVersion(),
                jra.getDiscoveryApplication().getId(),
                jra.getNode(),
                jra.getTripleStore(),
                jra.getClassName(),
                jra.getDataSource(),
                jra.getCompletedDate(),
                jra.getStartedDate(),
                jra.getStatusResult().toString(),
                jra.isCompleted(),
                jra.isStarted(),
                jra.isDoSync(),
                jra.isSearchLinks(),
                jra.getSearchFromDelta(),
                jra.getBodyRequest());
        if (jra.getObjectResults()!=null) {
            for (ObjectResult or : jra.getObjectResults()) {
                objectResultProxy.save(or);
            }
        }
        return jra.clone();
         */
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
}
