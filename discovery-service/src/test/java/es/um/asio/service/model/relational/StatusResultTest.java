package es.um.asio.service.model.relational;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
class StatusResultTest {

    @Test
    void values() {
        List<StatusResult> values = Arrays.asList(StatusResult.values());
        Assert.assertTrue(values.size() == 4);
        Assert.assertTrue(values.contains(StatusResult.PENDING));
        Assert.assertTrue(values.contains(StatusResult.COMPLETED));
        Assert.assertTrue(values.contains(StatusResult.FAIL));
        Assert.assertTrue(values.contains(StatusResult.ABORTED));
    }

    @Test
    void valueOf() {
        List<StatusResult> values = Arrays.asList(StatusResult.values());
        for (StatusResult a : values) {
            Assert.assertTrue(StatusResult.valueOf(a.toString()).equals(a));
        }
    }
}