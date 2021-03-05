package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.DiscoveryApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * DiscoveryApplicationRepository interface. Repository JpaRepository for DiscoveryApplication entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface DiscoveryApplicationRepository extends JpaRepository<DiscoveryApplication,Long> {

    Optional<DiscoveryApplication> findById(String id);
}
