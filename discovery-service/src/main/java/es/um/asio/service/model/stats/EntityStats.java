package es.um.asio.service.model.stats;

import es.um.asio.service.model.TripleObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class EntityStats {

    // node -> tiple -> class -> AttStats
    Map<String ,Map<String, Map<String, Map<String,AttributeStats>>>> stats;

    public EntityStats() {
        stats = new HashMap<>();
    }

    public void addAttributes(String node, String triple, TripleObject to) {
        if (!stats.containsKey(node))
            stats.put(node, new HashMap<>());
        if (!stats.get(node).containsKey(triple))
            stats.get(node).put(triple, new HashMap<>());
        if (!stats.get(node).get(triple).containsKey(to.getClassName()))
            stats.get(node).get(triple).put(to.getClassName(), new HashMap<>());
        for (Map.Entry<String, Object> attEntity: to.getAttributes().entrySet()) {
            if (!stats.get(node).get(triple).get(to.getClassName()).containsKey(attEntity.getKey()))
                stats.get(node).get(triple).get(to.getClassName()).put(attEntity.getKey(), new AttributeStats(attEntity.getKey()));
            stats.get(node).get(triple).get(to.getClassName()).get(attEntity.getKey()).addValue(String.valueOf(attEntity.getValue()));
        }
    }

    public float getAttributeVariety(String node, String triple, String className,String key) {
        if (stats.get(node).get(triple).get(className).containsKey(key))
            return stats.get(node).get(triple).get(className).get(key).getVariety();
        else return 0f;
    }

    public Map<String,AttributeStats> getAttributesMap(String node, String triple, String className) {
        if (stats.containsKey(node) && stats.get(node).containsKey(triple) && stats.get(node).get(triple).containsKey(className))
            return stats.get(node).get(triple).get(className);
        else
            return null;
    }


    public boolean isEmpty() {
        return stats.isEmpty();
    }

}
