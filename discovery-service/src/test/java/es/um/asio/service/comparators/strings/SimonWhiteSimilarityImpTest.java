package es.um.asio.service.comparators.strings;

import data.Stats;
import data.SyntheticData;
import org.javatuples.Pair;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class SimonWhiteSimilarityImpTest {

    private final Logger logger = LoggerFactory.getLogger(SimonWhiteSimilarityImpTest.class);
    String name = "Simon White Similarity";
    Stats stats;
    List<Pair<String,String>> equals, shuffled, changedCharacters,
            truncatedCharacter, different, allModifications,
            allChanges, randomCharacters;
    Similarity similarityImp = new SimonWhiteSimilarityImp();

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
    void calculateSimilarityOnEqualTest() {
        stats = evaluateSimilarityAlgorithm(equals,new CosineSimilarityImp());
        logger.info(name + "(Equal): " + stats.toString());
        Assert.assertTrue( stats.getMean() == 1.0 );
    }

    @Test
    void calculateSimilarityOnShuffledTest(){
        // Shuffled String: The compare val must be greater than 0.3 and less than 1.0
        stats = evaluateSimilarityAlgorithm(shuffled,similarityImp);
        logger.info(name + "(Shuffled): " + stats.toString());
        Assert.assertTrue( stats.getMean() >= 1.);
    }

    @Test
    void calculateSimilarityOnChangedCharactersTest(){
        // Shuffled String: The compare val must be greater than 0.3 and less than 1.0
        stats = evaluateSimilarityAlgorithm(changedCharacters,similarityImp);
        logger.info(name + "(Changed Characters): " + stats.toString());
        Assert.assertTrue( stats.getMean() >= .0);
    }

    @Test
    void calculateSimilarityOnTruncatedCharactersTest(){
        // Shuffled String: The compare val must be greater than 0.3 and less than 1.0
        stats = evaluateSimilarityAlgorithm(truncatedCharacter,similarityImp);
        logger.info(name + "(Truncated Characters): " + stats.toString());
        Assert.assertTrue( stats.getMean() >= .2 );
    }

    @Test
    void calculateSimilarityOnAllChangesTest(){
        // Shuffled String: The compare val must be greater than 0.3 and less than 1.0
        stats = evaluateSimilarityAlgorithm(allChanges,similarityImp);
        logger.info(name + "(All Changes): " + stats.toString());
        Assert.assertTrue( stats.getMean() >= .0 );
    }

    @Test
    void calculateSimilarityOnDistinctTest(){
        // Shuffled String: The compare val must be greater than 0.3 and less than 1.0
        stats = evaluateSimilarityAlgorithm(different,similarityImp);
        logger.info(name + "(Distinct): " + stats.toString());
        Assert.assertTrue( stats.getMean() >= .0 );
    }

    public Stats evaluateSimilarityAlgorithm(List<Pair<String,String>> dataCollection, Similarity similarityImp) {
        Stats stats = new Stats();
        for (Pair<String,String> t : dataCollection) {
            float similarity = similarityImp.calculateSimilarity(t.getValue(0).toString(), t.getValue(1).toString());
            stats.addValue(similarity);
        }
        return stats;
    }

}