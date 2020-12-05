package es.um.asio.service.model.relational;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.model.TripleObject;
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
class ActionResultTest {

    Set<ActionResult> actionResults;
    DataGenerator dg;

    @BeforeEach
    public void init() throws Exception {
        actionResults = new HashSet<>();
        dg = new DataGenerator();
        JobRegistry jobRegistry = dg.getJobRegistry();

        for(ObjectResult or : jobRegistry.getObjectResults()) {
            actionResults.addAll(or.getActionResults());
        }
    }

    @Test
    void addObjectResult() {
        TripleObject to = dg.getTripleObjects().get(0);
        for (ActionResult actionResult : actionResults) {
            ObjectResult orNew = new ObjectResult(
                    null,
                    to,
                    0f
            );
            actionResult.addObjectResult(orNew);
            Assert.assertTrue(actionResult.getObjectResults().contains(orNew));
        }
    }

    @Test
    void setId() {
        for (ActionResult actionResult : actionResults) {
            long id = new Random().nextLong();
            actionResult.setId(id);
            Assert.assertTrue(id == actionResult.getId());
        }
    }

    @Test
    void setAction() {
        for (ActionResult actionResult : actionResults) {
            int rnd = (int) ((Math.random() * (4 - 0)) + 0);
            Action action = Action.values()[rnd];
            actionResult.setAction(action);
            actionResult.getAction().equals(action);
        }
    }

    @Test
    void setObjectResults() {
        for (ActionResult actionResult : actionResults) {
            Set<ObjectResult> ors = actionResult.getObjectResults();
            if (ors.size()>0) {
                ors.remove(ors.toArray()[0]);
            }
            actionResult.setObjectResults(ors);
            Assert.assertTrue(actionResult.getObjectResults().size() == ors.size());
        }
    }

    @Test
    void setObjectResultParent() {
        TripleObject to = dg.getTripleObjects().get(0);
        for (ActionResult actionResult : actionResults) {
            ObjectResult orNew = new ObjectResult(
                    null,
                    to,
                    0f
            );
            actionResult.setObjectResultParent(orNew);
            Assert.assertTrue(actionResult.getObjectResultParent().equals(orNew));
        }
    }

    @Test
    void getId() {
        for (ActionResult actionResult : actionResults) {
            int rnd = (int) ((Math.random() * (4 - 0)) + 0);
            actionResult.setId(rnd);
            Assert.assertTrue(actionResult.getId()==rnd);
        }
    }

    @Test
    void getAction() {
        for (ActionResult actionResult : actionResults) {
            Assert.assertTrue(Arrays.asList(Action.values()).contains(actionResult.getAction()));
        }
    }

    @Test
    void getObjectResults() {
        for (ActionResult actionResult : actionResults) {
            Assert.assertNotNull(actionResult.getObjectResults());
        }
    }

    @Test
    void getObjectResultParent() {
        for (ActionResult actionResult : actionResults) {
            Assert.assertNotNull(actionResult.getObjectResultParent());
        }
    }

    @Test
    void testEquals() {
        for (ActionResult actionResult : actionResults) {
            for (ActionResult actionResultInner : actionResults) {
                if (actionResult.getId() == actionResultInner.getId()) {
                    Assert.assertTrue(actionResult.equals(actionResultInner));
                }
            }
        }
    }

    @Test
    void canEqual() {
        for (ActionResult actionResult : actionResults) {
            for (ActionResult actionResultInner : actionResults) {
                if (actionResult.getId() == actionResultInner.getId()) {
                    Assert.assertTrue(actionResult.canEqual(actionResultInner));
                }
            }
        }
    }

    @Test
    void testHashCode() {
        for (ActionResult actionResult : actionResults) {
            for (ActionResult actionResultInner : actionResults) {
                if (actionResult.getId() == actionResultInner.getId()) {
                    Assert.assertTrue(actionResult.hashCode() == actionResultInner.hashCode());
                }
            }
        }
    }
}