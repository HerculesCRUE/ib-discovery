package es.um.asio.service.model.relational;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.util.Utils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class ElasticRegistryTest {

    List<ElasticRegistry> elasticRegistries;
    DataGenerator dg;

    @PostConstruct
    public void init() throws Exception {
        elasticRegistries = new ArrayList<>();
        dg = new DataGenerator();
        DiscoveryApplication discoveryApplication = new DiscoveryApplication("app1");
        for (int i = 1 ; i <= 5 ; i++) {
            ElasticRegistry er = new ElasticRegistry(
                    discoveryApplication,
                    String.format("node_%s",i),
                    String.format("tripleStore_%s",i),
                    String.format("className_%s",i),
                    i
            );
            er.setId(i);
            er.setLastUpdate(new Date());
            elasticRegistries.add(er);
        }

    }

    @Test
    void setId() {
        for (ElasticRegistry er : elasticRegistries) {
            long id = new Random().nextLong();
            er.setId(id);
            Assert.assertTrue(er.getId() == id);
        }
    }

    @Test
    void setDiscoveryApplication() {
        DiscoveryApplication da = new DiscoveryApplication("app2");
        for (ElasticRegistry er : elasticRegistries) {
            er.setDiscoveryApplication(da);
            Assert.assertTrue(er.getDiscoveryApplication().equals(da));
        }
    }

    @Test
    void setNode() {
        for (ElasticRegistry er : elasticRegistries) {
            String node = RandomStringUtils.randomAlphabetic(10);
            er.setNode(node);
            Assert.assertTrue(er.getNode().equals(node));
        }
    }

    @Test
    void setTripleStore() {
        for (ElasticRegistry er : elasticRegistries) {
            String triple = RandomStringUtils.randomAlphabetic(10);
            er.setTripleStore(triple);
            Assert.assertTrue(er.getTripleStore().equals(triple));
        }
    }

    @Test
    void setClassName() {
        for (ElasticRegistry er : elasticRegistries) {
            String className = RandomStringUtils.randomAlphabetic(10);
            er.setClassName(className);
            Assert.assertTrue(er.getClassName().equals(className));
        }
    }

    @Test
    void setInserted() {
        for (ElasticRegistry er : elasticRegistries) {
            int inserted = Math.abs(new Random().nextInt());
            er.setInserted(inserted);
            Assert.assertTrue(er.getInserted() == inserted);
        }
    }

    @Test
    void setLastUpdate() {
        for (ElasticRegistry er : elasticRegistries) {
            Date d = new Date();
            er.setLastUpdate(d);
            Assert.assertTrue(er.getLastUpdate().equals(d));
        }
    }

    @Test
    void getId() {
        for (ElasticRegistry er : elasticRegistries) {
            Assert.assertTrue(er.getId()!=0);
        }
    }

    @Test
    void getDiscoveryApplication() {
        for (ElasticRegistry er : elasticRegistries) {
            Assert.assertNotNull(er.getDiscoveryApplication());
        }
    }

    @Test
    void getNode() {
        for (ElasticRegistry er : elasticRegistries) {
            Assert.assertTrue(Utils.isValidString(er.getNode()));
        }
    }

    @Test
    void getTripleStore() {
        for (ElasticRegistry er : elasticRegistries) {
            Assert.assertTrue(Utils.isValidString(er.getTripleStore()));
        }
    }

    @Test
    void getClassName() {
        for (ElasticRegistry er : elasticRegistries) {
            Assert.assertTrue(Utils.isValidString(er.getClassName()));
        }
    }

    @Test
    void getInserted() {
        for (ElasticRegistry er : elasticRegistries) {
            Assert.assertTrue(er.getInserted() != 0);
        }
    }

    @Test
    void getLastUpdate() {
        for (ElasticRegistry er : elasticRegistries) {
            Assert.assertNotNull(er.getLastUpdate());
        }
    }

    @Test
    void testEquals() {
        for (ElasticRegistry er : elasticRegistries) {
            for (ElasticRegistry erInner : elasticRegistries) {
                if (er.getId() == erInner.getId())
                    Assert.assertTrue(er.equals(erInner));
                else
                    Assert.assertFalse(er.equals(erInner));
            }
        }
    }

    @Test
    void canEqual() {
        for (ElasticRegistry er : elasticRegistries) {
            for (ElasticRegistry erInner : elasticRegistries) {
                if (er.getId() == erInner.getId())
                    Assert.assertTrue(er.equals(erInner));
                else
                    Assert.assertFalse(er.equals(erInner));
            }
        }
    }

    @Test
    void testHashCode() {
        for (ElasticRegistry er : elasticRegistries) {
            for (ElasticRegistry erInner : elasticRegistries) {
                if (er.getId() == erInner.getId())
                    Assert.assertTrue(er.hashCode() == erInner.hashCode());
                else
                    Assert.assertFalse(er.hashCode() == erInner.hashCode());
            }
        }
    }
}