package es.um.asio.service.proxy;

import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.model.relational.RequestType;

import java.util.Optional;

public interface RequestRegistryProxy {

    RequestRegistry save(RequestRegistry requestRegistry);

    Optional<RequestRegistry> findByUserIdAndRequestCodeAndRequestType(String userId, String requestCode, RequestType requestType);
}
