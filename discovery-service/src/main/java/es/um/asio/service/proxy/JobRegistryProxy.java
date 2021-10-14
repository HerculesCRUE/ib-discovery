package es.um.asio.service.proxy;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.model.relational.RequestType;

import java.util.Optional;

/**
 * Proxy for RequestRegistry repository interface
 * @see RequestRegistry
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface JobRegistryProxy {

    /**
     * Save a request jobRegistry in the repository
     * @param jobRegistry JobRegistry
     * @return JobRegistry
     */
    JobRegistry save(JobRegistry jobRegistry) throws CloneNotSupportedException;

    /**
     * Save a request jobRegistry in the repository
     * @param jobRegistry JobRegistry
     * @return JobRegistry
     */
    JobRegistry saveRequests(JobRegistry jobRegistry) throws CloneNotSupportedException;

    /**
     * Find by id
     * @param id String. The id
     * @return Optional<JobRegistry>
     */
    Optional<JobRegistry> findById(String id);
}
