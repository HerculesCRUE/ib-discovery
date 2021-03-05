package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.model.relational.RequestType;
import es.um.asio.service.proxy.RequestRegistryProxy;
import es.um.asio.service.repository.relational.RequestRegistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Proxy for RequestRegistry repository implementation.
 * @implNote RequestRegistryProxy
 * @see RequestRegistry
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public class RequestRegistryProxyImp implements RequestRegistryProxy {


    @Autowired
    RequestRegistryRepository requestRegistryRepository;

    /**
     * Save a request registry in the repository
     * @param requestRegistry RequestRegistry
     * @return RequestRegistry
     */
    @Override
    public RequestRegistry save(RequestRegistry requestRegistry) {
        Optional<RequestRegistry> aux = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(requestRegistry.getUserId(),requestRegistry.getRequestCode(), requestRegistry.getRequestType());
        if (!aux.isEmpty()) {
            return requestRegistryRepository.save(aux.get());
        } else
            return requestRegistryRepository.save(requestRegistry);
    }

    /**
     * Find by userId and RequestCode And RequestType
     * @param userId String. The User id.
     * @param requestCode String. The Request code.
     * @param requestType String. The Request type.
     * @return Optional<RequestRegistry>
     */
    @Override
    public Optional<RequestRegistry> findByUserIdAndRequestCodeAndRequestType(String userId, String requestCode, RequestType requestType) {
        return requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, requestType);
    }

}
