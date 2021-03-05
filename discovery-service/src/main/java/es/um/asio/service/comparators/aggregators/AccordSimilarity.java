package es.um.asio.service.comparators.aggregators;


import es.um.asio.service.comparators.strings.*;
import es.um.asio.service.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements Accord Similarity between Strings using all algorithms describes
 * @see es.um.asio.service.comparators.strings
 * @link https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-para-atributos
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public class AccordSimilarity {

    /**
     * Default constructor
     */
    private AccordSimilarity() {}

    /**
     * algorithms map, the key is the name of algorithm and the value is the value of the similarity
     */
    static Map<String, Similarity> algorithms;


    /**
     * @param str1 The first string to compare
     * @param str2 The second string to compare
     * @return float as the value of the similarity by accord
     */
    public static float calculateAccordSimilarity(String str1, String str2) {
        str1 = Utils.normalize(str1);
        str2 = Utils.normalize(str2);
        Map<String,Float> metrics = new HashMap<>();
        for (Map.Entry<String, Similarity> e : getAlgorithms().entrySet()) {
            metrics.put(e.getKey(), e.getValue().calculateSimilarity(str1,str2));
        }
        List<Float> lMetrics = new ArrayList<>(metrics.values());
        List<Float> filtered = lMetrics.stream().filter(m-> m >= 0.6f).collect(Collectors.toList());
        if (filtered.size() >=5 )
            Collections.sort(lMetrics,Collections.reverseOrder());
        else
            Collections.sort(lMetrics);
        return getDescendantWeightMean(lMetrics,(1f/3f));
    }


    /**
     * @see Similarity
     * @return Map<String,Similarity> with the name of the algorithm and the implementation
     */
    public static Map<String,Similarity>  getAlgorithms() {
        if (algorithms != null)
            return algorithms;
        else {
            algorithms = new HashMap<>();
            algorithms.put("BlockDistanceSimilarityImp",new BlockDistanceSimilarityImp());
            algorithms.put("CosineSimilarityImp",new CosineSimilarityImp());
            algorithms.put("DiceDistanceSimilarityImp",new DiceDistanceSimilarityImp());
            algorithms.put("EuclideanDistanceSimilarityImp",new EuclideanDistanceSimilarityImp());
            algorithms.put("GeneralizedJaccardSimilarityImp",new GeneralizedJaccardSimilarityImp());
            algorithms.put("JaccardSimilarityImp",new JaccardSimilarityImp());
            algorithms.put("JaroWinklerSimilarityImp",new JaroWinklerSimilarityImp());
            algorithms.put("LevenshteinSimilarityImp",new LevenshteinSimilarityImp());
            algorithms.put("LongestCommonSubsequenceSimilarityImp",new LongestCommonSubsequenceSimilarityImp());
            algorithms.put("LongestCommonSubStringSimilarityImp",new LongestCommonSubStringSimilarityImp());
            algorithms.put("OverlapCoefificientSimilarityImp",new OverlapCoefficientSimilarityImp());
            algorithms.put("SimonWhiteSimilarityImp",new SimonWhiteSimilarityImp());
            algorithms.put("SmithWetermanGotohSimilarityImp",new SmithWetermanGotohSimilarityImp());
            algorithms.put("SmithWetermanSimilarityImp",new SmithWetermanSimilarityImp());
            return algorithms;
        }
    }

    /**
     * The method calculate the Weighted average of similitudes
     * @param l List<Float> of the similitude of algorithms
     * @param ratio Threshold for evaluate if the similarity is positive or negative
     * @return float. The weighted mean value of similitaries
     */
    public static float getDescendantWeightMean(List<Float> l, float ratio) {
        float mean = 0f;
        float w = 1f;
        float p = 0f;
        for (int i = 0; i < l.size()-2 ; i++) {
            p = w*ratio;
            w = w-p;
            mean += l.get(i) * p;
        }
        mean +=  l.get(l.size()-2) * p;
        mean +=  l.get(l.size()-2) * p;
        return mean;
    }

}
