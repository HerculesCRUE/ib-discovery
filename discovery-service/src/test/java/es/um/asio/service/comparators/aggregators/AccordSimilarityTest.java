package es.um.asio.service.comparators.aggregators;

import data.Stats;
import data.SyntheticData;
import org.javatuples.Pair;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccordSimilarityTest {
    private final Logger logger = LoggerFactory.getLogger(AccordSimilarityTest.class);
    String name = "Accord Similarity";
    Stats stats;
    List<Pair<String,String>> equals, shuffled, changedCharacters,
            truncatedCharacter, different, allModifications,
            allChanges, randomCharacters;

    @BeforeEach
    void setUp() {
        stats = new Stats();
        SyntheticData sd = SyntheticData.getInstance();
        equals = sd.getEquals();
        shuffled = sd.getShuffled();
        changedCharacters = sd.getChangedCharacters();
        truncatedCharacter = sd.getTruncatedCharacter();
        different = sd.getDifferent();
        allModifications = sd.getAllModifications();
        allChanges = sd.getAllChanges();
        randomCharacters = sd.getRandomCharacters();
    }

    @Test
    void simpleSimilarity() {
        String s1 = "Daniel Ruiz Santamaria";
        String s2 = "Ruiz Santamaría, D.";
        float similarity = AccordSimilarity.calculateAccordSimilarity(s1, s2);
    }

    @Test
    void calculateSimilarityOnEqualTest() {
        stats = evaluateSimilarityAccord(equals);
        logger.info(name + "(Equal): " + stats.toString());
        Assert.assertTrue( stats.getRatioForRange(1f) >= 1f);
    }

    @Test
    void calculateSimilarityOnShuffledTest(){
        stats = evaluateSimilarityAccord(shuffled);
        logger.info(name + "(Shuffled): " + stats.toString());
        Assert.assertTrue( stats.getRatioForRange(0.9f) >= 1f);
    }

    @Test
    void calculateSimilarityOnChangedCharactersTest(){
        // Shuffled String: The compare value must be greater than 0.3 and less than 1.0
        stats = evaluateSimilarityAccord(changedCharacters);
        logger.info(name + "(Changed Characters): " + stats.toString());
        Assert.assertTrue( stats.getRatioForRange(0.55f) >= 0.60f);
    }

    @Test
    void calculateSimilarityOnTruncatedCharactersTest(){
        // Shuffled String: The compare value must be greater than 0.3 and less than 1.0
        stats = evaluateSimilarityAccord(truncatedCharacter);
        logger.info(name + "(All Changes): " + stats.toString());
        Assert.assertTrue( stats.getRatioForRange(0.8f) >= 0.95f);
    }

    @Test
    void calculateSimilarityOnAllChangesTest(){
        // Shuffled String: The compare value must be greater than 0.3 and less than 1.0
        stats = evaluateSimilarityAccord(allChanges);
        logger.info(name + "(All Changes): " + stats.toString());
        Assert.assertTrue( stats.getRatioForRange(0.65f) >= 0.8f);
    }

    @Test
    void calculateSimilarityOnDistinctTest(){
        // Shuffled String: The compare value must be greater than 0.3 and less than 1.0
        stats = evaluateSimilarityAccord(different);
        logger.info(name + "(Distinct): " + stats.toString());
        Assert.assertTrue( stats.getRatioForRange(0.65f) < .05f);
    }

    public Stats evaluateSimilarityAccord(List<Pair<String,String>> dataCollection) {
        Stats stats = new Stats();
        for (Pair<String,String> t : dataCollection) {
            float similarity = AccordSimilarity.calculateAccordSimilarity(t.getValue(0).toString(), t.getValue(1).toString());
            stats.addValue(similarity);
        }
        return stats;
    }
}