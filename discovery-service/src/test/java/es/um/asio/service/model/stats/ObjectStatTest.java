package es.um.asio.service.model.stats;

import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.model.relational.ObjectResult;
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

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class ObjectStatTest {

    ObjectStat os;
    float ratio;

    @PostConstruct
    public void init() {
        ratio = Math.abs(new Random().nextFloat());
        os = new ObjectStat() {
            @Override
            public float getRelativeImportanceRatio() {
                return ratio;
            }
        };
    }

    @Test
    void setName() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        os.setName(rnd);
        Assert.assertEquals(os.getName(),rnd);
    }

    @Test
    void setCounter() {
        int rnd = Math.abs(new Random().nextInt());
        os.setCounter(rnd);
        Assert.assertEquals(os.getCounter(),rnd);
    }

    @Test
    void getName() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        os.setName(rnd);
        Assert.assertEquals(os.getName(),rnd);
    }

    @Test
    void getCounter() {
        int rnd = Math.abs(new Random().nextInt());
        os.setCounter(rnd);
        Assert.assertEquals(os.getCounter(),rnd);
    }

    @Test
    void getRelativeImportanceRatio() {
        Assert.assertTrue(os.getRelativeImportanceRatio() == ratio);
    }
}