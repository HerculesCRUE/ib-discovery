package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.*;
import es.um.asio.service.proxy.*;
import es.um.asio.service.repository.relational.ObjectResultRepository;
import es.um.asio.service.repository.relational.custom.ObjectResultCustomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.util.*;

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

    @Autowired
    ObjectResultCustomRepository objectResultCustomRepository;


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

    @Override
    public Map<Long, Set<ObjectResult>> getDependentObjectResultByRequestRegistry(String userId, String requestCode, RequestType requestType) {
        Map<Long, Set<ObjectResult>> childObjectResults = new HashMap<>();
        Map<Long,ObjectResult> jrObjectResultMainMap = new HashMap<>();
        Map<Long,Attribute> jrAttributesMainMap = new HashMap<>();
        Map<Long,Value> jrValuesMainMap = new HashMap<>();
        List<Tuple> results = objectResultCustomRepository.getDependentObjectResultByRequestRegistry(userId,requestCode,requestType);
        for (Tuple t : results) {
            long idObjectResultMain = (long) ((t.get("orm_id")!=null)?(Long.valueOf(t.get("orm_id").toString())):null);
            if (!childObjectResults.containsKey(idObjectResultMain)) {
                childObjectResults.put(idObjectResultMain,new HashSet<>());
            }

            // Recovery Object Results Dependents
            long idObject= (long) ((t.get("or_id")!=null)?(Long.valueOf(t.get("or_id").toString())):null);
            ObjectResult or = null;
            if (idObject>0) {
                if (!jrObjectResultMainMap.containsKey(idObject)) {
                    or = new ObjectResult(null,t);
                    jrObjectResultMainMap.put(or.getId(),or);
                } else {
                    or = jrObjectResultMainMap.get(idObject);
                }

                long idAttribute= (long) ((t.get("at_id")!=null)?(Long.valueOf(t.get("at_id").toString())):null);
                Attribute att = null;
                if (idAttribute>0) {
                    if (!jrAttributesMainMap.containsKey(idAttribute)) {
                        att = new Attribute(or,null, t);
                        jrAttributesMainMap.put(att.getId(), att);
                    } else {
                        att = jrAttributesMainMap.get(idAttribute);
                    }
                    or.getAttributes().add(att);
                }

                long idVal= (long) ((t.get("va_id")!=null)?(Long.valueOf(t.get("va_id").toString())):null);
                Value v = null;
                if (idAttribute>0) {
                    if (!jrValuesMainMap.containsKey(idVal)) {
                        v = new Value(att, t);
                        jrAttributesMainMap.put(att.getId(), att);
                    } else {
                        v = jrValuesMainMap.get(idAttribute);
                    }
                    att.getValues().add(v);
                }
            }
            childObjectResults.get(idObjectResultMain).add(or);

        }
        return childObjectResults;
    }
}
