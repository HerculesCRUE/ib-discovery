package es.um.asio.service.comparators.strings;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.Jaccard;
import org.simmetrics.metrics.MongeElkan;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

import java.util.Arrays;

import static org.simmetrics.builders.StringMetricBuilder.with;

public class JaccardSimilarityImp implements Similarity {


    /*
     * El algoritmo se basa en medir el coseno entre la distancia de las cadenas A a la cadena B. El vector se forma con el conteo de las palabras en las dos cadenas
     * Ventajas: Muy bueno con los mezclados
     * Inconvenientes: Funciona mal con las abreviaturas o cambios de caracteres
     */
    @Override
    public float calculateSimilarity(String str1, String str2) {
        StringMetric metric =
                with(new Jaccard<>())
                        .simplify(Simplifiers.toLowerCase())
                        .simplify(Simplifiers.replaceNonWord())
                        .tokenize(Tokenizers.whitespace())
                        .build();
        return Math.max(new MongeElkan(metric).compare(Arrays.asList(str1.split(" " )), Arrays.asList(str2.split(" " ))),metric.compare(str1,str2));
    }

}
