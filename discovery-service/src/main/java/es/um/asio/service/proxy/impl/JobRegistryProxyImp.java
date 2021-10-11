package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.ObjectResult;
import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.proxy.JobRegistryProxy;
import es.um.asio.service.proxy.ObjectResultProxy;
import es.um.asio.service.proxy.RequestRegistryProxy;
import es.um.asio.service.repository.relational.JobRegistryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


    @Override
    public JobRegistry save(JobRegistry jra) {
        /*
        JobRegistry jra;
        Optional<JobRegistry> jobRegistry = findById(jr.getId());
        if (!jobRegistry.isEmpty()) {
            jra = jobRegistry.get().merge(jr);
        } else {
            jra = jr;
        }
         */
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
                jra.getBodyRequest()
        );
        if (jra.getObjectResults()!=null) {
            for (ObjectResult or : jra.getObjectResults()) {
                objectResultProxy.save(or);
            }
        }
        /*
        if (jr.getRequestRegistries()!=null) {
            for (RequestRegistry rr : jr.getRequestRegistries()) {
                requestRegistryProxy.save(rr);
            }
        }

        if (jr.getObjectResults()!=null) {
            for (ObjectResult or : jr.getObjectResults()) {
                objectResultProxy.save(or);
            }
        }
        try {
            return jobRegistryRepository.saveAndFlush(jr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jr;
         */
        return jra;
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
