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
    private boolean isLink;

    /**
     * Constructor
     * @param similarity float. The similarity value
     * @param weight float. The weight value
     */
    public SimilarityValue(float similarity, float weight, boolean isLink) {
        this.similarity = similarity;
        this.weight = weight;
        this.weightedSimilarity = similarity * weight;
        this.isLink = isLink;
    }

    /**
     * Constructor
     * @param similarity float. The similarity value
     * @param weight float. The weight value
     * @param weightedSimilarity float. The weightedSimilarity value
     */
    public SimilarityValue(float similarity, float weight, float weightedSimilarity, boolean isLink) {
        this.isLink = isLink;
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
