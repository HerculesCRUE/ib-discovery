package es.um.asio.service.comparators.entities;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
class SimilarityValueTest {

    @Test
    void setSimilarity() {
        SimilarityValue sv = new SimilarityValue(1f,1f);
        sv.setSimilarity(0.5f);
        Assert.assertTrue(sv.getSimilarity() == .5f);
    }

    @Test
    void setWeight() {
        SimilarityValue sv = new SimilarityValue(1f,1f);
        sv.setWeight(0.5f);
        Assert.assertTrue(sv.getWeight() == .5f);
    }

    @Test
    void setWeightedSimilarity() {
        SimilarityValue sv = new SimilarityValue(1f,1f);
        sv.setWeightedSimilarity(0.5f);
        Assert.assertTrue(sv.getWeightedSimilarity() == .5f);
    }

    @Test
    void getSimilarity() {
        SimilarityValue sv = new SimilarityValue(1f,1f);
        sv.setSimilarity(0.5f);
        Assert.assertTrue(sv.getSimilarity() == .5f);
    }

    @Test
    void getWeight() {
        SimilarityValue sv = new SimilarityValue(1f,1f);
        sv.setWeight(0.5f);
        Assert.assertTrue(sv.getWeight() == .5f);
    }

    @Test
    void getWeightedSimilarity() {
        SimilarityValue sv = new SimilarityValue(1f,1f);
        sv.setWeightedSimilarity(0.5f);
        Assert.assertTrue(sv.getWeightedSimilarity() == .5f);
    }
}