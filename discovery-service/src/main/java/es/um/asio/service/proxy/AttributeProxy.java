package es.um.asio.service.proxy;


import es.um.asio.service.model.relational.Attribute;
import es.um.asio.service.model.relational.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Proxy for RequestRegistry repository interface
 * @see ValueRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public interface AttributeProxy {

    /**
     * Save a request Attribute in the repository
     * @param attribute Attribute
     * @return Attribute
     */
    Attribute save(Attribute attribute);

    /**
     * Find by id
     * @param id Long. The id.
     * @return Optional<Attribute>
     */
    Optional<Attribute> findById(long id);
}
