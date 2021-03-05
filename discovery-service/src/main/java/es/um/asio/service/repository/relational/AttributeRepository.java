package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AttributeRepository interface. Repository JpaRepository for Attribute entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface AttributeRepository extends JpaRepository<Attribute,String> {
}
