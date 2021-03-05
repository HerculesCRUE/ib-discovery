package es.um.asio.service.comparators.strings;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.metrics.NeedlemanWunch;
import org.simmetrics.simplifiers.Simplifiers;

import java.util.Arrays;

import static org.simmetrics.builders.StringMetricBuilder.with;

/**
 * This class implements the similitude of Needleman Wunch Similarity algorithm to compare Strings
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public class NeedlemanWunchSimilarityImp implements Similarity {

    /**
     *
     *  This method calculate the similarity using Needleman Wunch  Algorithm in (0,1) range.
     *  description: The algorithm is based on Needleman Wunch  of the String A and B
     * @param str1 : fist sting to compare similarity
     * @param str2: second String to compare Similarity
     * @return float as similitude measure in range (0,1)
     */
    @Override
    public float calculateSimilarity(String str1, String str2) {
        StringMetric metric =
                with(new NeedlemanWunch())
                        .simplify(Simplifiers.toLowerCase())
                        .simplify(Simplifiers.replaceNonWord())
                        .build();
        return Math.max(new MongeElkan(metric).compare(Arrays.asList(str1.split(" " )), Arrays.asList(str2.split(" " ))),metric.compare(str1,str2));
    }

}
