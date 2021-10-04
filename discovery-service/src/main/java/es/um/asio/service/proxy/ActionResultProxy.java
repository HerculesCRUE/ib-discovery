package es.um.asio.service.proxy;


import es.um.asio.service.model.relational.Action;
import es.um.asio.service.model.relational.ActionResult;
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
public interface ActionResultProxy {

    /**
     * Save a request Action in the repository
     * @param action Action
     * @return Action
     */
    ActionResult save(ActionResult actionResult);

    /**
     * Find by id
     * @param id Long. The id.
     * @return Optional<Action>
     */
    Optional<ActionResult> findById(long id);
}
