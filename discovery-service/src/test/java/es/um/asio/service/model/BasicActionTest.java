package es.um.asio.service.model;

import es.um.asio.service.TestDiscoveryApplication;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class BasicActionTest {

    @Test
    void fromString() {
        Assert.assertTrue(BasicAction.fromString("INSERT") == BasicAction.INSERT);
        Assert.assertTrue(BasicAction.fromString("UPDATE") == BasicAction.UPDATE);
        Assert.assertTrue(BasicAction.fromString("DELETE") == BasicAction.DELETE);
        Assert.assertTrue(BasicAction.fromString("LINK") == BasicAction.LINK);
    }

    @Test
    void values() {
        Set<BasicAction> basicActions = new HashSet(Arrays.asList(BasicAction.values()));
        Assert.assertTrue(basicActions.size() == BasicAction.values().length);
        Assert.assertTrue(basicActions.contains(BasicAction.DELETE));
        Assert.assertTrue(basicActions.contains(BasicAction.UPDATE));
        Assert.assertTrue(basicActions.contains(BasicAction.INSERT));
        Assert.assertTrue(basicActions.contains(BasicAction.LINK));
    }

    @Test
    void valueOf() {
        Assert.assertTrue(BasicAction.valueOf("DELETE").equals(BasicAction.DELETE));
        Assert.assertTrue(BasicAction.valueOf("UPDATE").equals(BasicAction.UPDATE));
        Assert.assertTrue(BasicAction.valueOf("INSERT").equals(BasicAction.INSERT));
        Assert.assertTrue(BasicAction.valueOf("LINK").equals(BasicAction.LINK));
    }
}