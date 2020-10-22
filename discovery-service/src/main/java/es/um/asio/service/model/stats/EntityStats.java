package es.um.asio.service.model.stats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.um.asio.service.util.Utils;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EntityStats extends ObjectStat{

    Map<String,AttributeStats> attValues;
    Map<String,EntityStats> objValues;

    public EntityStats(String name) {
        setName(name);
        setCounter(0);
        attValues = new HashMap<>();
        objValues = new HashMap<>();
    }

    public void addValue(String name,Object value) {
        if (getName().equals("Proyecto") && name.contains("nombre"))
            System.out.println();
        setCounter(getCounter()+1);
        if (Utils.isPrimitive(value)) { // Si es primitivo
            addAttValue(name,value);
        } else { // Si es un objeto
            addObjValue(name,value);
        }
    }

    private void addAttValue(String name,Object value) {
        setCounter(getCounter()+1);
        AttributeStats attributeStats;
        if (!attValues.containsKey(name))
            attributeStats = new AttributeStats(name);
        else
            attributeStats = attValues.get(name);
        attributeStats.addValue(value);
        attValues.put(name,attributeStats);
    }

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
            System.out.println();
        }
    }

    @Override
    public float getRelativeImportanceRatio() {
        float attrsRatio = 0f;
        if (!attValues.isEmpty()) {
            for (AttributeStats attributeStats : attValues.values()) {
                attrsRatio += attributeStats.getRelativeImportanceRatio();
            }
        }

        float entityRatio = 0f;
        if (!objValues.isEmpty()) {
            for (EntityStats entityStats : objValues.values()) {
                entityRatio += entityStats.getRelativeImportanceRatio();
            }
        }
        return (attrsRatio+entityRatio)/(attValues.size()+objValues.size());
    }

    public Map<String,Object> buildStats() {
        Map<String,Object> stats = new HashMap<>();
        stats.put("relativeRatio",getRelativeImportanceRatio());
        stats.put("attributesSize",attValues.size());
        stats.put("entitiesSize",objValues.size());
        stats.put("isEmpty",(attValues.size()+objValues.size())==0);
        if (!attValues.isEmpty()) {
            stats.put("attributes", new HashMap<>());
            for (AttributeStats attributeStats : attValues.values()) {
                ((Map) stats.get("attributes")).put(attributeStats.getName(),attributeStats.getRelativeImportanceRatio());
            }
        }

        float entityRatio = 0f;
        if (!objValues.isEmpty()) {
            stats.put("entities", new HashMap<>());
            for (EntityStats entityStats : objValues.values()) {
                ((Map) stats.get("entities")).put(entityStats.getName(),entityStats.buildStats());
            }
        }
        return stats;

    }
}
