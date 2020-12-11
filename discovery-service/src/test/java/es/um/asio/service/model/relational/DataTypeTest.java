package es.um.asio.service.model.relational;

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
        Assert.assertTrue(values.size() == 8);
        Assert.assertTrue(values.contains(DataType.BOOLEAN));
        Assert.assertTrue(values.contains(DataType.DATE));
        Assert.assertTrue(values.contains(DataType.DOUBLE));
        Assert.assertTrue(values.contains(DataType.FLOAT));
        Assert.assertTrue(values.contains(DataType.INTEGER));
        Assert.assertTrue(values.contains(DataType.LONG));
        Assert.assertTrue(values.contains(DataType.OBJECT));
        Assert.assertTrue(values.contains(DataType.STRING));
    }

    @Test
    void valueOf() {
        List<DataType> values = Arrays.asList(DataType.values());
        for (DataType dt : values) {
            Assert.assertTrue(DataType.valueOf(dt.toString()).equals(dt));
        }
    }
}