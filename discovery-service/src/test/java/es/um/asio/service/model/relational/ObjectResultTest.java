package es.um.asio.service.model.relational;

import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import data.DataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@RunWith(SpringRunner.class)
class ObjectResultTest {

    Set<ObjectResult> objectResults;
    JobRegistry jobRegistry;

    @BeforeEach
    public void setUp() throws Exception {
        DataGenerator dg = new DataGenerator();
        objectResults = new HashSet<>();
        jobRegistry = dg.getJobRegistry();
        objectResults = jobRegistry.getObjectResults();

    }

    @Test
    void addAutomatic() {
        for (ObjectResult or : objectResults) {
            for (ObjectResult orInner : objectResults) {
                if (!or.equals(orInner)) {
                    or.addAutomatic(orInner);
                    Assert.assertTrue(or.getAutomatic().contains(orInner));
                }
            }
        }
    }

    @Test
    void addManual() {
        for (ObjectResult or : objectResults) {
            for (ObjectResult orInner : objectResults) {
                if (!or.equals(orInner)) {
                    or.addManual(orInner);
                    Assert.assertTrue(or.getManual().contains(orInner));
                }
            }
        }
    }

    @Test
    void toTripleObject() {
        for (ObjectResult or : objectResults) {
            Assert.assertNotNull(or.toTripleObject(jobRegistry));
        }
    }

    @Test
    void getAttributesAsMap() {
        for (ObjectResult or : objectResults) {
            Assert.assertFalse(or.getAttributesAsMap(or.getAttributes(),new LinkedTreeMap<String,Object>()).isEmpty());
        }
    }

    @Test
    void getRecursiveJobRegistry() {
        for (ObjectResult or : objectResults) {
            Assert.assertTrue(or.getRecursiveJobRegistry().equals(jobRegistry));
        }
    }

    @Test
    void toSimplifiedJson() {
        for (ObjectResult or : objectResults) {
            JsonObject jOR = or.toSimplifiedJson(false,null);
            Assert.assertNotNull(jOR);
            Assert.assertTrue(jOR.has("node"));
            Assert.assertTrue(jOR.get("node").getAsString().equals(or.getNode()));
            Assert.assertTrue(jOR.has("tripleStore"));
            Assert.assertTrue(jOR.get("tripleStore").getAsString().equals(or.getTripleStore()));
            Assert.assertTrue(jOR.has("entityId"));
            Assert.assertTrue(jOR.get("entityId").getAsString().equals(or.getEntityId()));
            Assert.assertTrue(jOR.has("localUri"));
            Assert.assertTrue(jOR.get("localUri").getAsString().equals(or.getLocalURI()));
            Assert.assertTrue(jOR.has("attributes"));
            Assert.assertNotNull(jOR.get("attributes").getAsJsonObject());
        }
    }

    @Test
    void setId() {
        for (ObjectResult or : objectResults) {
            long rnd = Math.abs(new Random().nextLong());
            or.setId(rnd);
            Assert.assertTrue(or.getId() == rnd);
        }
    }

    @Test
    void setNode() {
        for (ObjectResult or : objectResults) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            or.setNode(rnd);
            Assert.assertTrue(or.getNode().equals(rnd));
        }
    }

    @Test
    void setTripleStore() {
        for (ObjectResult or : objectResults) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            or.setTripleStore(rnd);
            Assert.assertTrue(or.getTripleStore().equals(rnd));
        }
    }

    @Test
    void setClassName() {
        for (ObjectResult or : objectResults) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            or.setClassName(rnd);
            Assert.assertTrue(or.getClassName().equals(rnd));
        }
    }

    @Test
    void setLocalURI() {
        for (ObjectResult or : objectResults) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            or.setLocalURI(rnd);
            Assert.assertTrue(or.getLocalURI().equals(rnd));
        }
    }

    @Test
    void setLastModification() {
        for (ObjectResult or : objectResults) {
            Date d = new Date();
            or.setLastModification(d);
            Assert.assertTrue(or.getLastModification().equals(d));
        }
    }

    @Test
    void setJobRegistry() {
        for (ObjectResult or : objectResults) {
            or.setJobRegistry(jobRegistry);
            Assert.assertTrue(or.getJobRegistry().equals(jobRegistry));
        }
    }

    @Test
    void setEntityId() {
        for (ObjectResult or : objectResults) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            or.setEntityId(rnd);
            Assert.assertTrue(or.getEntityId().equals(rnd));
        }
    }

    @Test
    void setAttributes() {
        for (ObjectResult or : objectResults) {
            Set<Attribute> attrs = or.getAttributes();
            attrs.remove(attrs.toArray()[0]);
            or.setAttributes(attrs);
            Assert.assertTrue(or.getAttributes().equals(attrs));
        }
    }

    @Test
    void setAutomatic() {
        for (ObjectResult or : objectResults) {
            or.setAutomatic(or.getAutomatic());
            Assert.assertNotNull(or.getAutomatic());
        }
    }

    @Test
    void setParentAutomatic() {
        for (ObjectResult or : objectResults) {
            or.setParentAutomatic(or);
            Assert.assertTrue(or.getParentAutomatic().equals(or));
        }
    }

    @Test
    void setManual() {
        for (ObjectResult or : objectResults) {
            or.setManual(or.getManual());
            Assert.assertNotNull(or.getManual());
        }
    }

    @Test
    void setParentManual() {
        for (ObjectResult or : objectResults) {
            or.setParentManual(or);
            Assert.assertTrue(or.getParentManual().equals(or));
        }
    }

    @Test
    void setLink() {
        for (ObjectResult or : objectResults) {
            or.setLink(or.getLink());
            Assert.assertNotNull(or.getLink());
        }
    }

    @Test
    void setParentLink() {
        for (ObjectResult or : objectResults) {
            or.setParentLink(or);
            Assert.assertTrue(or.getParentLink().equals(or));
        }
    }

    @Test
    void setActionResults() {
        for (ObjectResult or : objectResults) {
            Set<ActionResult> actionResults = or.getActionResults();
            actionResults.remove(actionResults.toArray()[0]);
            or.setActionResults(actionResults);
            Assert.assertTrue(or.getActionResults().equals(actionResults));

        }
    }

    @Test
    void setSimilarity() {
        for (ObjectResult or : objectResults) {
            float rnd = Math.abs(new Random().nextFloat())/Float.MAX_VALUE;
            or.setSimilarity(rnd);
            Assert.assertTrue(or.getSimilarity() == rnd);
        }
    }

    @Test
    void setMain() {
        for (ObjectResult or : objectResults) {
            or.setMain(true);
            Assert.assertTrue(or.isMain());
        }
    }

    @Test
    void testSetAutomatic() {
        for (ObjectResult or : objectResults) {
            or.setAutomatic(or.getAutomatic());
            Assert.assertNotNull(or.getAutomatic());
        }
    }

    @Test
    void testSetManual() {
        for (ObjectResult or : objectResults) {
            or.setManual(or.getAutomatic());
            Assert.assertNotNull(or.getManual());
        }
    }

    @Test
    void setMerge() {
        for (ObjectResult or : objectResults) {
            or.setMerge(true);
            Assert.assertTrue(or.isMerge());
        }
    }

    @Test
    void testSetLink() {
        for (ObjectResult or : objectResults) {
            or.setLink(or.getLink());
            Assert.assertNotNull(or.getLink());
        }
    }

    @Test
    void setMergeAction() {
        for (ObjectResult or : objectResults) {
            or.setMergeAction(MergeAction.UPDATE);
            Assert.assertEquals(MergeAction.UPDATE,or.getMergeAction());
        }
    }

    @Test
    void setActionResultParent() {
        for (ObjectResult or : objectResults) {
            ActionResult actionResult = (ActionResult) or.getActionResults().toArray()[0];
            or.setActionResultParent(actionResult);
            Assert.assertEquals(or.getActionResultParent(),actionResult);
        }
    }

    @Test
    void getId() {
        for (ObjectResult or : objectResults) {
            long rnd = Math.abs(new Random().nextLong());
            or.setId(rnd);
            Assert.assertTrue(or.getId() == rnd);
        }
    }

    @Test
    void getNode() {
        for (ObjectResult or : objectResults) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            or.setNode(rnd);
            Assert.assertTrue(or.getNode().equals(rnd));
        }
    }

    @Test
    void getTripleStore() {
        for (ObjectResult or : objectResults) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            or.setTripleStore(rnd);
            Assert.assertTrue(or.getTripleStore().equals(rnd));
        }
    }

    @Test
    void getClassName() {
        for (ObjectResult or : objectResults) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            or.setClassName(rnd);
            Assert.assertTrue(or.getClassName().equals(rnd));
        }
    }

    @Test
    void getLocalURI() {
        for (ObjectResult or : objectResults) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            or.setLocalURI(rnd);
            Assert.assertTrue(or.getLocalURI().equals(rnd));
        }
    }

    @Test
    void getLastModification() {
        for (ObjectResult or : objectResults) {
            Date d = new Date();
            or.setLastModification(d);
            Assert.assertTrue(or.getLastModification().equals(d));
        }
    }

    @Test
    void getJobRegistry() {
        for (ObjectResult or : objectResults) {
            or.setJobRegistry(jobRegistry);
            Assert.assertTrue(or.getJobRegistry().equals(jobRegistry));
        }
    }

    @Test
    void getEntityId() {
        for (ObjectResult or : objectResults) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            or.setEntityId(rnd);
            Assert.assertTrue(or.getEntityId().equals(rnd));
        }
    }

    @Test
    void getAttributes() {
        for (ObjectResult or : objectResults) {
            Set<Attribute> attrs = or.getAttributes();
            attrs.remove(attrs.toArray()[0]);
            or.setAttributes(attrs);
            Assert.assertTrue(or.getAttributes().equals(attrs));
        }
    }

    @Test
    void getAutomatic() {
        for (ObjectResult or : objectResults) {
            or.setAutomatic(or.getAutomatic());
            Assert.assertNotNull(or.getAutomatic());
        }
    }

    @Test
    void getParentAutomatic() {
        for (ObjectResult or : objectResults) {
            or.setParentAutomatic(or);
            Assert.assertTrue(or.getParentAutomatic().equals(or));
        }
    }

    @Test
    void getManual() {
        for (ObjectResult or : objectResults) {
            or.setManual(or.getManual());
            Assert.assertNotNull(or.getManual());
        }
    }

    @Test
    void getParentManual() {
        for (ObjectResult or : objectResults) {
            or.setParentManual(or);
            Assert.assertTrue(or.getParentManual().equals(or));
        }
    }

    @Test
    void getLink() {
        for (ObjectResult or : objectResults) {
            or.setLink(or.getLink());
            Assert.assertNotNull(or.getLink());
        }
    }

    @Test
    void getParentLink() {
        for (ObjectResult or : objectResults) {
            or.setParentLink(or);
            Assert.assertTrue(or.getParentLink().equals(or));
        }
    }

    @Test
    void getActionResults() {
        for (ObjectResult or : objectResults) {
            Set<ActionResult> actionResults = or.getActionResults();
            actionResults.remove(actionResults.toArray()[0]);
            or.setActionResults(actionResults);
            Assert.assertTrue(or.getActionResults().equals(actionResults));

        }
    }

    @Test
    void getSimilarity() {
        for (ObjectResult or : objectResults) {
            float rnd = Math.abs(new Random().nextFloat())/Float.MAX_VALUE;
            or.setSimilarity(rnd);
            Assert.assertTrue(or.getSimilarity() == rnd);
        }
    }

    @Test
    void isMain() {
        for (ObjectResult or : objectResults) {
            or.setMain(true);
            Assert.assertTrue(or.isMain());
        }
    }

    @Test
    void isAutomatic() {
        for (ObjectResult or : objectResults) {
            or.setAutomatic(or.getAutomatic());
            Assert.assertNotNull(or.getAutomatic());
        }
    }

    @Test
    void isManual() {
        for (ObjectResult or : objectResults) {
            or.setManual(or.getAutomatic());
            Assert.assertNotNull(or.getManual());
        }
    }

    @Test
    void isMerge() {
        for (ObjectResult or : objectResults) {
            or.setMerge(true);
            Assert.assertTrue(or.isMerge());
        }
    }

    @Test
    void isLink() {
        for (ObjectResult or : objectResults) {
            or.setLink(or.getLink());
            Assert.assertNotNull(or.getLink());
        }
    }

    @Test
    void getMergeAction() {
        for (ObjectResult or : objectResults) {
            or.setMergeAction(MergeAction.UPDATE);
            Assert.assertEquals(MergeAction.UPDATE,or.getMergeAction());
        }
    }

    @Test
    void getActionResultParent() {
        for (ObjectResult or : objectResults) {
            ActionResult actionResult = (ActionResult) or.getActionResults().toArray()[0];
            or.setActionResultParent(actionResult);
            Assert.assertEquals(or.getActionResultParent(),actionResult);
        }
    }

    @Test
    void testEquals() {
        for (ObjectResult or : objectResults) {
            for (ObjectResult orInner : objectResults) {
                if (or.getId() == orInner.getId())
                    Assert.assertTrue(or.equals(orInner));
                else
                    Assert.assertFalse(or.equals(orInner));
            }
        }
    }

    @Test
    void canEqual() {
        for (ObjectResult or : objectResults) {
            Assert.assertTrue(or.canEqual( or));
        }
    }

    @Test
    void testHashCode() {
        for (ObjectResult or : objectResults) {
            for (ObjectResult orInner : objectResults) {
                if (or.getId() == orInner.getId())
                    Assert.assertTrue(or.hashCode() == orInner.hashCode());
                else
                    Assert.assertFalse(or.hashCode() == orInner.hashCode());
            }
        }
    }
}