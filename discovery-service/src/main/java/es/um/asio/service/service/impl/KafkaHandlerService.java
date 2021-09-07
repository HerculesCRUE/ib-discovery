package es.um.asio.service.service.impl;

import com.google.gson.JsonObject;
import es.um.asio.service.config.DataProperties;
import es.um.asio.service.model.relational.Action;
import es.um.asio.service.model.relational.ActionResult;
import es.um.asio.service.model.relational.ObjectResult;
import es.um.asio.service.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * KafkaHandlerService implementation. Handle to publish or subscribe in Topics
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public class KafkaHandlerService {

    private final Logger logger = LoggerFactory.getLogger(KafkaHandlerService.class);

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    DataProperties dataProperties;

    @PostConstruct
    public void init() {
        logger.info("initialized kafka handler service");
    }

    /**
     * Send a message to publish a new action
     * @see ActionResult
     * @param actionResult ActionResult. The action result
     * @param node String. The node where the action is needed
     * @param tripleStore String. The triple store where the action is needed
     * @param className String. The class name where the action is needed
     */
    void sendMessageAction(ActionResult actionResult,String node, String tripleStore, String className) {
        String topic = dataProperties.getKafka().getTopicDiscoveryAction().getTopic();
        for (ObjectResult or : actionResult.getObjectResults()) {
            JsonObject jObjectResult = or.toSimplifiedJson(false);
            JsonObject jMessage = new JsonObject();
            jMessage.addProperty("action",actionResult.getAction().toString());
            if (actionResult.getAction() == Action.DELETE || actionResult.getAction() == Action.UPDATE) {
                try {
                    String localURI = jObjectResult.get("localUri").getAsString();
                    if (Utils.isValidString(localURI)) {
                        String[] uriChunk = localURI.split("/");
                        if (uriChunk.length > 1) {
                            jObjectResult.addProperty("className", uriChunk[uriChunk.length - 2]);
                        }
                    }
                } catch (Exception e) {

                }
            }
            jMessage.add("linkedTo",jObjectResult);
            if (actionResult.getAction() == Action.LINK) {
                JsonObject jLink = actionResult.getObjectResultParent().toSimplifiedJson(false);
                jMessage.add("object", jLink);
            }
            String msgStr = jMessage.toString();
            logger.info(": {}, by node: {}, tripleStore: {}, className: {}",msgStr,node,tripleStore,className);
            kafkaTemplate.send(topic,jMessage.toString());
        }
    }

    /**
     * Subscriber to entity changes
     * @param message String. The message of the changes on entity
     */
    @KafkaListener(topics = "${data.kafka.topicEntityChange.topic}", groupId = "${data.kafka.topicEntityChange.groupId}")
    public void onEntityChange(String message) {
        logger.info("On entity Change message: {}", message);
    }

}
