package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.CacheRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CacheRegistryRepository extends JpaRepository<CacheRegistry,Long> {
}
