package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.model.relational.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RequestRegistry repository
 * @see RequestRegistry
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface RequestRegistryRepository extends JpaRepository<RequestRegistry, Long> {

    /**
     * Find by userId and RequestCode And RequestType
     * @param userId String. The User id.
     * @param requestCode String. The Request code.
     * @param requestType String. The Request type.
     * @return Optional<RequestRegistry>
     */
    Optional<RequestRegistry> findByUserIdAndRequestCodeAndRequestType(String userId, String requestCode, RequestType requestType);


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

}
