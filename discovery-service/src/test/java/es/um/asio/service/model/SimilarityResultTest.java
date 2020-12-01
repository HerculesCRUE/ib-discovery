package es.um.asio.service.model;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class SimilarityResultTest {

    List<TripleObject> tripleObjects;
    List<SimilarityResult> similarityResults;

    @PostConstruct
    public void init() throws Exception {
        DataGenerator dataGenerator = new DataGenerator();
        tripleObjects = dataGenerator.getTripleObjects();
        similarityResults = new ArrayList<>();
        for (TripleObject to : tripleObjects) {
            SimilarityResult sr = new SimilarityResult(to);
            similarityResults.add(sr);
        }
    }

    @Test
    void addAutomatics() {
        for (SimilarityResult sr : similarityResults) {
            List<EntitySimilarityObj> esos = new ArrayList<>();
            for (TripleObject to : tripleObjects) {
                if (!sr.getTripleObject().getId().equals(to.getId())) {
                    EntitySimilarityObj eso = new EntitySimilarityObj(to);
                    esos.add(eso);
                }
            }
            sr.addAutomatics(esos);
            Assert.assertTrue((sr.getAutomatic().size() + 1) == tripleObjects.size());
        }
    }

    @Test
    void addManuals() {
        for (SimilarityResult sr : similarityResults) {
            List<EntitySimilarityObj> esos = new ArrayList<>();
            for (TripleObject to : tripleObjects) {
                if (!sr.getTripleObject().getId().equals(to.getId())) {
                    EntitySimilarityObj eso = new EntitySimilarityObj(to);
                    esos.add(eso);
                }
            }
            sr.addManuals(esos);
            Assert.assertTrue((sr.getManual().size() + 1) == tripleObjects.size());
        }
    }

    @Test
    void testEquals() {
        int counter = 0;
        for (SimilarityResult sr : similarityResults) {
            counter++;
            List<EntitySimilarityObj> esos = new ArrayList<>();
            for (TripleObject to : tripleObjects) {
                if (!sr.getTripleObject().getId().equals(to.getId())) {
                    EntitySimilarityObj eso = new EntitySimilarityObj(to);
                    esos.add(eso);
                }
            }
            if (counter%2 == 1) {
                sr.addAutomatics(esos);
            } else {
                sr.addManuals(esos);
            }
        }

        for (SimilarityResult sr : similarityResults) {
            for (SimilarityResult srInner : similarityResults) {
                if (sr.getTripleObject().getId().equals(srInner.getTripleObject().getId()))
                    Assert.assertTrue(sr.equals(srInner));
                else
                    Assert.assertFalse(sr.equals(srInner));
            }
        }
    }

    @Test
    void testHashCode() {
        for (SimilarityResult sr : similarityResults) {
            for (SimilarityResult srInner : similarityResults) {
                if (sr.getTripleObject().getId().equals(srInner.getTripleObject().getId()))
                    Assert.assertTrue(sr.hashCode() == srInner.hashCode());
                else
                    Assert.assertFalse(sr.hashCode() == srInner.hashCode());
            }
        }
    }

    @Test
    void setTripleObject() {
        for (SimilarityResult sr : similarityResults) {
            for (TripleObject to : tripleObjects) {
                sr.setTripleObject(to);
                Assert.assertTrue(sr.getTripleObject().getId().equals(to.getId()));
            }
        }
    }

    @Test
    void setAutomatic() {
        for (SimilarityResult sr : similarityResults) {
            List<EntitySimilarityObj> esos = new ArrayList<>();
            for (TripleObject to : tripleObjects) {
                if (!sr.getTripleObject().getId().equals(to.getId())) {
                    EntitySimilarityObj eso = new EntitySimilarityObj(to);
                    esos.add(eso);
                }
            }
            sr.addAutomatics(esos);
            Assert.assertTrue((sr.getAutomatic().size() + 1) == tripleObjects.size());
        }
    }

    @Test
    void setManual() {
        for (SimilarityResult sr : similarityResults) {
            List<EntitySimilarityObj> esos = new ArrayList<>();
            for (TripleObject to : tripleObjects) {
                if (!sr.getTripleObject().getId().equals(to.getId())) {
                    EntitySimilarityObj eso = new EntitySimilarityObj(to);
                    esos.add(eso);
                }
            }
            sr.addManuals(esos);
            Assert.assertTrue((sr.getManual().size() + 1) == tripleObjects.size());
        }
    }

    @Test
    void getTripleObject() {
        for (SimilarityResult sr : similarityResults) {
            Assert.assertTrue(sr.getTripleObject() instanceof TripleObject);
        }
    }

    @Test
    void getAutomatic() {
        for (SimilarityResult sr : similarityResults) {
            List<EntitySimilarityObj> esos = new ArrayList<>();
            for (TripleObject to : tripleObjects) {
                if (!sr.getTripleObject().getId().equals(to.getId())) {
                    EntitySimilarityObj eso = new EntitySimilarityObj(to);
                    esos.add(eso);
                }
            }
            sr.addAutomatics(esos);
            Assert.assertTrue((sr.getAutomatic().size() + 1) == tripleObjects.size());
        }
    }

    @Test
    void getManual() {
        for (SimilarityResult sr : similarityResults) {
            List<EntitySimilarityObj> esos = new ArrayList<>();
            for (TripleObject to : tripleObjects) {
                if (!sr.getTripleObject().getId().equals(to.getId())) {
                    EntitySimilarityObj eso = new EntitySimilarityObj(to);
                    esos.add(eso);
                }
            }
            sr.addManuals(esos);
            Assert.assertTrue((sr.getManual().size() + 1) == tripleObjects.size());
        }
    }
}