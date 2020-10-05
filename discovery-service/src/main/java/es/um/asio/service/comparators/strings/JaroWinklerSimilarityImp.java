package es.um.asio.service.comparators.strings;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.JaroWinkler;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.simplifiers.Simplifiers;

import java.util.Arrays;

import static org.simmetrics.builders.StringMetricBuilder.with;

public class JaroWinklerSimilarityImp extends Similarity {

    /*
     * El algoritmo se basa en aplicar penalizaciones cuando no exista coincidencia siendo mayor la penalización en los 4 primeros. El vector se forma con el conteo de las palabras en las dos cadenas
     * Ventajas: Muy bueno con los mezclados
     * Inconvenientes: Funciona mal con las abreviaturas o cambios de caracteres
     */
    @Override
    public float calculateSimilarity(String str1, String str2) {
        StringMetric metric =
                with(new JaroWinkler())
                        .simplify(Simplifiers.toLowerCase())
                        .simplify(Simplifiers.removeDiacritics())
                        .build();
        return Math.max(new MongeElkan(metric).compare(Arrays.asList(str1.split(" " )), Arrays.asList(str2.split(" " ))),metric.compare(str1,str2));
    }

}
