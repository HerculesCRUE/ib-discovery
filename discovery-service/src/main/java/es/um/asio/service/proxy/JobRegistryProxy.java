package es.um.asio.service.proxy;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.model.relational.RequestType;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    JobRegistry save(JobRegistry jobRegistry) throws CloneNotSupportedException;

    /**
     * Save a request jobRegistry in the repository
     * @param jobRegistry JobRegistry
     * @return JobRegistry
     */
    @Transactional
    JobRegistry saveRequests(JobRegistry jobRegistry) throws CloneNotSupportedException;

    /**
     * Find by id
     * @param id String. The id
     * @return Optional<JobRegistry>
     */
    @Transactional
    Optional<JobRegistry> findById(String id);


    /**
     * Find by id
     * @param userId String. The userId
     * @param requestCode String. The requestCode
     * @param requestType String. The requestType
     * @return JobRegistry
     */
    @Transactional
    JobRegistry findJobRegistryByUserIdAndRequestCodeAndRequestTypeNoNested(String userId, String requestCode, RequestType requestType);
}
