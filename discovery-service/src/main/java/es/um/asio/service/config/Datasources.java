package es.um.asio.service.config;

import com.google.api.client.json.Json;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import es.um.asio.service.util.Utils;
import lombok.*;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Sources Configuration configuration properties.
 * @see Thresholds
 * @see Node
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Component
@ConfigurationProperties("data-sources") // prefix app, find app.* values
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Datasources {

    private boolean useCachedData;
    private Thresholds thresholds;
    private List<Node> nodes = new ArrayList<>();
    @Value("${app.uris-factory-host}")
    private String urisFactoryHost;
    @Value("${app.discovery-service-host}")
    private String discoveryServiceHost; //dsh;
    @Value("${app.service-data-name}")
    private String serviceDataName;

    @ToString.Exclude
    private static final Logger logger = LoggerFactory.getLogger(Datasources.class);

    /**
     * @see Node
     * @param nodeName String
     * @return Node
     */
    public Node getNodeByName(String nodeName) {
        for (Node node: nodes) {
            if (node.getNodeName().equalsIgnoreCase(nodeName))
                return node;
        }
        return null;
    }

    /**
     * After initialize do request to Service-Discovery to get Nodes
     * @see Node
     * link "https://github.com/HerculesCRUE/ib-service-discovery"
     */
    @PostConstruct
    private void postConstruct() {
        try {

            Map<String,String> qParams = new HashMap<>();
            qParams.put("serviceName",getServiceDataName());
            logger.info("DataSource: "+ this.toString());
            JsonElement jResponse = Utils.doRequest(new URL(getDiscoveryServiceHost()+"service-discovery/service"), Connection.Method.GET,null,null,qParams,true);
            if (jResponse!=null && jResponse.isJsonArray()) {
                for (JsonElement jeNode : jResponse.getAsJsonArray()) {
                    Node n = new Node(jeNode.getAsJsonObject());
                    this.nodes.add(n);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thresholds Configuration configuration properties.
     * @author  Daniel Ruiz Santamaría
     * @version 2.0
     * @since   1.0
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class Thresholds {
        private double manualThreshold;
        private double automaticThreshold;
        private double manualThresholdWithOutId;
        private double automaticThresholdWithOutId;
        private double elasticSearchAttributesThresholdSimple;
        private double elasticSearchAttributesNumberRatioSimple;
        private double elasticSearchAttributesThresholdComplex;
        private double elasticSearchAttributesNumberRatioComplex;
        private double elasticSearchMaxDesirableNumbersOfResults;
        private double elasticSearchCutOffAccordPercentile;

        /*public void setManualThreshold(String manualThreshold) {
            this.manualThreshold = Double.valueOf(manualThreshold);
        }

        public void setAutomaticThreshold(String automaticThreshold) {
            this.automaticThreshold = Double.valueOf(automaticThreshold);
        }

        public void setManualThresholdWithOutId(String manualThresholdWithOutId) {
            this.manualThresholdWithOutId = Double.valueOf(manualThresholdWithOutId);
        }

        public void setAutomaticThresholdWithOutId(String automaticThresholdWithOutId) {
            this.automaticThresholdWithOutId = Double.valueOf(automaticThresholdWithOutId);
        }

        public void setElasticSearchAttributesThresholdSimple(String elasticSearchAttributesThresholdSimple) {
            this.elasticSearchAttributesThresholdSimple = Double.valueOf(elasticSearchAttributesThresholdSimple);
        }

        public void setElasticSearchAttributesNumberRatioSimple(String elasticSearchAttributesNumberRatioSimple) {
            this.elasticSearchAttributesNumberRatioSimple = Double.valueOf(elasticSearchAttributesNumberRatioSimple);
        }

        public void setElasticSearchAttributesThresholdComplex(String elasticSearchAttributesThresholdComplex) {
            this.elasticSearchAttributesThresholdComplex = Double.valueOf(elasticSearchAttributesThresholdComplex);
        }

        public void setElasticSearchAttributesNumberRatioComplex(String elasticSearchAttributesNumberRatioComplex) {
            this.elasticSearchAttributesNumberRatioComplex = Double.valueOf(elasticSearchAttributesNumberRatioComplex);
        }

        public void setElasticSearchMaxDesirableNumbersOfResults(String elasticSearchMaxDesirableNumbersOfResults) {
            this.elasticSearchMaxDesirableNumbersOfResults = Double.valueOf(elasticSearchMaxDesirableNumbersOfResults);
        }

        public void setElasticSearchCutOffAccordPercentile(String elasticSearchCutOffAccordPercentile) {
            this.elasticSearchCutOffAccordPercentile = Double.valueOf(elasticSearchCutOffAccordPercentile);
        }*/
    }



    /**
     * Node Configuration configuration properties.
     * @see Node
     * @see TripleStore
     * @author  Daniel Ruiz Santamaría
     * @version 2.0
     * @since   1.0
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class Node {
        private String nodeName;
        private String serviceName;
        private List<TripleStore> tripleStores = new ArrayList<>();

        public Node(JsonObject jNode){
            if (jNode.has("name"))
                this.nodeName =  jNode.get("name").getAsString();
            if (jNode.has("services")) {
                for (JsonElement jeService : jNode.get("services").getAsJsonArray()) {
                    JsonObject jService = jeService.getAsJsonObject();
                    String host = null,port = null;
                    if (jService.has("baseURL"))
                        host = jService.get("baseURL").getAsString();
                    if (jService.has("port"))
                        port = jService.get("port").getAsString();
                    if (jService.has("name"))
                        this.serviceName = jService.get("name").getAsString();
                    if (jService.has("types")) {
                        for (JsonElement jeType : jService.get("types").getAsJsonArray()) {
                            JsonObject jType = jeType.getAsJsonObject();
                            TripleStore ts = new TripleStore(host,port,jType);
                            this.tripleStores.add(ts);
                        }
                    }
                }
            }
        }

        /**
         * Filter TripleStore by name
         * @param type
         * @return
         */
        public TripleStore getTripleStoreByType(String type) {
            for (TripleStore to: tripleStores) {
                if (to.type.trim().equalsIgnoreCase(type.trim())) {
                    return to;
                }
            }
            return null;
        }

        /**
         * @return String. The node name
         */
        public String getNodeName() {
            return Utils.normalize(nodeName);
        }

        /**
         * TripleStore Configuration configuration properties.
         * @author  Daniel Ruiz Santamaría
         * @version 2.0
         * @since   1.0
         */
        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        @ToString
        public static class TripleStore {
            private String type;
            private String baseURL;
            private String name;
            private String user;
            private String password;
            private String suffixURL;

            public TripleStore(String host,String port, JsonObject jType) {
                if (jType.has("name")) {
                    type = jType.get("name").getAsString();
                    name = jType.get("name").getAsString();
                }
                baseURL = host+":"+port;
                if (jType.has("suffixURL"))
                    suffixURL = jType.get("suffixURL").getAsString();
            }

        }

    }

}
