package es.um.asio.service.model.relational;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
class RequestTypeTest {

    @Test
    void values() {
        List<RequestType> values = Arrays.asList(RequestType.values());
        Assert.assertTrue(values.size() == 2);
        Assert.assertTrue(values.contains(RequestType.ENTITY_LINK_CLASS));
        Assert.assertTrue(values.contains(RequestType.ENTITY_LINK_INSTANCE));
    }

    @Test
    void valueOf() {
        List<RequestType> values = Arrays.asList(RequestType.values());
        for (RequestType a : values) {
            Assert.assertTrue(RequestType.valueOf(a.toString()).equals(a));
        }
    }
}