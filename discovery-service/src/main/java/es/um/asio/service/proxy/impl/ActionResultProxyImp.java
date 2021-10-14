package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.ActionResult;
import es.um.asio.service.model.relational.Attribute;
import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.ObjectResult;
import es.um.asio.service.proxy.ActionResultProxy;
import es.um.asio.service.proxy.AttributeProxy;
import es.um.asio.service.proxy.ObjectResultProxy;
import es.um.asio.service.repository.relational.ActionResultRepository;
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
public class ActionResultProxyImp implements ActionResultProxy {

    private final Logger logger = LoggerFactory.getLogger(ActionResultProxyImp.class);

    @Autowired
    ActionResultRepository actionResultRepository;


    @Autowired
    ObjectResultRepository objectResultRepository;

    @Autowired
    AttributeProxy attributeProxy;


    @Override
    public ActionResult save(ActionResult actionResult) {
        Long id = actionResultRepository.getNextId();
        actionResultRepository.insertNoNested(
                id,
                ((actionResult.getAction()!=null)?actionResult.getAction().name():null),
                actionResult.getVersion(),
                ((actionResult.getObjectResultParent()!=null)?actionResult.getObjectResultParent().getId():null)
        );
        actionResult.setId(id);
        for (ObjectResult or : actionResult.getObjectResults()) {
            try {
                if (or.getId()!=0) {
                    objectResultRepository.updateActionResultId(or.getId(), id);
                } else {
                    Long idOR = objectResultRepository.getNextId();
                    try {
                        objectResultRepository.insertNoNested(
                                idOR,
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
                        objectResultRepository.updateActionResultId(idOR, id);

                        if (or.getAttributes()!=null) {
                            for (Attribute att : or.getAttributes()) {
                                att.getObjectResult().setId(idOR);
                                try {
                                    attributeProxy.save(att);
                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                            }
                        }


                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
                if (or.getAttributes()!=null) {
                    for (Attribute att : or.getAttributes()) {
                        att.getObjectResult().setId(id);
                        try {
                            attributeProxy.save(att);
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }

        return actionResult;
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
