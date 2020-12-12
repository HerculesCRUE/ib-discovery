package es.um.asio.service.comparators.entities;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SimilarityValue {
    private float similarity;
    private float weight;
    private float weightedSimilarity;

    public SimilarityValue(float similarity, float weight) {
        this.similarity = similarity;
        this.weight = weight;
        this.weightedSimilarity = similarity * weight;
    }

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
