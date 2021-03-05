package es.um.asio.service.comparators.strings;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.metrics.SmithWaterman;
import org.simmetrics.simplifiers.Simplifiers;

import java.util.Arrays;

import static org.simmetrics.builders.StringMetricBuilder.with;

/**
 * This class implements the similitude of Smith Weterman Similarity algorithm to compare Strings
 * @author  Daniel Ruiz Santamaría
 * @version 1.0
 * @since   2021-01-01
 */
public class SmithWetermanSimilarityImp implements Similarity {

    /**
     *
     *  This method calculate the similarity using Smith Weterman Similarity Algorithm in (0,1) range.
     *  description: The algorithm is based on Smith Weterman of the String A and B
     * @param str1 : fist sting to compare similarity
     * @param str2: second String to compare Similarity
     * @return float as similitude measure in range (0,1)
     */
    @Override
    public float calculateSimilarity(String str1, String str2) {
        StringMetric metric =
                with(new SmithWaterman())
                        .simplify(Simplifiers.toLowerCase())
                        .simplify(Simplifiers.removeDiacritics())
                        .build();
        return Math.max(new MongeElkan(metric).compare(Arrays.asList(str1.split(" " )), Arrays.asList(str2.split(" " ))),metric.compare(str1,str2));
    }

}
