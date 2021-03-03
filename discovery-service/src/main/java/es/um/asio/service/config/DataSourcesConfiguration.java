package es.um.asio.service.config;

import com.google.api.client.json.Json;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import es.um.asio.service.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.Connection;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("data-sources") // prefix app, find app.* values
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataSourcesConfiguration {

    private boolean useCachedData;
    private Thresholds thresholds;
    private List<Node> nodes = new ArrayList<>();
    private String urisFactoryHost;
    private String discoveryServiceHost;
    private String serviceDataName;

    public Node getNodeByName(String nodeName) {
        for (Node node: nodes) {
            if (node.getNodeName().equalsIgnoreCase(nodeName))
                return node;
        }
        return null;
    }

    @PostConstruct
    private void postConstruct() {
        try {

            Map<String,String> qParams = new HashMap<>();
            qParams.put("serviceName",serviceDataName);
            JsonElement jResponse = Utils.doRequest(new URL(discoveryServiceHost+"service-discovery/service"), Connection.Method.GET,null,null,qParams,true);
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

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
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

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
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

        public TripleStore getTripleStoreByType(String type) {
            for (TripleStore to: tripleStores) {
                if (to.type.trim().equalsIgnoreCase(type.trim())) {
                    return to;
                }
            }
            return null;
        }

        public String getNodeName() {
            return Utils.normalize(nodeName);
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
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
