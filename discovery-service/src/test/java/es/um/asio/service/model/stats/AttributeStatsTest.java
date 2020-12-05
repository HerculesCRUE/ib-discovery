package es.um.asio.service.model.stats;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.relational.Value;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
class AttributeStatsTest {

    StatsHandler statsHandler;
    Map<String,AttributeStats> attributeStats;

    @BeforeEach
    public void setUp() throws Exception {
        DataGenerator dg = new DataGenerator();
        List<TripleObject> tos = dg.getTripleObjects();
        attributeStats = new HashMap<>();
        statsHandler = new StatsHandler();
        for (TripleObject to : tos) {
            statsHandler.addAttributes(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to);
            statsHandler.buildStats(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to.getClassName());
        }
        Map<String ,Map<String, Map<String,EntityStats>>> stats = statsHandler.getStats();
        for (Map.Entry<String, Map<String, Map<String, EntityStats>>> nEntry : stats.entrySet()) {
            for(Map.Entry<String, Map<String, EntityStats>> tsEntry : nEntry.getValue().entrySet()) {
                for (Map.Entry<String, EntityStats> cnEntry : tsEntry.getValue().entrySet()) {
                    attributeStats.putAll(cnEntry.getValue().getAttValues());
                }
            }
        }
    }

    @Test
    void addValue() {
        for (AttributeStats as: attributeStats.values()) {
            String rndValue = RandomStringUtils.randomAlphabetic(10);
            as.addValue(rndValue);
            Assert.assertTrue(as.getValues().contains(rndValue));
        }
    }

    @Test
    void getRelativeImportanceRatio() {
        for (AttributeStats as: attributeStats.values()) {
            Assert.assertTrue((as.getValues().size() == 0 && as.getRelativeImportanceRatio() == 0) || (as.getRelativeImportanceRatio() == (Float.valueOf(as.getValues().size())/Float.valueOf(as.getCounter()))));
        }
    }

    @Test
    void testEquals() {
        for (AttributeStats as: attributeStats.values()) {
            for (AttributeStats asInner: attributeStats.values()) {
                if (as.hashCode() == asInner.hashCode())
                    Assert.assertTrue(as.equals(asInner));
                else
                    Assert.assertFalse(as.equals(asInner));
            }
        }
    }

    @Test
    void testHashCode() {
        for (AttributeStats as: attributeStats.values()) {
            for (AttributeStats asInner: attributeStats.values()) {
                if (as.equals(asInner))
                    Assert.assertTrue(as.hashCode() == asInner.hashCode());
                else
                    Assert.assertFalse(as.hashCode() == asInner.hashCode());
            }
        }
    }

    @Test
    void setValues() {
        for (AttributeStats as: attributeStats.values()) {
            Set<Object> values = as.getValues();
            as.setValues(values);
            Assert.assertEquals(as.getValues(),values);
        }
    }

    @Test
    void getValues() {
        for (AttributeStats as: attributeStats.values()) {
            Set<Object> values = as.getValues();
            as.setValues(values);
            Assert.assertEquals(as.getValues(),values);
        }
    }
}