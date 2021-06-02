package es.um.asio.service.proxy;

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
public interface RequestRegistryProxy {

    /**
     * Save a request registry in the repository
     * @param requestRegistry RequestRegistry
     * @return RequestRegistry
     */
    RequestRegistry save(RequestRegistry requestRegistry);

    /**
     * Find by userId and RequestCode And RequestType
     * @param userId String. The User id.
     * @param requestCode String. The Request code.
     * @param requestType String. The Request type.
     * @return Optional<RequestRegistry>
     */
    Optional<RequestRegistry> findByUserIdAndRequestCodeAndRequestType(String userId, String requestCode, RequestType requestType);
}
