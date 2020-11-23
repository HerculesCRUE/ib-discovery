package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.ActionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionResultRepository extends JpaRepository<ActionResult,Long> {
}
