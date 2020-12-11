package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.JobRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface JobRegistryRepository extends JpaRepository<JobRegistry,String> {

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

    @Query(value = "update job_registry j " +
            "set j.status_result = 'ABORTED' , j.is_completed = 1, j.completion_date = now() " +
            "WHERE j.discoveryApplication_id <> :appId and " +
            "j.status_result = 'PENDING'"
            , nativeQuery = true)
    void closeOtherJobRegistryByAppId(@Param("appId") String appId);

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
