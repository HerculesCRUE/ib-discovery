package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.ActionResult;
import es.um.asio.service.model.relational.Attribute;
import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.ObjectResult;
import es.um.asio.service.proxy.ActionResultProxy;
import es.um.asio.service.proxy.AttributeProxy;
import es.um.asio.service.repository.relational.ActionResultRepository;
import es.um.asio.service.repository.relational.ObjectResultRepository;
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
public class ActionResultProxyImp implements ActionResultProxy {

    @Autowired
    ActionResultRepository actionResultRepository;

    @Autowired
    AttributeProxy attributeProxy;

    @Autowired
    ActionResultProxy actionResultProxy;


    @Override
    public ActionResult save(ActionResult actionResult) {

        return actionResultRepository.saveAndFlush(actionResult);
    }

    @Override
    public Optional<ActionResult> findById(long id) {
        Optional<ActionResult> requestRegistries = actionResultRepository.findById(id);
        if (!requestRegistries.isEmpty()) {
            return Optional.of(requestRegistries.get());
        } else {
            return Optional.empty();
        }
    }

}
