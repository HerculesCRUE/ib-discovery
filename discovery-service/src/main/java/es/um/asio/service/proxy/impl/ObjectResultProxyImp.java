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
        long id = objectResultCustomRepository.persist(or,true);
        or.setId(id);
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
            long idObjectResultMain = ((t.get("orm_id")!=null)?(Long.valueOf(t.get("orm_id").toString())):0);
            if (!childObjectResults.containsKey(idObjectResultMain)) {
                childObjectResults.put(idObjectResultMain,new HashSet<>());
            }

            // Recovery Object Results Dependents
            long idObject= ((t.get("or_id")!=null)?(Long.valueOf(t.get("or_id").toString())):0);
            ObjectResult or = null;
            if (idObject>0) {
                if (!jrObjectResultMainMap.containsKey(idObject)) {
                    or = new ObjectResult(null,t);
                    jrObjectResultMainMap.put(or.getId(),or);
                } else {
                    or = jrObjectResultMainMap.get(idObject);
                }

                long idAttribute= ((t.get("at_id")!=null)?(Long.valueOf(t.get("at_id").toString())):0);
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

                long idVal= ((t.get("va_id")!=null)?(Long.valueOf(t.get("va_id").toString())):0);
                Value v = null;
                if (idVal>0) {
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
