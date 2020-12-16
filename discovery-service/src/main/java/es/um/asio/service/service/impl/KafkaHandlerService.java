package es.um.asio.service.service.impl;

import com.google.gson.JsonObject;
import es.um.asio.service.config.DataProperties;
import es.um.asio.service.model.relational.Action;
import es.um.asio.service.model.relational.ActionResult;
import es.um.asio.service.model.relational.ObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    void sendMessageAction(ActionResult actionResult,String node, String tripleStore, String className) {
        String topic = dataProperties.getKafka().getTopicDiscoveryAction().getTopic();
        for (ObjectResult or : actionResult.getObjectResults()) {
            JsonObject jObjectResult = or.toSimplifiedJson(false);
            JsonObject jMessage = new JsonObject();
            jMessage.addProperty("action",actionResult.getAction().toString());
            jMessage.add("object",jObjectResult);
            if (actionResult.getAction() == Action.LINK) {
                JsonObject jLink = actionResult.getObjectResultParent().toSimplifiedJson(false);
                jMessage.add("linkedTo", jLink);
            }
            String msgStr = jMessage.toString();
            logger.info("Sending message: {}, by node: {}, tripleStore: {}, className: {}",msgStr,node,tripleStore,className);
            kafkaTemplate.send(topic,jMessage.toString());
        }
    }

    @KafkaListener(topics = "${data.kafka.topicEntityChange.topic}", groupId = "${data.kafka.topicEntityChange.groupId}")
    public void onEntityChange(String message) {
        logger.info("On entity Change message: {}", message);
    }

}
