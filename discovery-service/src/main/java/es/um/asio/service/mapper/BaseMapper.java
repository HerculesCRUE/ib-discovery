package es.um.asio.service.mapper;

import java.util.Optional;

/**
 * MapStruct base mapper.
 *
 * @param <E>
 *            the entity type.
 * @param <D>
 *            the DTO type.
 */
public interface BaseMapper<E, D> {
    /**
     * Convert entity to DTO.
     *
     * @param entity
     *            the entity
     * @return the DTO
     */
    D convertToDto(E entity);

    /**
     * Convert DTO to entity.
     *
     * @param dto
     *            the DTO
     * @return the entity.
     */
    E convertFromDto(D dto);

    /**
     * Convert {@link Optional} to DTO
     *
     * @param optional
     *            the Optional.
     * @return the DTO.
     */
    default Optional<D> convertToDto(final Optional<E> optional) {
        return Optional.ofNullable(this.convertToDto(optional.orElse(null)));
    }

    /**
     * Convert {@link Optional} to DTO
     *
     * @param optional
     *            the Optional.
     * @return the DTO.
     */
    default Optional<E> convertFromDto(final Optional<D> optional) {
        return Optional.ofNullable(this.convertFromDto(optional.orElse(null)));
    }
}
