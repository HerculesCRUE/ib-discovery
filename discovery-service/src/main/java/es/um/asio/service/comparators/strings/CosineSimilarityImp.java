package es.um.asio.service.comparators.strings;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

import java.util.Arrays;

import static org.simmetrics.builders.StringMetricBuilder.with;

/**
 * This class implements the Cosine Similarity algorithm to compare Strings
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public class CosineSimilarityImp implements Similarity {


    /**
     *
     *  This method calculate the similarity using Cosine Similarity Algorithm in (0,1) range.
     *  description: The algorithm is based on measuring the cosine between the distance from strings A to string B. The vector is formed by counting the words in the two strings
     *  Advantage: Very good with mixed String
     *  Drawbacks: Works poorly with abbreviations or character changes
     * @param str1 : fist sting to compare similarity
     * @param str2: second String to compare Similarity
     * @return float as similitude measure in range (0,1)
     */
    @Override
    public float calculateSimilarity(String str1, String str2) {
        StringMetric metric =
                with(new CosineSimilarity<>())
                        .simplify(Simplifiers.toLowerCase())
                        .simplify(Simplifiers.replaceNonWord())
                        .tokenize(Tokenizers.whitespace())
                        .build();
        return Math.max(new MongeElkan(metric).compare(Arrays.asList(str1.split(" " )), Arrays.asList(str2.split(" " ))),metric.compare(str1,str2));
    }

}
