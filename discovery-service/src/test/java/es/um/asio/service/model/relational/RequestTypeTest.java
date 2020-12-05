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