package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.ObjectResult;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ObjectResultRepository interface. Repository JpaRepository for ObjectResult entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface ObjectResultRepository extends JpaRepository<ObjectResult,Long> {
}
