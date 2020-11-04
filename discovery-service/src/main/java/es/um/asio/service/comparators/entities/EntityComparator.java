package es.um.asio.service.comparators.entities;

import es.um.asio.service.comparators.attribute.AttributeSimilarity;
import es.um.asio.service.model.TripleObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityComparator {

    public static EntitySimilarityObj compare(TripleObject o1, TripleObject o2, Map<String, Float> stats) {
        stats = cleanStat(o1,o2,stats);
        EntitySimilarityObj eso = new EntitySimilarityObj(o2);
        for (Map.Entry<String, Float> eStat : stats.entrySet()) {
            List<Object> vo1 =o1.getValueFromFlattenAttributes(eStat.getKey());
            List<Object> vo2 =o2.getValueFromFlattenAttributes(eStat.getKey());
            SimilarityValue simVal = AttributeSimilarity.compare(vo1,vo2,eStat.getValue());
            eso.addSimilarity(eStat.getKey(),simVal);
        }
        return eso;
    }

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
