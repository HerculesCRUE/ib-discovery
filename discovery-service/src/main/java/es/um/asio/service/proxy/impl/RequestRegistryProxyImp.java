package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.model.relational.RequestType;
import es.um.asio.service.proxy.RequestRegistryProxy;
import es.um.asio.service.repository.relational.RequestRegistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequestRegistryProxyImp implements RequestRegistryProxy {

    @Autowired
    RequestRegistryRepository requestRegistryRepository;

    @Override
    public RequestRegistry save(RequestRegistry requestRegistry) {
        Optional<RequestRegistry> aux = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(requestRegistry.getUserId(),requestRegistry.getRequestCode(), requestRegistry.getRequestType());
        if (!aux.isEmpty()) {
            return requestRegistryRepository.save(aux.get());
        } else
            return requestRegistryRepository.save(requestRegistry);
    }

    @Override
    public Optional<RequestRegistry> findByUserIdAndRequestCodeAndRequestType(String userId, String requestCode, RequestType requestType) {
        return requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, requestType);
    }

}
