package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.model.relational.RequestType;
import es.um.asio.service.proxy.RequestRegistryProxy;
import es.um.asio.service.repository.relational.RequestRegistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Optional<List<RequestRegistry>> aux = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(requestRegistry.getUserId(),requestRegistry.getRequestCode(), requestRegistry.getRequestType());
        if (!aux.isEmpty() && aux.get().size()>0) {
            return requestRegistryRepository.saveAndFlush(aux.get().get(0));
        } else
            return requestRegistryRepository.saveAndFlush(requestRegistry);
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
        Optional<List<RequestRegistry>> requestRegistries = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, requestType);
        Optional<RequestRegistry> requestRegistry;
        if (!requestRegistries.isEmpty() && requestRegistries.get().size()>0) {
            return Optional.of(requestRegistries.get().get(0));
        } else {
            return Optional.empty();
        }
        // return requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, requestType);
    }

}
