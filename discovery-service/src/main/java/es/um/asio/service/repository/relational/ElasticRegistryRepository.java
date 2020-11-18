package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.ElasticRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElasticRegistryRepository extends JpaRepository<ElasticRegistry,Long> {
}
