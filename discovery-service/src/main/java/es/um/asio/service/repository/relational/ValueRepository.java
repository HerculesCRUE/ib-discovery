package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.Value;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ValueRepository interface. Repository JpaRepository for Value entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface ValueRepository extends JpaRepository<Value,Long> {
}
