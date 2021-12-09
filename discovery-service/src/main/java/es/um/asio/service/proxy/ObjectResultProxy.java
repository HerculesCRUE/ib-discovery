package es.um.asio.service.proxy;


import es.um.asio.service.model.relational.Attribute;
import es.um.asio.service.model.relational.ObjectResult;
import es.um.asio.service.model.relational.RequestType;
import es.um.asio.service.model.relational.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Proxy for RequestRegistry repository interface
 * @see ValueRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public interface ObjectResultProxy {

    /**
     * Save a request ObjectResult in the repository
     * @param objectResult ObjectResult
     * @return ObjectResult
     */
    ObjectResult save(ObjectResult objectResult);

    /**
     * Find by id
     * @param id Long. The id.
     * @return Optional<ObjectResult>
     */
    Optional<ObjectResult> findById(long id);


    /**
     * Find by id
     * @param id Long. The id.
     * @return Optional<ObjectResult>
     */
    Map<Long, Set<ObjectResult>> getDependentObjectResultByRequestRegistry(String userId, String requestCode, RequestType requestType);
}
