package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.ActionResult;
import es.um.asio.service.model.relational.Attribute;
import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.Value;
import es.um.asio.service.proxy.ActionResultProxy;
import es.um.asio.service.proxy.AttributeProxy;
import es.um.asio.service.proxy.ValueProxy;
import es.um.asio.service.repository.relational.ActionResultRepository;
import es.um.asio.service.repository.relational.AttributeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AttributeProxyImp implements AttributeProxy {

    private final Logger logger = LoggerFactory.getLogger(AttributeProxyImp.class);

    @Autowired
    AttributeRepository attributeRepository;


    @Autowired
    ValueProxy valueProxy;


    @Override
    public Attribute save(Attribute att) {
        Long id = attributeRepository.getNextId();
        attributeRepository.insertNoNested(id,att.getKey(), att.getVersion(), ((att.getObjectResult()!=null)?att.getObjectResult().getId():null), ((att.getParentValue()!=null)?att.getParentValue().getId():null) );
        att.setId(id);
        if (att.getValues()!=null) {
            for (Value v :att.getValues()) {
                valueProxy.save(v);
            }
        }
        return att;
    }

    @Override
    public Optional<Attribute> findById(long id) {
        Optional<Attribute> requestRegistries = attributeRepository.findById(id);
        if (!requestRegistries.isEmpty()) {
            return Optional.of(requestRegistries.get());
        } else {
            return Optional.empty();
        }
    }

}
