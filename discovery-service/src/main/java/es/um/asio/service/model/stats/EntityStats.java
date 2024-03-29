package es.um.asio.service.model.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.um.asio.service.util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * EntityStats Class. Model the stats of entities. Can be recursive. Extends of ObjectStat
 * @see ObjectStat
 * @see AttributeStats
 * @see EntityStats
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
public class EntityStats extends ObjectStat{

    Map<String,AttributeStats> attValues;
    Map<String,EntityStats> objValues;
    private int counter;

    private final Logger logger = LoggerFactory.getLogger(EntityStats.class);

    /**
     * Constructor
     * @see ObjectStat
     * @param name. String. The name in ObjectStat
     */
    public EntityStats(String name) {
        setName(name);
        setCounter(0);
        attValues = new HashMap<>();
        objValues = new HashMap<>();
    }

    /**
     * Add value
     * @param name String. The attribute name
     * @param value Object. The value
     */
    public void addValue(String name,Object value) {
        //setCounter(getCounter()+1);
        if (Utils.isPrimitive(value)) { // Si es primitivo
            addAttValue(name,value);
        } else { // Si es un objeto
            addObjValue(name,value);
        }
    }

    /**
     * Add Attribute value
     * @param name String. The attribute name
     * @param value Object. The value
     */
    private void addAttValue(String name,Object value) {
        // setCounter(getCounter()+1);
        AttributeStats attributeStats;
        if (!attValues.containsKey(name)) {
            attributeStats = new AttributeStats(name);
        }
        else {
            attributeStats = attValues.get(name);
        }
        attributeStats.addValue(value);
        attValues.put(name,attributeStats);
    }


    /**
     * Add Object value
     * @param name String. The attribute name
     * @param value Object. The value
     */
    private void addObjValue(String name,Object value) {
        setCounter(getCounter()+1);

        EntityStats entityStats;
        if (!objValues.containsKey(name))
            entityStats = new EntityStats(name);
        else
            entityStats = objValues.get(name);
        try {
            if (!(value instanceof List)) { // Si no es una lista
                Map<String, Object> attrs = new ObjectMapper().convertValue(value, Map.class); // Obtengo los atributos
                for (Map.Entry<String, Object> att : attrs.entrySet()) { // Por cada atributo
                    entityStats.addValue(att.getKey(),att.getValue());
                }
                objValues.put(entityStats.getName(),entityStats);
            } else { // Si es una lista
                for (Object v : (List) value) {
                    addValue(name,v);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Calculate Relative importance for entity
     * @return float. The relative importance
     */
    @Override
    public float getRelativeImportanceRatio() {
        float maxAtt = getAttRelativeImportanceRatio();
        float maxEnt = getEntRelativeImportanceRatio();
        return Math.max(maxAtt,maxEnt);
    }

    /**
     * Calculate Relative importance for attributes
     * @return float. The relative importance
     */
    public float getAttRelativeImportanceRatio(){
        List<Float> attrsRatio = new ArrayList<>();
        if (!attValues.isEmpty()) {
            for (AttributeStats attributeStats : attValues.values()) {
                float ratio = attributeStats.getRelativeImportanceRatio();
                attrsRatio.add(ratio);
            }
        }
        return !attrsRatio.isEmpty()?Collections.max(attrsRatio):0f;
    }

    /**
     * Calculate Relative importance for entity
     * @return float. The relative importance
     */
    public float getEntRelativeImportanceRatio(){
        List<Float> entityRatio = new ArrayList<>();
        if (!objValues.isEmpty()) {
            for (EntityStats entityStats : objValues.values()) {
                float ratio = entityStats.getRelativeImportanceRatio();
                entityRatio.add(ratio);
            }
        }
        return !entityRatio.isEmpty()?Collections.max(entityRatio):0f;
    }

    /**
     * Build stats for all attributes
     * @return Map<String,Object>. The stats. The key is the kpi of the stat and the value is the stat
     */
    public Map<String,Object> buildStats() {
        Map<String,Object> stats = new HashMap<>();
        stats.put("maxRelativeRatio",getRelativeImportanceRatio());
        stats.put("maxAttributesRelativeRatio",getAttRelativeImportanceRatio());
        stats.put("maxEntitiesRelativeRatio",getEntRelativeImportanceRatio());
        stats.put("attributesSize",attValues.size());
        stats.put("entitiesSize",objValues.size());
        stats.put("instances",getCounter());
        stats.put("isEmpty",(attValues.size()+objValues.size())==0);
        if (!attValues.isEmpty()) {
            stats.put("attributes", new HashMap<>());
            for (AttributeStats attributeStats : attValues.values()) {
                ((Map) stats.get("attributes")).put(attributeStats.getName(),attributeStats.getRelativeImportanceRatio());
            }
        }

        if (!objValues.isEmpty()) {
            stats.put("entities", new HashMap<>());
            for (EntityStats entityStats : objValues.values()) {
                ((Map) stats.get("entities")).put(entityStats.getName(),entityStats.buildStats());
            }
        }
        return stats;
    }

    /**
     * Generate more relevant stats of attributes
     * @param prefix string. generatee prefix for work with flatten attributes
     * @return Map<String,Float>. More relevant stats of attributes
     */
    public Map<String,Float> generateMoreRelevantAttributesMap(String prefix, Map<String, Float> otherStats){
        List<String> oStats = null;
        if (otherStats != null) {
            oStats = Arrays.asList(otherStats.keySet().toArray()).stream().map( m -> (m.toString())).collect(Collectors.toList());
        }
        String p;
        if (Utils.isValidString(prefix)) {
            if ((prefix.charAt(prefix.length()-1)=='.'))
                p = prefix;
            else
                p = prefix+".";
        } else
            p = "";
        Map<String,Float> attrs = new TreeMap<>();
        for (Map.Entry<String, AttributeStats> att : attValues.entrySet()) { // Para cada atributo
            String a = att.getKey();
            if (otherStats == null || oStats.contains(att.getKey())) {
                //if (!att.getKey().startsWith("Id") &&  !att.getKey().endsWith("Id")) {
                    attrs.put(p + att.getKey(), att.getValue().getRelativeImportanceRatio());
                //}
            }
        }
        for ( Map.Entry<String, EntityStats> obj : objValues.entrySet()) {
            if (otherStats == null ||  otherStats.containsKey(obj.getKey())) {
                attrs.putAll(obj.getValue().generateMoreRelevantAttributesMap(Utils.isValidString(p)?p+obj.getValue().getName():obj.getValue().getName(),otherStats));
            }
        }
        attrs = Utils.sortByValues(attrs);
        return attrs;
    }
}
