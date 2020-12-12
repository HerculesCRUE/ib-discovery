package es.um.asio.service.comparators.strings;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.Levenshtein;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.simplifiers.Simplifiers;

import java.util.Arrays;

import static org.simmetrics.builders.StringMetricBuilder.with;

public class LevenshteinSimilarityImp implements Similarity {

    /*
     * El algoritmo se basa en el conteo de la cantidad de cambios necesarios para pasar de la cadena A a la cadena B
     */
    @Override
    public float calculateSimilarity(String str1, String str2) {
        StringMetric metric =
                with(new Levenshtein())
                        .simplify(Simplifiers.toLowerCase())
                        .simplify(Simplifiers.removeDiacritics())
                        .build();
        return Math.max(new MongeElkan(metric).compare(Arrays.asList(str1.split(" " )), Arrays.asList(str2.split(" " ))),metric.compare(str1,str2));
    }
}
