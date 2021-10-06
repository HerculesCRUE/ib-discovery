package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.*;
import es.um.asio.service.proxy.*;
import es.um.asio.service.repository.relational.ObjectResultRepository;
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
public class ObjectResultProxyImp implements ObjectResultProxy {

    private final Logger logger = LoggerFactory.getLogger(ObjectResultProxyImp.class);

    @Autowired
    ObjectResultRepository objectResultRepository;

    @Autowired
    AttributeProxy attributeProxy;

    @Autowired
    ActionResultProxy actionResultProxy;


    @Override
    public ObjectResult save(ObjectResult objectResult) {

        if (objectResult.getActionResults()!=null) {
            for (ActionResult ac : objectResult.getActionResults()) {
                actionResultProxy.save(ac);
            }
        }

        if (objectResult.getAutomatic()!=null) {
            for (ObjectResult or : objectResult.getAutomatic()) {
                save(or);
            }
        }

        if (objectResult.getManual()!=null) {
            for (ObjectResult orAux : objectResult.getManual()) {
                save(orAux);
            }
        }

        if (objectResult.getLink()!=null) {
            for (ObjectResult or : objectResult.getLink()) {
                save(or);
            }
        }

        if (objectResult.getAttributes()!=null) {
            for (Attribute at : objectResult.getAttributes()) {
                attributeProxy.save(at);
            }
        }
        try {
            return objectResultRepository.saveAndFlush(objectResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectResult;
    }

    @Override
    public Optional<ObjectResult> findById(long id) {
        Optional<ObjectResult> requestRegistries = objectResultRepository.findById(id);
        if (!requestRegistries.isEmpty()) {
            return Optional.of(requestRegistries.get());
        } else {
            return Optional.empty();
        }
    }

}
