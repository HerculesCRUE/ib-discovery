package es.um.asio.service.model.relational;

import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.model.appstate.State;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class ActionTest {

    @Test
    void fromString() {
        List<Action> values = Arrays.asList(Action.values());
        for (Action a : values) {
            Assert.assertTrue(Action.fromString(a.toString()).equals(a));
        }
    }

    @Test
    void values() {
        List<Action> values = Arrays.asList(Action.values());
        Assert.assertTrue(values.size() == 4);
        Assert.assertTrue(values.contains(Action.INSERT));
        Assert.assertTrue(values.contains(Action.UPDATE));
        Assert.assertTrue(values.contains(Action.DELETE));
        Assert.assertTrue(values.contains(Action.LINK));
    }

    @Test
    void valueOf() {
        List<Action> values = Arrays.asList(Action.values());
        for (Action a : values) {
            Assert.assertTrue(Action.valueOf(a.toString()).equals(a));
        }
    }
}