package es.um.asio.service.comparators.strings;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.EuclideanDistance;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

import java.util.Arrays;

import static org.simmetrics.builders.StringMetricBuilder.with;

/**
 * This class implements the Euclidean Distance Similarity algorithm to compare Strings
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public class EuclideanDistanceSimilarityImp implements Similarity {


    /**
     *
     *  This method calculate the similarity using Euclidean Distance Algorithm in (0,1) range.
     *  description: The algorithm is based on measuring the Euclidean distance from strings A to string B.
     *  Advantage: Very good with mixed String
     *  Drawbacks: Works poorly with abbreviations or character changes
     * @param str1 : fist sting to compare similarity
     * @param str2: second String to compare Similarity
     * @return float as similitude measure in range (0,1)
     */
    @Override
    public float calculateSimilarity(String str1, String str2) {
        StringMetric metric =
                with(new EuclideanDistance<>())
                        .simplify(Simplifiers.toLowerCase())
                        .simplify(Simplifiers.replaceNonWord())
                        .tokenize(Tokenizers.whitespace())
                        .build();
        return Math.max(new MongeElkan(metric).compare(Arrays.asList(str1.split(" " )), Arrays.asList(str2.split(" " ))),metric.compare(str1,str2));
    }

}
