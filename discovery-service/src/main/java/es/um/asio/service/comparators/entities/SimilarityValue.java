package es.um.asio.service.comparators.entities;

import lombok.Getter;
import lombok.Setter;

/**
 * This class Model a SimilarityValue for the attributes
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
public class SimilarityValue {
    private float similarity;
    private float weight;
    private float weightedSimilarity;

    /**
     * Constructor
     * @param similarity float. The similarity value
     * @param weight float. The weight value
     */
    public SimilarityValue(float similarity, float weight) {
        this.similarity = similarity;
        this.weight = weight;
        this.weightedSimilarity = similarity * weight;
    }

    /**
     * Constructor
     * @param similarity float. The similarity value
     * @param weight float. The weight value
     * @param weightedSimilarity float. The weightedSimilarity value
     */
    public SimilarityValue(float similarity, float weight, float weightedSimilarity) {
        if (similarity == 0) {
            this.weight =weight;
            this.weightedSimilarity = weightedSimilarity;
            this.similarity = weightedSimilarity/weight;
        } else if (weight == 0) {
            this.similarity = similarity;
            this.weightedSimilarity = weightedSimilarity;
            this.weight = weightedSimilarity/similarity;
        }
        else {
            this.similarity = similarity;
            this.weight = weight;
            this.weightedSimilarity = weightedSimilarity;
        }
    }
}
