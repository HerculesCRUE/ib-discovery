package es.um.asio.service.model.stats;

import es.um.asio.service.model.TripleObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * StatsHandler Class. Is a handler for work with stats. Store in memory the stats of entities in form Node -> TripleStore -> Class -> Entity stats
 * @see TripleObject
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class StatsHandler {


    // node -> tiple -> class -> EntityStat
    private Map<String ,Map<String, Map<String,EntityStats>>> stats;

    /**
     * Constructor
     */
    public StatsHandler() {
        stats = new HashMap<>();
    }

    /**
     * Add attributes to entity Stats
     * @param node String. The node name
     * @param triple The Triple Store name
     * @param to TripleObject. The tripleObject to add attributes to stats
     */
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
            es.setCounter(es.getCounter()+1);
            for (Map.Entry<String, Object> attEntity : to.getAttributes().entrySet()) { // Para todos los attributos
                es.addValue(attEntity.getKey(),attEntity.getValue());
            }

        }
    }


    /**
     * Get EntityStats by node, triple store and class name
     * @param node String. The node name
     * @param triple String. The Triple Store name
     * @param className String. The class name
     * @return EntityStats
     */
    public EntityStats getAttributesMap(String node, String triple, String className) {
        if (stats.containsKey(node) && stats.get(node).containsKey(triple) && stats.get(node).get(triple).containsKey(className))
            return stats.get(node).get(triple).get(className);
        else
            return null;
    }


    /**
     * Check if stats is empty
     * @return boolean
     */
    public boolean isEmpty() {
        return stats.isEmpty();
    }

    /**
     * Clean all calculated stats
     */
    public void cleanStats() {
        stats = new HashMap<>();
    }

    /**
     * Calculate stats by node name, triple store and class
     * @param node String. The node name
     * @param triple String. The Triple Store name
     * @param className String. The class name
     * @return Map<String,Object>. The new stats
     */
    public Map<String,Object> buildStats(String node, String triple, String className) {
        if (this.stats.containsKey(node) && this.stats.get(node).containsKey(triple) && this.stats.get(node).get(triple).containsKey(className)) {
            return this.stats.get(node).get(triple).get(className).buildStats();
        } else
            return null;
    }

    /**
     * Get more relevant Attributes map by node name, triple store and class
     * @param node String. The node name
     * @param triple String. The Triple Store name
     * @param className String. The class name
     * @return Map<String,Float>. The key is the attribute name and the value is the relevance
     */
    public Map<String,Float> generateMoreRelevantAttributesMap(String node, String triple, String className){
        if (this.stats.containsKey(node) && this.stats.get(node).containsKey(triple) && this.stats.get(node).get(triple).containsKey(className)) {
            return this.stats.get(node).get(triple).get(className).generateMoreRelevantAttributesMap(null);
        } else
            return null;
    }



}
