package es.um.asio.service.comparators.strings;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.EuclideanDistance;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

import java.util.Arrays;

import static org.simmetrics.builders.StringMetricBuilder.with;

public class EuclideanDistanceSimilarityImp extends Similarity {


    /*
     * El algoritmo se basa en medir la distancia euclidea de las cadenas A a la cadena B.
     * Ventajas: Muy bueno con los mezclados
     * Inconvenientes: Funciona mal con las abreviaturas o cambios de caracteres
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
