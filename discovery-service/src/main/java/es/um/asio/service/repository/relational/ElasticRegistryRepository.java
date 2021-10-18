package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.ElasticRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * ElasticRegistryRepository interface. Repository JpaRepository for ElasticRegistry entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Transactional(readOnly = true)
public interface ElasticRegistryRepository extends JpaRepository<ElasticRegistry,Long> {
}
