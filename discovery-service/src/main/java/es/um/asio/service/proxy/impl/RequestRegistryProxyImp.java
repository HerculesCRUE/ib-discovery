package es.um.asio.service.proxy.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.ObjectResult;
import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.model.relational.RequestType;
import es.um.asio.service.proxy.RequestRegistryProxy;
import es.um.asio.service.repository.relational.RequestRegistryRepository;
import es.um.asio.service.repository.relational.custom.RequestRegistryCustomRepository;
import es.um.asio.service.service.impl.DataHandlerImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Proxy for RequestRegistry repository implementation.
 * @implNote RequestRegistryProxy
 * @see RequestRegistry
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public class RequestRegistryProxyImp implements RequestRegistryProxy {

    private final Logger logger = LoggerFactory.getLogger(RequestRegistryProxyImp.class);

    @Autowired
    RequestRegistryRepository requestRegistryRepository;

    @Autowired
    RequestRegistryCustomRepository requestRegistryCustomRepository;

    /**
     * Save a request registry in the repository
     * @See RequestRegistry
     * @param RequestRegistry rr
     * @return RequestRegistry
     */
    @Override
    public RequestRegistry save(RequestRegistry rr) {
        try {
            if (rr.getId()!=0) {
                requestRegistryRepository.updateNoNested(rr.getId(), rr.getVersion(), rr.getUserId(), rr.getRequestCode(),
                        rr.getRequestDate(), ((rr.getRequestType()!=null)?rr.getRequestType().toString():RequestType.ENTITY_LINK_CLASS.toString()),
                        rr.isPropagueInKafka(), rr.getWebHook(), ((rr.getJobRegistry()!=null)?rr.getJobRegistry().getId():null) );
            } else {
                long id = requestRegistryRepository.getNextId();
                rr.setId(id);
                requestRegistryRepository.insertNoNested(rr.getId(), rr.getVersion(), rr.getUserId(), rr.getRequestCode(),
                        rr.getRequestDate(), ((rr.getRequestType()!=null)?rr.getRequestType().toString():RequestType.ENTITY_LINK_CLASS.toString()),
                        rr.isPropagueInKafka(), rr.getWebHook(), ((rr.getJobRegistry()!=null)?rr.getJobRegistry().getId():null) );
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return rr;
    }

    /**
     * Find by userId and RequestCode And RequestType
     * @param userId String. The User id.
     * @param requestCode String. The Request code.
     * @param requestType String. The Request type.
     * @return Optional<RequestRegistry>
     */
    @Override
    public Optional<RequestRegistry> findByUserIdAndRequestCodeAndRequestType(String userId, String requestCode, RequestType requestType) {
        Optional<List<RequestRegistry>> requestRegistries = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, requestType);
        if (!requestRegistries.isEmpty() && requestRegistries.get().size()>0) {
            return Optional.of(requestRegistries.get().get(0));
        } else {
            return Optional.empty();
        }
        // return requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, requestType);
    }

    /**
     * Find by userId and RequestCode And RequestType
     * @param userId String. The User id.
     * @param requestCode String. The Request code.
     * @param requestType String. The Request type.
     * @return Optional<RequestRegistry>
     */
    @Override
    public JobRegistry findJobRegistryByUserIdAndRequestCodeAndRequestType(String userId, String requestCode, RequestType requestType) {

        Optional<List<RequestRegistry>> requestRegistries = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, requestType);
        if (!requestRegistries.isEmpty() && requestRegistries.get().size()>0) {
            RequestRegistry registry = Optional.of(requestRegistries.get().get(0)).get();
            return registry.getJobRegistry();
        } else {
            return null;
        }
        // return requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType(userId, requestCode, requestType);
    }

    @Override
    public JsonObject getRequestRegistriesByUserId(String userId) {
        JsonObject jResponse = new JsonObject();
        List<Tuple> results = requestRegistryCustomRepository.getRequestRegistriesByUserId(userId);
        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        for (Tuple t : results) {
            String requestType = t.get("request_type").toString();
            String className = t.get("class_name").toString();
            String requestCode = t.get("request_code").toString();
            Date requestDate = null;
            try {
                requestDate = sdfIn.parse(t.get("request_date").toString());
            } catch (Exception e) {

            }
            if (!jResponse.has(requestType)) {
                jResponse.add(requestType, new JsonObject());
            }
            if (!jResponse.get(requestType).getAsJsonObject().has(className)) {
                jResponse.get(requestType).getAsJsonObject().add(className, new JsonArray());
            }
            JsonObject jItem = new JsonObject();
            jItem.addProperty("requestCode",requestCode);
            if (requestDate!=null)
                jItem.addProperty("requestDate",sdfOut.format(requestDate)+" UTC");
            jResponse.get(requestType).getAsJsonObject().get(className).getAsJsonArray().add(jItem);
        }
        return jResponse;
    }
}
