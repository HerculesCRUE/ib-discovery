package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.model.relational.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestRegistryRepository extends JpaRepository<RequestRegistry, Long> {

    Optional<RequestRegistry> findByUserIdAndRequestCodeAndRequestType(String userId, String requestCode, RequestType requestType);


}
