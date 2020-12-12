package es.um.asio.service.comparators.strings;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.LongestCommonSubstring;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.simplifiers.Simplifiers;

import java.util.Arrays;

import static org.simmetrics.builders.StringMetricBuilder.with;

public class LongestCommonSubStringSimilarityImp implements Similarity {

    /*
     * Mide el grado de la longitud de caracteres que coinciden en cualquier posición de la palabra
     * Ventajas: Funciona bien con los mezclados
     * Inconvenientes: Mal en el resto de casos
     */
    @Override
    public float calculateSimilarity(String str1, String str2) {
        StringMetric metric =
                with(new LongestCommonSubstring())
                        .simplify(Simplifiers.toLowerCase())
                        .simplify(Simplifiers.removeDiacritics())
                        .build();
        return Math.max(new MongeElkan(metric).compare(Arrays.asList(str1.split(" " )), Arrays.asList(str2.split(" " ))),metric.compare(str1,str2));
    }

}
