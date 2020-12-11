package es.um.asio.audit.abstractions.service;

import es.um.asio.audit.abstractions.filter.EntityFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Interface for services that perform queries on given entities.
 *
 * @param <E>
 *            The entity type.
 * @param <I>
 *            The entity identifier type.
 * @param <F>
 *            The filter type.
 * @param <P>
 *            The pagination type.
 */
public interface QueryService<E, I extends Serializable, F extends EntityFilter> {
    /**
     * Find a single entity from the identifier.
     *
     * @param identifier
     *            The identifier
     * @return The entity returned
     */
    Optional<E> find(I identifier);

    /**
     * Fina a paginated list of entities from a given filter.
     *
     * @param filter
     *            The filter
     * @param pageable
     *            Pagination configuration
     * @return The paginated list
     */
    Page<E> findPaginated(F filter, Pageable pageable);

    /**
     * Returns all of the entities from the database.
     *
     * @return the list
     */
    List<E> findAll();
}
