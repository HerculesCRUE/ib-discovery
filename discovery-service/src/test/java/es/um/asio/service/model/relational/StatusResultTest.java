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
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
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