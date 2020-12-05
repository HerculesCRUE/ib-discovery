package es.um.asio.service.model.stats;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.model.TripleObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
class EntityStatsTest {

    StatsHandler statsHandler;
    Set<EntityStats> entityStats;

    @BeforeEach
    public void setUp() throws Exception {
        DataGenerator dg = new DataGenerator();
        List<TripleObject> tos = dg.getTripleObjects();
        entityStats = new HashSet<>();
        statsHandler = new StatsHandler();
        for (TripleObject to : tos) {
            statsHandler.addAttributes(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to);
            statsHandler.buildStats(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to.getClassName());
        }
        Map<String ,Map<String, Map<String,EntityStats>>> stats = statsHandler.getStats();
        for (Map.Entry<String, Map<String, Map<String, EntityStats>>> nEntry : stats.entrySet()) {
            for(Map.Entry<String, Map<String, EntityStats>> tsEntry : nEntry.getValue().entrySet()) {
                for (Map.Entry<String, EntityStats> cnEntry : tsEntry.getValue().entrySet()) {
                    entityStats.add(cnEntry.getValue());
                }
            }
        }
    }

    @Test
    void addValue() {
        for (EntityStats es: entityStats) {
            String rndKey = RandomStringUtils.randomAlphabetic(10);
            String rndValue = RandomStringUtils.randomAlphabetic(10);
            es.addValue(rndKey,rndValue);
            Assert.assertTrue(es.getAttValues().get(rndKey).values.contains(rndValue));
        }
    }

    @Test
    void getRelativeImportanceRatio() {
        for (EntityStats es: entityStats) {
            Assert.assertTrue((es.attValues.size() == 0 && es.getRelativeImportanceRatio() == 0) || (es.getRelativeImportanceRatio() == (1.0f/(es.attValues.size()*1.0f))));
        }
    }

    @Test
    void getAttRelativeImportanceRatio() {
        for (EntityStats es: entityStats) {
            Assert.assertTrue((es.attValues.size() == 0 && es.getAttRelativeImportanceRatio() == 0) || (es.getAttRelativeImportanceRatio() == (1.0f/(es.attValues.size()*1.0f))));
        }
    }

    @Test
    void getEntRelativeImportanceRatio() {
        for (EntityStats es: entityStats) {
            Assert.assertTrue( (es.objValues.size() == 0 && es.getEntRelativeImportanceRatio() == 0) || (es.getEntRelativeImportanceRatio() == (1.0f/(es.objValues.size()*1.0f))) );
        }
    }

    @Test
    void buildStats() {
        for (EntityStats es: entityStats) {
            Map<String,Object> stats = es.buildStats();
            Assert.assertTrue(stats.containsKey("maxRelativeRatio"));
            Assert.assertTrue(((float)stats.get("maxRelativeRatio")) == es.getRelativeImportanceRatio());
            Assert.assertTrue(stats.containsKey("maxAttributesRelativeRatio"));
            Assert.assertTrue(((float)stats.get("maxAttributesRelativeRatio")) == es.getAttRelativeImportanceRatio());
            Assert.assertTrue(stats.containsKey("maxEntitiesRelativeRatio"));
            Assert.assertTrue(((float)stats.get("maxEntitiesRelativeRatio")) == es.getEntRelativeImportanceRatio());
            Assert.assertTrue(stats.containsKey("attributesSize"));
            Assert.assertTrue(((int)stats.get("attributesSize")) == es.getAttValues().size());
            Assert.assertTrue(stats.containsKey("entitiesSize"));
            Assert.assertTrue(((int)stats.get("entitiesSize")) == es.getObjValues().size());
            Assert.assertTrue(stats.containsKey("attributes"));
            Assert.assertTrue(((Map)stats.get("attributes")).size() == es.getAttValues().size());
        }
    }

    @Test
    void generateMoreRelevantAttributesMap() {
        for (EntityStats es: entityStats) {
            Assert.assertNotNull(es.generateMoreRelevantAttributesMap(null));
        }
    }

    @Test
    void setAttValues() {
        for (EntityStats es: entityStats) {
            Map<String, AttributeStats> attValues = es.getAttValues();
            es.setAttValues(attValues);
            Assert.assertTrue(es.getAttValues().equals(attValues));
        }
    }

    @Test
    void setObjValues() {
        for (EntityStats es: entityStats) {
            Map<String, EntityStats> objValues = es.getObjValues();
            es.setObjValues(objValues);
            Assert.assertTrue(es.getObjValues().equals(objValues));
        }
    }

    @Test
    void getAttValues() {
        for (EntityStats es: entityStats) {
            Map<String, AttributeStats> attValues = es.getAttValues();
            es.setAttValues(attValues);
            Assert.assertTrue(es.getAttValues().equals(attValues));
        }
    }

    @Test
    void getObjValues() {
        for (EntityStats es: entityStats) {
            Map<String, EntityStats> objValues = es.getObjValues();
            es.setObjValues(objValues);
            Assert.assertTrue(es.getObjValues().equals(objValues));
        }
    }
}