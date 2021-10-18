package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.model.relational.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository for RequestRegistry repository
 * @see RequestRegistry
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Transactional(readOnly = true)
public interface RequestRegistryRepository extends JpaRepository<RequestRegistry, Long> {

    /**
     * Find by userId and RequestCode And RequestType
     * @param userId String. The User id.
     * @param requestCode String. The Request code.
     * @param requestType String. The Request type.
     * @return Optional<RequestRegistry>
     */
    Optional<List<RequestRegistry>> findByUserIdAndRequestCodeAndRequestType(String userId, String requestCode, RequestType requestType);


    /**
     * Find by userId and RequestCode And RequestType
     * @param userId String. The User id.
     * @param requestCode String. The Request code.
     * @param requestType String. The Request type.
     * @return Optional<RequestRegistry>
     */
    Optional<List<RequestRegistry>> findByUserIdOrderByRequestDateDesc(String userId);


    /**
     * Find distinct request codes
     * @return List<String> of distinct request codes
     */
    @Query(value = "SELECT DISTINCT a.request_code FROM request_registry a", nativeQuery = true)
    List<String> findDistinctRequestCode();

    /**
     * Check if exist request code
     * @param requestCode String Request code
     * @return int. 0 if not exist 1 if exist
     */
    @Query(value = "SELECT IF(count(request_code)>0,1,0) FROM request_registry a WHERE request_code like ?1%", nativeQuery = true)
    int existRequestCode(String requestCode);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "INSERT discovery.request_registry (\n" +
            "\tid, version, user_id, request_code, request_date, " +
            "request_type, propague_in_kafka, web_hook, jobRegistry_id\n"+
            ") VALUES (\n" +
            "\t:id, :version, :userId, :requestCode, :requestDate, \n"+
            "\t:requestType, :propagueInKafka, :webHook, :jobRegistryId\n"+
            ")"
            , nativeQuery = true)
    void insertNoNested(
            @Param("id") Long id,
            @Param("version") Long version,
            @Param("userId") String userId,
            @Param("requestCode") String requestCode,
            @Param("requestDate") Date requestDate,
            @Param("requestType") String requestType,
            @Param("propagueInKafka") boolean propagueInKafka,
            @Param("webHook") String webHook,
            @Param("jobRegistryId") String jobRegistryId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE discovery.request_registry j set " +
            "j.version = :version," +
            "j.user_id = :userId," +
            "j.request_code = :requestCode," +
            "j.request_date = :requestDate," +
            "j.request_type = :requestType," +
            "j.propague_in_kafka = :propagueInKafka," +
            "j.web_hook = :webHook," +
            "j.jobRegistry_id = :jobRegistryId" +
            " WHERE j.id = :id", nativeQuery = true)
    void updateNoNested(
            @Param("id") Long id,
            @Param("version") Long version,
            @Param("userId") String userId,
            @Param("requestCode") String requestCode,
            @Param("requestDate") Date requestDate,
            @Param("requestType") String requestType,
            @Param("propagueInKafka") boolean propagueInKafka,
            @Param("webHook") String webHook,
            @Param("jobRegistryId") String jobRegistryId
    );

    @Query(value = "SELECT coalesce(max(id), 0)+1 FROM discovery.request_registry", nativeQuery = true)
    public Long getNextId();

}
