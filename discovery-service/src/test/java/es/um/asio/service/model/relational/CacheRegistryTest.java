package es.um.asio.service.model.relational;

import com.google.gson.JsonObject;
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
import javax.validation.constraints.AssertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class CacheRegistryTest {

    List<CacheRegistry> cacheRegistries;
    DataGenerator dg;

    @PostConstruct
    public void init() throws Exception {
        cacheRegistries = new ArrayList<>();
        dg = new DataGenerator();
        JobRegistry jobRegistry = dg.getJobRegistry();
        DiscoveryApplication discoveryApplication = new DiscoveryApplication("app1");
        for (int i = 1 ; i <= 5 ; i++) {
            CacheRegistry cr = new CacheRegistry(
                    discoveryApplication,
                    String.format("node_%s",i),
                    String.format("tripleStore_%s",i),
                    String.format("className_%s",i)
            );
            cr.setId(i);
            cr.setLastUpdate(new Date());
            cacheRegistries.add(cr);
        }

    }

    @Test
    void setId() {
        for (CacheRegistry cr : cacheRegistries) {
            long id = new Random().nextLong();
            cr.setId(id);
            Assert.assertTrue(cr.getId() == id);
        }
    }

    @Test
    void setDiscoveryApplication() {
        DiscoveryApplication da = new DiscoveryApplication("app2");
        for (CacheRegistry cr : cacheRegistries) {
            cr.setDiscoveryApplication(da);
            Assert.assertTrue(cr.getDiscoveryApplication().equals(da));
        }
    }

    @Test
    void setNode() {
        for (CacheRegistry cr : cacheRegistries) {
            String node = RandomStringUtils.randomAlphabetic(10);
            cr.setNode(node);
            Assert.assertTrue(cr.getNode().equals(node));
        }
    }

    @Test
    void setTripleStore() {
        for (CacheRegistry cr : cacheRegistries) {
            String triple = RandomStringUtils.randomAlphabetic(10);
            cr.setTripleStore(triple);
            Assert.assertTrue(cr.getTripleStore().equals(triple));
        }
    }

    @Test
    void setClassName() {
        for (CacheRegistry cr : cacheRegistries) {
            String className = RandomStringUtils.randomAlphabetic(10);
            cr.setTripleStore(className);
            Assert.assertTrue(cr.getTripleStore().equals(className));
        }
    }

    @Test
    void setLastUpdate() {
        for (CacheRegistry cr : cacheRegistries) {
            Date d = new Date();
            cr.setLastUpdate(d);
            Assert.assertTrue(cr.getLastUpdate().equals(d));
        }
    }

    @Test
    void getId() {
        for (CacheRegistry cr : cacheRegistries) {
            Assert.assertTrue(cr.getId()!=0);
        }
    }

    @Test
    void getDiscoveryApplication() {
        for (CacheRegistry cr : cacheRegistries) {
            Assert.assertNotNull(cr.getDiscoveryApplication());
        }
    }

    @Test
    void getNode() {
        for (CacheRegistry cr : cacheRegistries) {
            Assert.assertTrue(Utils.isValidString(cr.getNode()));
        }
    }

    @Test
    void getTripleStore() {
        for (CacheRegistry cr : cacheRegistries) {
            Assert.assertTrue(Utils.isValidString(cr.getNode()));
        }
    }

    @Test
    void getClassName() {
        for (CacheRegistry cr : cacheRegistries) {
            Assert.assertTrue(Utils.isValidString(cr.getClassName()));
        }
    }

    @Test
    void getLastUpdate() {
        for (CacheRegistry cr : cacheRegistries) {
            Assert.assertTrue(Utils.isValidString(cr.getClassName()));
        }
    }

    @Test
    void testEquals() {
        for (CacheRegistry cr : cacheRegistries) {
            for (CacheRegistry crInner : cacheRegistries) {
                if (cr.getId() == crInner.getId())
                    Assert.assertTrue(cr.equals(crInner));
                else
                    Assert.assertFalse(cr.equals(crInner));
            }
        }
    }

    @Test
    void canEqual() {
        for (CacheRegistry cr : cacheRegistries) {
            for (CacheRegistry crInner : cacheRegistries) {
                if (cr.getId() == crInner.getId())
                    Assert.assertTrue(cr.equals(crInner));
                else
                    Assert.assertFalse(cr.equals(crInner));
            }
        }
    }

    @Test
    void testHashCode() {
        for (CacheRegistry cr : cacheRegistries) {
            for (CacheRegistry crInner : cacheRegistries) {
                if (cr.getId() == crInner.getId())
                    Assert.assertTrue(cr.hashCode() == crInner.hashCode());
                else
                    Assert.assertFalse(cr.hashCode() == crInner.hashCode());
            }
        }
    }
}