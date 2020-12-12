package es.um.asio.service.comparators.entities;

import com.google.gson.Gson;
import es.um.asio.service.comparators.aggregators.AccordSimilarity;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.AttributeStats;
import es.um.asio.service.util.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class EntitySimilarity {

    private EntitySimilarity() {}

    public static EntitySimilarityObj compare(TripleObject to, Map<String, AttributeStats> attributeStatsMap, Object obj1, Object obj2) {
        Gson gson = new Gson();
        EntitySimilarityObj eso  = new EntitySimilarityObj(to);
        Set<String> allKeys = new HashSet<>();
        LinkedHashMap o1 = gson.fromJson(gson.toJson(obj1),LinkedHashMap.class);
        LinkedHashMap o2 = gson.fromJson(gson.toJson(obj2),LinkedHashMap.class);
        allKeys.addAll(o1.keySet());
        for (String key : new ArrayList<String>(o2.keySet())) {
            if (!allKeys.contains(key))
                allKeys.add(key);
        }
        Set<String> intersectionKeys = new HashSet<>();
        List<Float> similarityMetrics = new ArrayList<>();
        float weightAggregate = 0f;
        for (String key : allKeys) {
            if (o1.containsKey(key) && o2.containsKey(key)) {
                intersectionKeys.add(key);
                float weight = 1;
                if (attributeStatsMap!=null && attributeStatsMap.containsKey(key)) {
                    weight = attributeStatsMap.get(key).getRelativeImportanceRatio();
                }
                weightAggregate += weight;
                float sim = compareAtt(to,attributeStatsMap, key, o1.get(key),o2.get(key));
                eso.getSimilarities().put(key,new SimilarityValue(sim,weight));
                similarityMetrics.add(weight*sim);
            } else {
                similarityMetrics.add(0f);
            }

        }
        float similarityResult = ((float) similarityMetrics.stream().mapToDouble(a->a).sum()) / (weightAggregate==0?1:weightAggregate);
        eso.setSimilarity(similarityResult);
        return eso;
    }

    public static float compareAtt(TripleObject to,Map<String, AttributeStats> attributeStatsMap,String key, Object a1, Object a2) {
        if (isNumber(a1) && isNumber(a2)) {
            return compareNumberAtt(((key!=null)?attributeStatsMap.get(key).getRelativeImportanceRatio():0.5f), Float.valueOf(a1.toString()), Float.valueOf(a2.toString()));
        } else if(isBoolean(a1) && isBoolean(a2)) {
            return compareNumberAtt(Boolean.valueOf(a1.toString()), Boolean.valueOf(a2.toString()));
        } else if (isArrayList(a1) && isArrayList(a2)){
            List<Float> fs = compareLists(to,attributeStatsMap,(List) a1,(List) a2 );
            return ((float) fs.stream().mapToDouble(a->a).sum()) / ((float) fs.size());
        } else if (isObject(a1) && isObject(a2)){
            return  EntitySimilarity.compare(to,attributeStatsMap,a1,a2).getSimilarity();
        } else
            return compareNumberAtt(String.valueOf(a1.toString()), String.valueOf(a2.toString()));

    }

    public static float compareNumberAtt(float variability, float a1, float a2) {
        if (a1 == a2)
            return 1;
        if (variability > 0.95) {
            return 0;
        }
        float max = Math.max(a1,a2);
        float min = Math.min(a1,a2);
        float nMax = (max==0)?0:(float) Math.floor((Double.valueOf(max)/max)*10);
        float nMin = (max==0)?0:(float) Math.floor((Double.valueOf(min)/max)*10);
        return (float) Math.pow((0.5f),(nMax-nMin));
    }

    public static float compareNumberAtt(boolean a1, boolean a2) {
        if (a1 == a2)
            return 1.0f;
        else
            return 0f;
    }

    public static float compareNumberAtt(String a1, String a2) {
        if (a1.trim().equalsIgnoreCase(a2.trim()))
            return 1.0f;
        else
            return AccordSimilarity.calculateAccordSimilarity(String.valueOf(a1),String.valueOf(a2));
    }

    public static boolean isNumber(Object o) {
        return Utils.isValidNumber(o.toString());
    }

    public static boolean isBoolean(Object o) {
        return Utils.isBoolean(o.toString());
    }

    public static boolean isObject(Object o) {
        return o instanceof Map;
    }

    public static boolean isArrayList(Object o) {
        return o instanceof List;
    }

    public static List<Float> compareLists(TripleObject to,Map<String, AttributeStats> attributeStatsMap,List ls1, List ls2) {
        List l1 = (ls1.size() >= ls2.size())?ls1:ls2;
        List l2 = (ls1.size() >= ls2.size())?ls2:ls1;
        List<Float> returns = new ArrayList<>();
        if (l2.isEmpty()) { // Si la 2 lista esta vacia he terminado
            for (int i = 0 ; i<l1.size() ; i++) {
                returns.add(0f);
            }
        } else {
            int indexMaxSimilarityL1 = -1;
            int indexMaxSimilarityL2 = -1;
            float maxSimilarity = 0;
            int indexL1 = 0;
            int indexL2 = 0;
            for ( Object o1 : l1) { // Para todos los objetos de l1
                for ( Object o2 : l2) { // Para todos los objetos de l2
                    float similarity = compareAtt(to, attributeStatsMap, null,o1,o2);
                    if (similarity > maxSimilarity) {
                        maxSimilarity = similarity;
                        indexMaxSimilarityL1 = indexL1;
                        indexMaxSimilarityL2 = indexL2;
                    }
                    indexL2++;
                }
                indexL1++;
            }
            l1.remove(indexMaxSimilarityL1);
            l2.remove(indexMaxSimilarityL2);
            returns.add(maxSimilarity);
            if (!l1.isEmpty())
                returns.addAll(compareLists(to,attributeStatsMap,l1,l2));
        }
        return returns;
    }


}
