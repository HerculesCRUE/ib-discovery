package es.um.asio.service.comparators.attribute;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
class SpecialComparatorTest {

    @Test
    void compare() {
        List<Object> l1 = new ArrayList<>();
        List<Object> l2 = new ArrayList<>();
        for (int i = 1 ; i <=5 ; i++) {
            l1.add(RandomStringUtils.randomAlphabetic(10));
            l2.add(RandomStringUtils.randomAlphabetic(10));
        }
        Assert.assertTrue(AttributeSimilarity.compare(l1,l1,1f).getSimilarity() == 1f);
        Assert.assertTrue(AttributeSimilarity.compare(l1,l2,1f).getSimilarity() <= .5f);
    }
}