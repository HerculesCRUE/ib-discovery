package es.um.asio.service.model.stats;

import data.DataGenerator;
import es.um.asio.service.model.TripleObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
class StatsHandlerTest {

    StatsHandler statsHandler;
    List<TripleObject> tos;

    @BeforeEach
    public void setUp() throws Exception {
        DataGenerator dg = new DataGenerator();
        tos = dg.getTripleObjects();
        statsHandler = new StatsHandler();
        for (TripleObject to : tos) {
            statsHandler.addAttributes(to.getTripleStore().getNode().getNodeName(),to.getTripleStore().getName(),to);
            statsHandler.buildStats(to.getTripleStore().getNode().getNodeName(),to.getTripleStore().getName(),to.getClassName());
        }
    }

    @Test
    void addAttributes() {
        for (TripleObject to : tos) {
            statsHandler.addAttributes(to.getTripleStore().getNode().getNodeName(),to.getTripleStore().getName(),to);
            Assert.assertTrue(statsHandler.getStats().get(to.getTripleStore().getNode().getNodeName()).get(to.getTripleStore().getName()).get(to.getClassName()).getAttValues().size() == to.getAttributes().size());
        }
    }

    @Test
    void getAttributesMap() {
        for (TripleObject to : tos) {
            Assert.assertNotNull(statsHandler.getAttributesMap(to.getTripleStore().getNode().getNodeName(),to.getTripleStore().getName(),to.getClassName()));
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
            Assert.assertNotNull(statsHandler.buildStats(to.getTripleStore().getNode().getNodeName(),to.getTripleStore().getName(),to.getClassName()));
        }
    }

    @Test
    void generateMoreRelevantAttributesMap() {
        for (TripleObject to : tos) {
            Assert.assertNotNull(statsHandler.generateMoreRelevantAttributesMap(to.getTripleStore().getNode().getNodeName(),to.getTripleStore().getName(),to.getClassName()));
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