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

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
class AttributeTypeTest {

    @Test
    void values() {
        Set<AttributeType> attributeTypes = new HashSet(Arrays.asList(AttributeType.values()));
        Assert.assertTrue(attributeTypes.size() == AttributeType.values().length);
        Assert.assertTrue(attributeTypes.contains(AttributeType.INTEGER));
        Assert.assertTrue(attributeTypes.contains(AttributeType.NUMBER));
        Assert.assertTrue(attributeTypes.contains(AttributeType.STRING));
        Assert.assertTrue(attributeTypes.contains(AttributeType.OBJECT));
        Assert.assertTrue(attributeTypes.contains(AttributeType.BOOLEAN));
        Assert.assertTrue(attributeTypes.contains(AttributeType.DATE));
    }

    @Test
    void valueOf() {
        Assert.assertTrue(AttributeType.valueOf("INTEGER").equals(AttributeType.INTEGER));
        Assert.assertTrue(AttributeType.valueOf("NUMBER").equals(AttributeType.NUMBER));
        Assert.assertTrue(AttributeType.valueOf("STRING").equals(AttributeType.STRING));
        Assert.assertTrue(AttributeType.valueOf("OBJECT").equals(AttributeType.OBJECT));
        Assert.assertTrue(AttributeType.valueOf("BOOLEAN").equals(AttributeType.BOOLEAN));
        Assert.assertTrue(AttributeType.valueOf("DATE").equals(AttributeType.DATE));
    }
}