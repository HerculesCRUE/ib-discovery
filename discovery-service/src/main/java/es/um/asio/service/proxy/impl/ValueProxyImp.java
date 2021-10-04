package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.Attribute;
import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.Value;
import es.um.asio.service.proxy.AttributeProxy;
import es.um.asio.service.proxy.ValueProxy;
import es.um.asio.service.repository.relational.AttributeRepository;
import es.um.asio.service.repository.relational.ValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Proxy for JobRegistryProxyImp repository implementation.
 * @implNote JobRegistryProxyImp
 * @see JobRegistry
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public class ValueProxyImp implements ValueProxy {

    @Autowired
    ValueRepository valueRepository;



    @Override
    public Value save(Value value) {
        return valueRepository.saveAndFlush(value);
    }

    @Override
    public Optional<Value> findById(long id) {
        Optional<Value> requestRegistries = valueRepository.findById(id);
        if (!requestRegistries.isEmpty()) {
            return Optional.of(requestRegistries.get());
        } else {
            return Optional.empty();
        }
    }

}
