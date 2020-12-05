package es.um.asio.service.comparators.entities;

import es.um.asio.service.TestDiscoveryApplication;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

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