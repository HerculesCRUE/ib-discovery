package es.um.asio.service.model.appstate;

import es.um.asio.service.TestDiscoveryApplication;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class DataTypeTest {

    @Test
    void values() {
        List<DataType> values = Arrays.asList(DataType.values());
        Assert.assertTrue(values.size() == 3);
        Assert.assertTrue(values.contains(DataType.CACHE));
        Assert.assertTrue(values.contains(DataType.REDIS));
        Assert.assertTrue(values.contains(DataType.ELASTICSEARCH));
    }

    @Test
    void valueOf() {
        List<DataType> values = Arrays.asList(DataType.values());
        for (DataType dt : values) {
            Assert.assertTrue(DataType.valueOf(dt.toString()).equals(dt));
        }
    }
}