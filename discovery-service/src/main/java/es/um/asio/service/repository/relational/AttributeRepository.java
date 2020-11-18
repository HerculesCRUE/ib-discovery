package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeRepository extends JpaRepository<Attribute,String> {
}
