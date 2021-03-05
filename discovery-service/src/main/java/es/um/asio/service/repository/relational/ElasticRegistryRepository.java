package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.ElasticRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ElasticRegistryRepository interface. Repository JpaRepository for ElasticRegistry entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface ElasticRegistryRepository extends JpaRepository<ElasticRegistry,Long> {
}
