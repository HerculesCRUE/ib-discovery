package es.um.asio.service.proxy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.um.asio.service.proxy.TriplesStorageProxy;
import es.um.asio.service.service.TriplesStorageService;

/**
 * Proxy service implementation for triples. Performs DTO conversion and permission checks.
 */
@Service
public class TriplesStorageProxyImpl implements TriplesStorageProxy {

    /**
     * Service Layer.
     */
    @Autowired
    private TriplesStorageService service;
    
//    /**
//     * DTO to entity mapper.
//     */
//    @Autowired
//    private UserMapper mapper;
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public Optional<UserDto> find(final String identifier) {
//        return this.mapper.convertToDto(this.service.find(identifier));
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public Page<UserDto> findPaginated(final UserFilter filter, final Pageable pageable) {
//        return this.mapper.convertToDto(this.service.findPaginated(filter, pageable));
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public List<UserDto> findAll() {
//        return this.mapper.convertToDto(this.service.findAll());
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public UserDto save(final UserDto entity) {
//        return this.mapper.convertToDto(this.service.save(this.mapper.convertFromDto(entity)));
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public List<UserDto> save(final Iterable<UserDto> entities) {
//        return this.mapper.convertToDto(this.service.save(this.mapper.convertFromDto(entities)));
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public UserDto update(final UserDto entity) throws NoSuchEntityException {
//        final User user = this.mapper.updateFromDto(entity, this.service.find(entity.getId())
//                .orElseThrow(() -> new NoSuchEntityException(String.format("User %s not found", entity.getId()))));
//        return this.mapper.convertToDto(this.service.update(user));
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void delete(final UserDto entity) {
//        this.service.delete(this.mapper.convertFromDto(entity));
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void delete(final String identifier) {
//        this.service.delete(identifier);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void undelete(final String identifier) {
//        this.service.undelete(identifier);
//    }

}
