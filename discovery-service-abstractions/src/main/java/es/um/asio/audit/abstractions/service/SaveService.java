package es.um.asio.audit.abstractions.service;

import es.um.asio.audit.abstractions.exception.NoSuchEntityException;

import java.util.List;

/**
 * Interface for services that save entities to the database.
 *
 * @param <E>
 *            The entity type
 */
public interface SaveService<E> {
    /**
     * Save an entity to the database.
     *
     * @param entity
     *            The entity to be saved
     * @return The saved entity
     */
    E save(E entity);

    /**
     * Save a list of entities to the database.
     *
     * @param entities
     *            The entities to save
     * @return The updated entities
     */
    List<E> save(Iterable<E> entities);

    /**
     * Updates an entity in the database.
     *
     * @param entity
     *            The entity to update
     * @return The updated entity
     * @throws NoSuchEntityException
     *             in case the entity was not found.
     */
    E update(E entity) throws NoSuchEntityException;
}
