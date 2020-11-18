package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.Value;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValueRepository extends JpaRepository<Value,Long> {
}
