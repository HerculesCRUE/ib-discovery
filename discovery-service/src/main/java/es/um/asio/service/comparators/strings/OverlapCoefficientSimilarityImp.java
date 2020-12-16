package es.um.asio.service.comparators.strings;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.metrics.OverlapCoefficient;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

import java.util.Arrays;

import static org.simmetrics.builders.StringMetricBuilder.with;

public class OverlapCoefficientSimilarityImp implements Similarity {

    /*
     * Mide el grado en el cual existen substring del conjunto A que aparecen en el conjunto B
     * Ventajas: Funciona bien con los mezclados
     * Inconvenientes: Mal en el resto de casos
     */
    @Override
    public float calculateSimilarity(String str1, String str2) {
        StringMetric metric =
                with(new OverlapCoefficient<>())
                        .simplify(Simplifiers.toLowerCase())
                        .simplify(Simplifiers.removeDiacritics())
                        .tokenize(Tokenizers.whitespace())
                        .build();
        return Math.max(new MongeElkan(metric).compare(Arrays.asList(str1.split(" " )), Arrays.asList(str2.split(" " ))),metric.compare(str1,str2));
    }

}
