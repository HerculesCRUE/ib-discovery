package es.um.asio.service.model.relational;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import javax.validation.constraints.AssertTrue;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class ValueTest {

    Set<Value> values;
    Set<Attribute> attributes;
    JobRegistry jr;

    @PostConstruct
    private void init() throws Exception {
        DataGenerator dg = new DataGenerator();
        jr = dg.getJobRegistry();
        values = new HashSet<>();
        attributes = new HashSet<>();
        for (ObjectResult or : jr.getObjectResults()) {
            for (Attribute att : or.getAttributes()) {
                attributes.add(att);
                values.addAll(att.getValues());
            }
        }
    }

    @Test
    void getValueParsedToType() {
        for (Value v :values) {
            Assert.assertTrue(v.getValueParsedToType() instanceof Object);
        }
    }

    @Test
    void getDataType() {
        for (Value v :values) {
            Assert.assertTrue(new ArrayList<DataType>(Arrays.asList(DataType.values())).contains(v.getDataType()));
        }
    }

    @Test
    void setId() {
        for (Value v :values) {
            long rnd = Math.abs(new Random().nextLong());
            v.setId(rnd);
            Assert.assertTrue(v.getId() == rnd);
        }
    }

    @Test
    void setAttribute() {
        for (Value v :values) {
            Attribute att = new Attribute();
            v.setAttribute(att);
            Assert.assertEquals(v.getAttribute(),att);
        }
    }

    @Test
    void setDataType() {
        int i = 0;
        for (Value v :values) {
            DataType dt = DataType.values()[i%DataType.values().length];
            v.setDataType(dt);
            Assert.assertEquals(v.getDataType(),dt);
            i++;
        }
    }

    @Test
    void setValue() {
        for (Value v :values) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            v.setValue(rnd);
            Assert.assertEquals(v.getValue(),rnd);
        }
    }

    @Test
    void setAttributes() {
        for (Value v :values) {
            v.setAttributes(attributes);
            Assert.assertEquals(v.getAttributes(),attributes);
        }
    }

    @Test
    void getId() {
        for (Value v :values) {
            long rnd = Math.abs(new Random().nextLong());
            v.setId(rnd);
            Assert.assertTrue(v.getId() == rnd);
        }
    }

    @Test
    void getAttribute() {
        for (Value v :values) {
            Attribute att = new Attribute();
            v.setAttribute(att);
            Assert.assertEquals(v.getAttribute(),att);
        }
    }

    @Test
    void testGetDataType() {
        int i = 0;
        for (Value v :values) {
            DataType dt = DataType.values()[i%DataType.values().length];
            v.setDataType(dt);
            Assert.assertEquals(v.getDataType(),dt);
            i++;
        }
    }

    @Test
    void getValue() {
        for (Value v :values) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            v.setValue(rnd);
            Assert.assertEquals(v.getValue(),rnd);
        }
    }

    @Test
    void getAttributes() {
        for (Value v :values) {
            v.setAttributes(attributes);
            Assert.assertEquals(v.getAttributes(),attributes);
        }
    }

    @Test
    void testEquals() {
        for (Value v :values) {
            for (Value vInner :values) {
                if (v.hashCode() == vInner.hashCode())
                    Assert.assertTrue(v.equals(vInner));
                else
                    Assert.assertFalse(v.equals(vInner));
            }
        }
    }

    @Test
    void canEqual() {
        for (Value v :values) {
            Assert.assertTrue(v.canEqual(v));
        }
    }

    @Test
    void testHashCode() {
        for (Value v :values) {
            for (Value vInner :values) {
                if (v.equals(vInner))
                    Assert.assertTrue(v.hashCode() == vInner.hashCode());
                else
                    Assert.assertFalse(v.hashCode() == vInner.hashCode());
            }
        }
    }
}