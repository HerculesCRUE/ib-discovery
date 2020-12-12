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
public class StatsHandler {


    // node -> tiple -> class -> EntityStat
    private Map<String ,Map<String, Map<String,EntityStats>>> stats;

    public StatsHandler() {
        stats = new HashMap<>();
    }

    public void addAttributes(String node, String triple, TripleObject to) {
        if (to != null && to.getAttributes() != null) {
            if (!stats.containsKey(node))
                stats.put(node, new HashMap<>());
            if (!stats.get(node).containsKey(triple))
                stats.get(node).put(triple, new HashMap<>());
            if (!stats.get(node).get(triple).containsKey(to.getClassName())) {
                stats.get(node).get(triple).put(to.getClassName(), new EntityStats(to.getClassName()));
            }
            EntityStats es = stats.get(node).get(triple).get(to.getClassName());
            for (Map.Entry<String, Object> attEntity : to.getAttributes().entrySet()) { // Para todos los attributos
                es.addValue(attEntity.getKey(),attEntity.getValue());
            }

        }
    }


    public EntityStats getAttributesMap(String node, String triple, String className) {
        if (stats.containsKey(node) && stats.get(node).containsKey(triple) && stats.get(node).get(triple).containsKey(className))
            return stats.get(node).get(triple).get(className);
        else
            return null;
    }


    public boolean isEmpty() {
        return stats.isEmpty();
    }

    public void cleanStats() {
        stats = new HashMap<>();
    }

    public Map<String,Object> buildStats(String node, String triple, String className) {
        if (this.stats.containsKey(node) && this.stats.get(node).containsKey(triple) && this.stats.get(node).get(triple).containsKey(className)) {
            return this.stats.get(node).get(triple).get(className).buildStats();
        } else
            return null;
    }

    public Map<String,Float> generateMoreRelevantAttributesMap(String node, String triple, String className){
        if (this.stats.containsKey(node) && this.stats.get(node).containsKey(triple) && this.stats.get(node).get(triple).containsKey(className)) {
            return this.stats.get(node).get(triple).get(className).generateMoreRelevantAttributesMap(null);
        } else
            return null;
    }



}
