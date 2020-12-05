package es.um.asio.service.comparators.entities;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.service.impl.CacheServiceImp;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class EntitySimilarityObjTest {

    List<TripleObject> tos;

    Set<EntitySimilarityObj> esos;

    @Autowired
    CacheServiceImp cache;

    @Autowired
    DataSourcesConfiguration conf;



    @PostConstruct
    public void init() throws Exception {
        DataGenerator dataGenerator = new DataGenerator();
        esos = new HashSet<>();
        tos = dataGenerator.getTripleObjects();
        for (TripleObject to : tos) {
            cache.addTripleObject(to.getTripleStore().getNode().getNode(), to.getTripleStore().getTripleStore(), to);
        }
        cache.generateEntityStats();

        for (TripleObject to : tos) {
            Map<String, List<EntitySimilarityObj>> esoMaps = EntitySimilarityHandler.calculateSimilarityInEntities(cache, to, tos, conf.getThresholds().getManualThreshold(), conf.getThresholds().getAutomaticThreshold());
            esos.addAll(esoMaps.get("AUTOMATIC"));
            esos.addAll(esoMaps.get("MANUAL"));
        }
        System.out.println();
    }

    @Test
    void getSimilarity() {
        for (EntitySimilarityObj eso : esos) {
            Assert.assertTrue(eso.getSimilarity() >= 0f && eso.getSimilarity() <= 1f);
        }

    }

    @Test
    void addSimilarity() {
        for (EntitySimilarityObj eso : esos) {
            SimilarityValue sv = new SimilarityValue(1f,1f);
            eso.addSimilarity("test",sv);
            Assert.assertTrue(eso.getSimilarities().containsKey("test") && eso.getSimilarities().get("test").equals(sv));
        }
    }

    @Test
    void getSimilarities() {
        for (EntitySimilarityObj eso : esos) {
            Assert.assertTrue(!eso.getSimilarities().isEmpty());
        }
    }

    @Test
    void testEquals() {
        for (EntitySimilarityObj eso : esos) {
            Assert.assertTrue(eso.equals(eso));
        }
    }

    @Test
    void testHashCode() {
        for (EntitySimilarityObj eso : esos) {
            for (EntitySimilarityObj esoInner : esos) {
                if (eso.equals(esoInner))
                    Assert.assertTrue(eso.hashCode() == esoInner.hashCode());
            }
        }
    }

    @Test
    void setTripleObject() {
        for (EntitySimilarityObj eso : esos) {
            TripleObject to = new TripleObject();
            eso.setTripleObject(to);
            Assert.assertEquals(eso.getTripleObject(),to);
        }
    }

    @Test
    void setSimilarity() {
        for (EntitySimilarityObj eso : esos) {
            eso.setSimilarity(.5f);
            Assert.assertTrue(eso.getSimilarity() == .5f);
        }
    }

    @Test
    void setSimilarities() {
        for (EntitySimilarityObj eso : esos) {
            Map<String, SimilarityValue> sMap = eso.getSimilarities();
            eso.setSimilarities(sMap);
            Assert.assertTrue(eso.getSimilarities().equals(sMap));
        }
    }

    @Test
    void getTripleObject() {
        for (EntitySimilarityObj eso : esos) {
            TripleObject to = new TripleObject();
            eso.setTripleObject(to);
            Assert.assertEquals(eso.getTripleObject(),to);
        }
    }
}