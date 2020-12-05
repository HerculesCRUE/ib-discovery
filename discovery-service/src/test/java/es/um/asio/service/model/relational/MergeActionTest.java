package es.um.asio.service.model.relational;

import es.um.asio.service.TestDiscoveryApplication;
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
class MergeActionTest {

    @Test
    void values() {
        List<MergeAction> values = Arrays.asList(MergeAction.values());
        Assert.assertTrue(values.size() == 3);
        Assert.assertTrue(values.contains(MergeAction.UPDATE));
        Assert.assertTrue(values.contains(MergeAction.INSERT));
        Assert.assertTrue(values.contains(MergeAction.DELETE));
    }

    @Test
    void valueOf() {
        List<MergeAction> values = Arrays.asList(MergeAction.values());
        for (MergeAction a : values) {
            Assert.assertTrue(MergeAction.valueOf(a.toString()).equals(a));
        }
    }
}