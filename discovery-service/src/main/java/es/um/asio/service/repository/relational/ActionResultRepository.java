package es.um.asio.service.repository.relational;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.relational.ActionResult;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ActionResultRepository interface. Repository JpaRepository for ActionResult entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface ActionResultRepository extends JpaRepository<ActionResult,Long> {
}
