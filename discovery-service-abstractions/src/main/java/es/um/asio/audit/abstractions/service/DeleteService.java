package es.um.asio.audit.abstractions.service;

import java.io.Serializable;

/**
 * Interface for services that can delete entities from the database.
 *
 * @param <E>
 *            The entity type
 * @param <I>
 *            The entity identifier type.
 */
public interface DeleteService<E, I extends Serializable> {
    /**
     * Deletes an entity in the database.
     *
     * @param entity
     *            The entity to delete
     */
    void delete(E entity);

    /**
     * Deletes an entity in the database.
     *
     * @param identifier
     *            The identifier of the entity
     */
    void delete(I identifier);
}
