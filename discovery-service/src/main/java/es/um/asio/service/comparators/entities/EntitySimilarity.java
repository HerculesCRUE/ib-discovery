package es.um.asio.service.comparators.entities;

import com.google.gson.Gson;
import es.um.asio.service.comparators.aggregators.AccordSimilarity;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.AttributeStats;
import es.um.asio.service.util.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * This class implements the statics methods by calculate the entity similitude
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
public class EntitySimilarity {

    /**
     * The default constructor
     */
    private EntitySimilarity() {}

    /**
     * The method compare two entities
     * @see TripleObject
     * @see AttributeStats
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-en-comparaci%C3%B3n-de-entidades"
     * @param to: TripleObject from which the entity comparison will be made
     * @param attributeStatsMap Map<String, AttributeStats> Stats of Attributes. The attribute name is the key, and the value is a AttributeStats Object
     * @param obj1 Object first attributes to compare. The attributes is the attribute value of the TripleObject pass as parameter
     * @param obj2 Object second attributes to compare
     * @return EntitySimilarityObj with the similitude between entities
     */
    public static EntitySimilarityObj compare(TripleObject to, Map<String, AttributeStats> attributeStatsMap, Object obj1, Object obj2) {
        Gson gson = new Gson();
        EntitySimilarityObj eso  = new EntitySimilarityObj(to);
        Set<String> allKeys = new HashSet<>();
        LinkedHashMap<String,Object> o1 = gson.fromJson(gson.toJson(obj1),LinkedHashMap.class);
        LinkedHashMap<String,Object>  o2 = gson.fromJson(gson.toJson(obj2),LinkedHashMap.class);
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

    /**
     * The method compare two attributes
     * @see TripleObject
     * @see AttributeStats
     * @param to: TripleObject from which the entity comparison will be made
     * @param attributeStatsMap Map<String, AttributeStats> Stats of Attributes. The attribute name is the key, and the value is a AttributeStats Object
     * @param key The name of the attribute
     * @param a1 Object first attribute to compare.
     * @param a2 Object second attribute to compare.
     * @return float with the similarity value in range (0,1)
     */
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


    /**
     * The method compare two attributes of type Numeric
     * @param variability variability ratio of the attributes
     * @param a1: first number to compare
     * @param a2: second number to compare
     * @return float with the similarity value in range (0,1)
     */
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

    /**
     * The method compare two attributes of type Boolean
     * @param a1 boolean first boolean to compare
     * @param a2 boolean second boolean to compare
     * @return float with the similarity value in range (0,1)
     */
    public static float compareNumberAtt(boolean a1, boolean a2) {
        if (a1 == a2)
            return 1.0f;
        else
            return 0f;
    }

    /**
     * The method compare two attributes of type String
     * @param a1 String first boolean to compare
     * @param a2 String second boolean to compare
     * @return float with the similarity value in range (0,1)
     */
    public static float compareNumberAtt(String a1, String a2) {
        if (a1.trim().equalsIgnoreCase(a2.trim()))
            return 1.0f;
        else
            return AccordSimilarity.calculateAccordSimilarity(String.valueOf(a1),String.valueOf(a2));
    }

    /**
     * The method check if the object is Number type
     * @param o The Object to check
     * @return true is the attribute is of type
     */
    public static boolean isNumber(Object o) {
        return Utils.isValidNumber(o.toString());
    }

    /**
     * The method check if the object is Boolean type
     * @param o The Object to check
     * @return true is the attribute is of type
     */
    public static boolean isBoolean(Object o) {
        return Utils.isBoolean(o.toString());
    }

    /**
     * The method check if the object is Object type
     * @param o The Object to check
     * @return true is the attribute is of type
     */
    public static boolean isObject(Object o) {
        return o instanceof Map;
    }

    /**
     * The method check if the object is List type
     * @param o The Object to check
     * @return true is the attribute is of type
     */
    public static boolean isArrayList(Object o) {
        return o instanceof List;
    }


    /**
     * The method compare two attributes of type List<Object>
     * @see TripleObject
     * @see AttributeStats
     * @param to: TripleObject from which the entity comparison will be made
     * @param attributeStatsMap Map<String, AttributeStats> Stats of Attributes. The attribute name is the key, and the value is a AttributeStats Object
     * @param ls1 Object first List to compare.
     * @param ls2 Object second List to compare.
     * @return float with the similarity value in range (0,1)
     */
    public static List<Float> compareLists(TripleObject to,Map<String, AttributeStats> attributeStatsMap,List<Object> ls1, List<Object> ls2) {
        List<Object> l1 = (ls1.size() >= ls2.size())?ls1:ls2;
        List<Object> l2 = (ls1.size() >= ls2.size())?ls2:ls1;
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
