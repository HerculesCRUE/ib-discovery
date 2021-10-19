package es.um.asio.service.proxy;


import es.um.asio.service.model.relational.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    /**
     * Find by id
     * @param id Long. The id.
     * @return Optional<ObjectResult>
     */
    Map<Long, Set<ActionResult>> getActionsResultByRequestRegistry(String userId, String requestCode, RequestType requestType);
}
