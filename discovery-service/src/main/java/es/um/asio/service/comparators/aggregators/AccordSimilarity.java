package es.um.asio.service.comparators.aggregators;


import es.um.asio.service.comparators.strings.*;
import es.um.asio.service.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class AccordSimilarity {

    private AccordSimilarity() {}

    static Map<String, Similarity> algorithms;

    public static float calculateAccordSimilarity(String str1, String str2) {
        str1 = Utils.normalize(str1);
        str2 = Utils.normalize(str2);
        Map<String,Float> metrics = new HashMap();
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
