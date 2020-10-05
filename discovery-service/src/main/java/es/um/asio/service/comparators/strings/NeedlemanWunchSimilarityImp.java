package es.um.asio.service.comparators.strings;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.metrics.NeedlemanWunch;
import org.simmetrics.simplifiers.Simplifiers;

import java.util.Arrays;

import static org.simmetrics.builders.StringMetricBuilder.with;

public class NeedlemanWunchSimilarityImp extends Similarity {

    /*
     * El algoritmo intenta aliar dos cadenas y concede penalizaciones
     * Ventajas: Bien con los truncados o pequeñas modificaciones
     * Inconvenientes: Funciona mal con las abreviaturas o cambios de caracteres
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
