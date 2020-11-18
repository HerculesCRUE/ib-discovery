package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.DiscoveryApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscoveryApplicationRepository extends JpaRepository<DiscoveryApplication,Long> {

    Optional<DiscoveryApplication> findById(String id);
}
