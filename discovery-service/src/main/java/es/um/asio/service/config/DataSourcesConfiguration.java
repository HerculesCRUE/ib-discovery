package es.um.asio.service.config;

import es.um.asio.service.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    public Node getNodeByName(String nodeName) {
        for (Node node: nodes) {
            if (node.getNodeName().equalsIgnoreCase(nodeName))
                return node;
        }
        return null;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Thresholds {
        private double manualThreshold;
        private double automaticThreshold;
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

        }

    }

}
