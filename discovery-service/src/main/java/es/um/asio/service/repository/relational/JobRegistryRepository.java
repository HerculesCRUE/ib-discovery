package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.JobRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * JobRegistryRepository interface. Repository JpaRepository for JobRegistry entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface JobRegistryRepository extends JpaRepository<JobRegistry,String> {

    /**
     * Query for get Open Jobs by appId, node, tripleStore and className
     * @param appId String. The app id.
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @param className String. The class name.
     * @return JobRegistry
     */
    @Query(value = "select j.* from job_registry j " +
            "left join request_registry r on j.id = r.jobRegistry_id " +
            "where " +
            "j.discoveryApplication_id = :appId and " +
            "j.node = :node and " +
            "j.triple_store = :tripleStore and " +
            "j.class_name = :className and " +
            "j.is_completed = 0 "+
            "order by r.request_date "+
            "limit 1"
            , nativeQuery = true
    )
   JobRegistry findOpenJobsByDiscoveryAppAndNodeAndTripleStoreAndClassName(
           @Param("appId") String appId,
           @Param("node") String node,
           @Param("tripleStore") String tripleStore,
           @Param("className") String className);

    /**
     * Query for update to ABORTED the old request not closed
     * @param appId String. The app id.
     */
    @Query(value = "update job_registry j " +
            "set j.status_result = 'ABORTED' , j.is_completed = 1, j.completion_date = now() " +
            "WHERE j.discoveryApplication_id <> :appId and " +
            "j.status_result = 'PENDING'"
            , nativeQuery = true)
    void closeOtherJobRegistryByAppId(@Param("appId") String appId);

    /**
     * Get last date in Job by appId, node, tripleStore and requestType
     * @param appId String. The app id.
     * @param node String. The node name.
     * @param tripleStore String. The triple store name.
     * @param requestType String. The request type.
     * @return
     */
    @Query(value = "SELECT max(completion_date) " +
            "FROM job_registry j " +
            "left join request_registry as r on j.id = r.jobRegistry_id " +
            "WHERE j.node = :node and " +
            "j.triple_store = :tripleStore and " +
            "j.class_name = :className and " +
            "j.status_result = 'COMPLETED' and " +
            "r.request_type = :requestType "
            , nativeQuery = true)
    Date getLastDateFromNodeAndTripleStoreAndClassName(
            @Param("node") String node,
            @Param("tripleStore") String tripleStore,
            @Param("className") String className,
            @Param("requestType") String requestType
    );


}
