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
    public ObjectResult save(ObjectResult or) {

        // Guardo objeto plano
        Long id = objectResultRepository.getNextId();
        try {
            objectResultRepository.insertNoNested(
                    id,
                    or.getVersion(),
                    ((or.getOrigin() != null) ? or.getOrigin().name() : null),
                    or.getNode(),
                    or.getTripleStore(),
                    or.getClassName(),
                    or.getLocalURI(),
                    or.getCanonicalURI(),
                    or.getLastModification(),
                    ((or.getJobRegistry() != null) ? or.getJobRegistry().getId() : null),
                    or.getEntityId(),
                    ((or.getParentAutomatic() != null) ? or.getParentAutomatic().getId() : null),
                    ((or.getParentManual() != null) ? or.getParentManual().getId() : null),
                    ((or.getParentLink() != null) ? or.getParentLink().getId() : null),
                    or.getSimilarity(),
                    or.getSimilarityWithOutId(),
                    or.isMain(),
                    or.isAutomatic(),
                    or.isManual(),
                    or.isMerge(),
                    or.isLink(),
                    ((or.getMergeAction() != null) ? or.getMergeAction().name() : null),
                    ((or.getState() != null) ? or.getState().name() : null),
                    ((or.getActionResultParent() != null && or.isMain() == false) ? or.getActionResultParent().getId() : null)

            );
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        or.setId(id);

        if (or.getAutomatic()!=null) {
            for (ObjectResult orAux : or.getAutomatic()) {
                orAux.getParentAutomatic().setId(id);
                try {
                    save(orAux);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

        if (or.getManual()!=null) {
            for (ObjectResult orAux : or.getManual()) {
                orAux.getParentManual().setId(id);
                try {
                    save(orAux);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

        if (or.getLink()!=null) {
            for (ObjectResult orAux : or.getLink()) {
                orAux.getParentLink().setId(id);
                try {
                    save(orAux);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

        if (or.getActionResults()!=null) {
            for (ActionResult ac : or.getActionResults()) {
                ac.getObjectResultParent().setId(id);
                try {
                    actionResultProxy.save(ac);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

        /*
        if (or.getActionResults()!=null) {
            for (ActionResult ac : or.getActionResults()) {
                actionResultProxy.save(ac);
            }
        }

        if (or.getAutomatic()!=null) {
            for (ObjectResult orAux : or.getAutomatic()) {
                save(orAux);
            }
        }

        if (or.getManual()!=null) {
            for (ObjectResult orAux : or.getManual()) {
                save(orAux);
            }
        }

        if (or.getLink()!=null) {
            for (ObjectResult orAux : or.getLink()) {
                save(orAux);
            }
        }

        if (or.getAttributes()!=null) {
            for (Attribute at : or.getAttributes()) {
                attributeProxy.save(at);
            }
        }
        */
        return or;
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
