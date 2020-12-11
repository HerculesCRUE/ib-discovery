package es.um.asio.service.model.appstate;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
@RunWith(SpringRunner.class)
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