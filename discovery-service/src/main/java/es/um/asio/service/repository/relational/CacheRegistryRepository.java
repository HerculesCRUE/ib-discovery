package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.CacheRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CacheRegistryRepository interface. Repository JpaRepository for CacheRegistry entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface CacheRegistryRepository extends JpaRepository<CacheRegistry,Long> {
}
