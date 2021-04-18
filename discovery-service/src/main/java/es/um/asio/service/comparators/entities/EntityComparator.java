package es.um.asio.service.comparators.entities;

import es.um.asio.service.comparators.attribute.AttributeSimilarity;
import es.um.asio.service.model.TripleObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the Entity comparator
 * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-en-comparaci%C3%B3n-de-entidades"
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public class EntityComparator {

    /**
     * The default constructor
     */
    private EntityComparator(){}


    /**
     * The algorithm compare entities applying the stats
     * @see TripleObject
     * @see EntitySimilarityObj
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-en-comparaci%C3%B3n-de-entidades"
     * @param o1 TripleObject the first entity to compare
     * @param o2 TripleObject the second entity to compare
     * @param stats Map<String, Float> with the attribute name as key and the weight of the attribute as value
     * @return EntitySimilarityObj
     */
    // TODO: Es posible que interese cambiar el comportamiento si es un identificador
    public static EntitySimilarityObj compare(TripleObject o1, TripleObject o2, Map<String, Float> stats) {
        stats = cleanStat(o1,o2,stats);
        EntitySimilarityObj eso = new EntitySimilarityObj(o2);
        for (Map.Entry<String, Float> eStat : stats.entrySet()) {
            List<Object> vo1 =o1.getValueFromFlattenAttributes(eStat.getKey());
            List<Object> vo2 =o2.getValueFromFlattenAttributes(eStat.getKey());
            SimilarityValue simVal = AttributeSimilarity.compare(vo1, vo2, eStat.getValue());
            eso.addSimilarity(eStat.getKey(),simVal);
        }
        return eso;
    }

    /**
     * The algorithm calculate new stats weights with the union of attributes between the TripleObjects pass in parameters
     * @see TripleObject
     * @param to1 TripleObject the first entity to compare
     * @param to2 TripleObject the second entity to compare
     * @param stats Map<String, Float> with the attribute name as key and the weight of the attribute as value
     * @return Map<String, Float> with the new stats of the union of the TripleObjects. The attribute name is the key and the weight of the attribute is the value
     */
    private static Map<String, Float> cleanStat(TripleObject to1,TripleObject to2,Map<String, Float> stats) {
        Map<String,Float> statAux = new HashMap<>();
        for (Map.Entry<String, Float> statEntry: stats.entrySet()) {
            if (to1.checkIfHasAttribute(statEntry.getKey())  || to2.checkIfHasAttribute(statEntry.getKey())) {
                statAux.put(statEntry.getKey(),statEntry.getValue());
            }
        }
        float sumStats = statAux.values().stream().reduce(0f, Float::sum);
        statAux.replaceAll((k, v) -> v/sumStats);
        return statAux;
    }
}
