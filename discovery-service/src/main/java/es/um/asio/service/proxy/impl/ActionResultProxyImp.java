package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.*;
import es.um.asio.service.proxy.ActionResultProxy;
import es.um.asio.service.proxy.AttributeProxy;
import es.um.asio.service.proxy.ObjectResultProxy;
import es.um.asio.service.repository.relational.ActionResultRepository;
import es.um.asio.service.repository.relational.ObjectResultRepository;
import es.um.asio.service.repository.relational.custom.ActionResultCustomRepository;
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
public class ActionResultProxyImp implements ActionResultProxy {

    private final Logger logger = LoggerFactory.getLogger(ActionResultProxyImp.class);

    @Autowired
    ActionResultRepository actionResultRepository;


    @Autowired
    ObjectResultRepository objectResultRepository;

    @Autowired
    AttributeProxy attributeProxy;

    @Autowired
    ActionResultCustomRepository actionResultCustomRepository;


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

    @Override
    public Map<Long, Set<ActionResult>> getActionsResultByRequestRegistry(String userId, String requestCode, RequestType requestType) {
        Map<Long, Set<ActionResult>> childActionResults = new HashMap<>();
        Map<Long,ActionResult> jrActionResultMainMap = new HashMap<>();
        Map<Long,ObjectResult> jrObjectResultMap = new HashMap<>();
        Map<Long,Attribute> jrAttributesMainMap = new HashMap<>();
        Map<Long,Value> jrValuesMainMap = new HashMap<>();
        List<Tuple> results = actionResultCustomRepository.getActionResultsByRequestRegistry(userId,requestCode,requestType);
        for (Tuple t : results) {
            long idObjectResultMain = (long) ((t.get("p_or_id")!=null)?(Long.valueOf(t.get("p_or_id").toString())):null);
            if (!childActionResults.containsKey(idObjectResultMain)) {
                childActionResults.put(idObjectResultMain,new HashSet<>());
            }


            // Recovery Action Results Dependents
            long idAction= (long) ((t.get("ac_id")!=null)?(Long.valueOf(t.get("ac_id").toString())):null);
            ActionResult ar = null;
            if (idAction>0) {
                if (!jrActionResultMainMap.containsKey(idAction)) {
                    ar = new ActionResult(t);
                    jrActionResultMainMap.put(ar.getId(),ar);
                } else {
                    ar = jrActionResultMainMap.get(idAction);
                }

                long idObject = (long) ((t.get("or_id")!=null)?(Long.valueOf(t.get("or_id").toString())):null);
                ObjectResult or = null;
                if (idObject>0) {
                    if (!jrObjectResultMap.containsKey(idObject)) {
                        or = new ObjectResult(null,t);
                        jrObjectResultMap.put(or.getId(), or);
                    } else {
                        or = jrObjectResultMap.get(idObject);
                    }
                    ar.getObjectResults().add(or);


                    long idAttribute = (long) ((t.get("at_id") != null) ? (Long.valueOf(t.get("at_id").toString())) : null);
                    Attribute att = null;
                    if (idAttribute > 0) {
                        if (!jrAttributesMainMap.containsKey(idAttribute)) {
                            att = new Attribute(or, null, t);
                            jrAttributesMainMap.put(att.getId(), att);
                        } else {
                            att = jrAttributesMainMap.get(idAttribute);
                        }
                        or.getAttributes().add(att);
                    }

                    long idVal = (long) ((t.get("va_id") != null) ? (Long.valueOf(t.get("va_id").toString())) : null);
                    Value v = null;
                    if (idAttribute > 0) {
                        if (!jrValuesMainMap.containsKey(idVal)) {
                            v = new Value(att, t);
                            jrAttributesMainMap.put(att.getId(), att);
                        } else {
                            v = jrValuesMainMap.get(idAttribute);
                        }
                        att.getValues().add(v);
                    }
                }
            }
            childActionResults.get(idObjectResultMain).add(ar);

        }
        return childActionResults;
    }
}
