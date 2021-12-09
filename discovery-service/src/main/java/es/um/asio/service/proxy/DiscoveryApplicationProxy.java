package es.um.asio.service.proxy;


import es.um.asio.service.model.relational.DiscoveryApplication;
import es.um.asio.service.model.relational.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Proxy for RequestRegistry repository interface
 * @see ValueRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public interface DiscoveryApplicationProxy {

    /**
     * Save a request registry in the repository
     * @param value Value
     * @return Value
     */
    DiscoveryApplication save(DiscoveryApplication value);

    /**
     * Find by userId and RequestCode And RequestType
     * @param id Long. The id.
     * @return Optional<Value>
     */
    Optional<DiscoveryApplication> findById(String id);
}
