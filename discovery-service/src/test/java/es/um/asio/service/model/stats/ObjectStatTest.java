package es.um.asio.service.model.stats;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
class ObjectStatTest {

    ObjectStat os;
    float ratio;

    @BeforeEach
    public void setUp() {
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
    void getName() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        os.setName(rnd);
        Assert.assertEquals(os.getName(),rnd);
    }


    @Test
    void getRelativeImportanceRatio() {
        Assert.assertTrue(os.getRelativeImportanceRatio() == ratio);
    }
}