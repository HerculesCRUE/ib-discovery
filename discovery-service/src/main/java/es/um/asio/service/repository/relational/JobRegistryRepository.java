package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.JobRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
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

    @Modifying
    @Transactional
    @Query(value = "UPDATE discovery.job_registry j set " +
            "j.version = :version," +
            "j.discoveryApplication_id = :discoveryApplicationId," +
            "j.node = :node," +
            "j.triple_store = :tripleStore," +
            "j.class_name = :className," +
            "j.class_name = :className," +
            "j.data_source = :dataSource," +
            "j.completion_date = :completedDate," +
            "j.started_date = :startedDate," +
            "j.status_result = :statusResult," +
            "j.is_completed = :isCompleted," +
            "j.is_started = :isStarted," +
            "j.do_synchronous = :doSync," +
            "j.search_links = :searchLinks," +
            "j.search_from_delta = :searchFromDelta," +
            "j.body_request = :bodyRequest" +
            " WHERE j.id = :id", nativeQuery = true)
    void updateNoNested(
            @Param("id") String id,
            @Param("version") long version,
            @Param("discoveryApplicationId") String discoveryApplicationId,
            @Param("node") String node,
            @Param("tripleStore") String tripleStore,
            @Param("className") String className,
            @Param("dataSource") String dataSource,
            @Param("completedDate") Date completedDate,
            @Param("startedDate") Date startedDate,
            @Param("statusResult") String statusResult,
            @Param("isCompleted") boolean isCompleted,
            @Param("isStarted") boolean isStarted,
            @Param("doSync") boolean doSync,
            @Param("searchLinks") boolean searchLinks,
            @Param("searchFromDelta") Date searchFromDelta,
            @Param("bodyRequest") String bodyRequest
    );


    /*
    *   id
        body_request
        do_synchronous
        is_completed
        is_started
        search_from_delta
        search_links
        started_date
        status_result
        triple_store
    * */


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
