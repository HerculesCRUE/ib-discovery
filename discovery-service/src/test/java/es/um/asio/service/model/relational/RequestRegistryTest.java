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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class RequestRegistryTest {

    Set<RequestRegistry> requestRegistries;
    JobRegistry jr;

    @PostConstruct
    public void init() throws Exception {
        DataGenerator dg = new DataGenerator();
        jr = dg.getJobRegistry();
        requestRegistries = jr.getRequestRegistries();
    }

    @Test
    void testEquals() {
        for (RequestRegistry rr : requestRegistries) {
            for (RequestRegistry rrInner : requestRegistries) {
                if (rr.hashCode() == rrInner.hashCode())
                    Assert.assertTrue(rr.equals(rrInner));
                else
                    Assert.assertFalse(rr.equals(rrInner));
            }
        }
    }

    @Test
    void testHashCode() {
        for (RequestRegistry rr : requestRegistries) {
            for (RequestRegistry rrInner : requestRegistries) {
                if (rr.equals(rrInner))
                    Assert.assertTrue(rr.hashCode() == rrInner.hashCode());
                else
                    Assert.assertFalse(rr.hashCode() == rrInner.hashCode());
            }
        }
    }

    @Test
    void setId() {
        for (RequestRegistry rr : requestRegistries) {
            long rnd = Math.abs(new Random().nextLong());
            rr.setId(rnd);
            Assert.assertTrue(rr.getId() == rnd);
        }
    }

    @Test
    void setUserId() {
        for (RequestRegistry rr : requestRegistries) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            rr.setUserId(rnd);
            Assert.assertTrue(rr.getUserId().equals(rnd));
        }
    }

    @Test
    void setRequestCode() {
        for (RequestRegistry rr : requestRegistries) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            rr.setRequestCode(rnd);
            Assert.assertTrue(rr.getRequestCode().equals(rnd));
        }
    }

    @Test
    void setRequestType() {
        int counter = 0;
        for (RequestRegistry rr : requestRegistries) {
            RequestType rt =  (counter%2 == 0)?RequestType.ENTITY_LINK_CLASS:RequestType.ENTITY_LINK_INSTANCE;
            rr.setRequestType(rt);
            Assert.assertTrue(rr.getRequestType().equals(rt));
            counter++;
        }
    }

    @Test
    void setRequestDate() {
        for (RequestRegistry rr : requestRegistries) {
            Date d = new Date();
            rr.setRequestDate(d);
            Assert.assertTrue(rr.getRequestDate().equals(d));
        }
    }

    @Test
    void setJobRegistry() {
        for (RequestRegistry rr : requestRegistries) {
            rr.setJobRegistry(jr);
            Assert.assertTrue(rr.getJobRegistry().equals(jr));
        }
    }

    @Test
    void setWebHook() {
        for (RequestRegistry rr : requestRegistries) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            rr.setWebHook(rnd);
            Assert.assertTrue(rr.getWebHook().equals(rnd));
        }
    }

    @Test
    void setPropagueInKafka() {
        int counter = 0;
        for (RequestRegistry rr : requestRegistries) {
            boolean propague =  (counter%2 == 0)?true:false;
            rr.setPropagueInKafka(propague);
            Assert.assertTrue(rr.isPropagueInKafka() == propague);
            counter++;
        }
    }

    @Test
    void getId() {
        for (RequestRegistry rr : requestRegistries) {
            long rnd = Math.abs(new Random().nextLong());
            rr.setId(rnd);
            Assert.assertTrue(rr.getId() == rnd);
        }
    }

    @Test
    void getUserId() {
        for (RequestRegistry rr : requestRegistries) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            rr.setUserId(rnd);
            Assert.assertTrue(rr.getUserId().equals(rnd));
        }
    }

    @Test
    void getRequestCode() {
        for (RequestRegistry rr : requestRegistries) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            rr.setRequestCode(rnd);
            Assert.assertTrue(rr.getRequestCode().equals(rnd));
        }
    }

    @Test
    void getRequestType() {
        int counter = 0;
        for (RequestRegistry rr : requestRegistries) {
            RequestType rt =  (counter%2 == 0)?RequestType.ENTITY_LINK_CLASS:RequestType.ENTITY_LINK_INSTANCE;
            rr.setRequestType(rt);
            Assert.assertTrue(rr.getRequestType().equals(rt));
            counter++;
        }
    }

    @Test
    void getRequestDate() {
        for (RequestRegistry rr : requestRegistries) {
            Date d = new Date();
            rr.setRequestDate(d);
            Assert.assertTrue(rr.getRequestDate().equals(d));
        }
    }

    @Test
    void getJobRegistry() {
        for (RequestRegistry rr : requestRegistries) {
            rr.setJobRegistry(jr);
            Assert.assertTrue(rr.getJobRegistry().equals(jr));
        }
    }

    @Test
    void getWebHook() {
        for (RequestRegistry rr : requestRegistries) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            rr.setWebHook(rnd);
            Assert.assertTrue(rr.getWebHook().equals(rnd));
        }
    }

    @Test
    void isPropagueInKafka() {
        int counter = 0;
        for (RequestRegistry rr : requestRegistries) {
            boolean propague =  (counter%2 == 0)?true:false;
            rr.setPropagueInKafka(propague);
            Assert.assertTrue(rr.isPropagueInKafka() == propague);
            counter++;
        }
    }
}