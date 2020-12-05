package es.um.asio.service.model.stats;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.model.TripleObject;
import org.junit.Assert;
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

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class StatsHandlerTest {

    StatsHandler statsHandler;
    List<TripleObject> tos;

    @PostConstruct
    public void init() throws Exception {
        DataGenerator dg = new DataGenerator();
        tos = dg.getTripleObjects();
        statsHandler = new StatsHandler();
        for (TripleObject to : tos) {
            statsHandler.addAttributes(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to);
            statsHandler.buildStats(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to.getClassName());
        }
    }

    @Test
    void addAttributes() {
        for (TripleObject to : tos) {
            statsHandler.addAttributes(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to);
            Assert.assertTrue(statsHandler.getStats().get(to.getTripleStore().getNode().getNode()).get(to.getTripleStore().getTripleStore()).get(to.getClassName()).getAttValues().size() == to.getAttributes().size());
        }
    }

    @Test
    void getAttributesMap() {
        for (TripleObject to : tos) {
            Assert.assertNotNull(statsHandler.getAttributesMap(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to.getClassName()));
        }
    }

    @Test
    void isEmpty() {
        Assert.assertFalse(statsHandler.isEmpty());
    }


    @Test
    void cleanStats() {
        statsHandler.cleanStats();
        Assert.assertTrue(statsHandler.isEmpty());
    }

    @Test
    void buildStats() {
        for (TripleObject to : tos) {
            Assert.assertNotNull(statsHandler.buildStats(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to.getClassName()));
        }
    }

    @Test
    void generateMoreRelevantAttributesMap() {
        for (TripleObject to : tos) {
            Assert.assertNotNull(statsHandler.generateMoreRelevantAttributesMap(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to.getClassName()));
        }
    }

    @Test
    void setStats() {
        statsHandler.setStats(statsHandler.getStats());
        Assert.assertFalse(statsHandler.isEmpty());
    }

    @Test
    void getStats() {
        statsHandler.setStats(statsHandler.getStats());
        Assert.assertFalse(statsHandler.isEmpty());
    }
}