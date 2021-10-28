package es.um.asio.service.comparators.attribute;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
class AttributeSimilarityTest {

    @Test
    void compareInteger() {
        int rnd1 = new Random().nextInt();
        int rnd2;
        do {
            rnd2 = new Random().nextInt();
        } while (rnd2 == rnd1);

        Assert.assertTrue(AttributeSimilarity.compareInteger(rnd1,rnd1,1f,1).getSimilarity() == 1f);
        Assert.assertTrue(AttributeSimilarity.compareInteger(rnd1,rnd2,1f,1).getSimilarity() <= .5f);
    }

    @Test
    void compareLong() {
        long rnd1 = new Random().nextLong();
        long rnd2;
        do {
            rnd2 = new Random().nextLong();
        } while (rnd2 == rnd1);

        Assert.assertTrue(AttributeSimilarity.compareLong(rnd1,rnd1,1f,1).getSimilarity() == 1f);
        Assert.assertTrue(AttributeSimilarity.compareLong(rnd1,rnd2,1f,1).getSimilarity() <= .5f);
    }

    @Test
    void compareDate() {
        Date rnd1 = new Date(Math.abs(new Random().nextLong()));
        Date rnd2;
        do {
            rnd2 = new Date(Math.abs(new Random().nextLong()));
        } while (rnd2.equals(rnd1));

        Assert.assertTrue(AttributeSimilarity.compareDate(rnd1,rnd1,1f,1).getSimilarity() == 1f);
        Assert.assertTrue(AttributeSimilarity.compareDate(rnd1,rnd2,1f,1).getSimilarity() <= .5f);
    }

    @Test
    void compareDouble() {
        double rnd1 = new Random().nextDouble();
        double rnd2;
        do {
            rnd2 = new Random().nextDouble();
        } while (rnd2 == rnd1);

        Assert.assertTrue(AttributeSimilarity.compareDouble(rnd1,rnd1,1f,1).getSimilarity() == 1f);
        Assert.assertTrue(AttributeSimilarity.compareDouble(rnd1,rnd2,1f,1).getSimilarity() <= .5f);
    }

    @Test
    void compareFloat() {
        float rnd1 = new Random().nextFloat();
        float rnd2;
        do {
            rnd2 = new Random().nextFloat();
        } while (rnd2 == rnd1);

        Assert.assertTrue(AttributeSimilarity.compareFloat(rnd1,rnd1,1f,1).getSimilarity() == 1f);
        Assert.assertTrue(AttributeSimilarity.compareFloat(rnd1,rnd2,1f,1).getSimilarity() <= .5f);
    }

    @Test
    void compareString() {
        String rnd1 = RandomStringUtils.randomAlphabetic(10);
        String rnd2;
        do {
            rnd2 = RandomStringUtils.randomAlphabetic(10);
        } while (rnd2.equals(rnd1));

        Assert.assertTrue(AttributeSimilarity.compareString(rnd1,rnd1,1f,1).getSimilarity() == 1f);
        Assert.assertTrue(AttributeSimilarity.compareString(rnd1,rnd2,1f,1).getSimilarity() <= .5f);
    }

    @Test
    void compareBoolean() {
        boolean rnd1 = new Random().nextBoolean();
        boolean rnd2;
        do {
            rnd2 = !rnd1;
        } while (rnd2 == rnd1);

        Assert.assertTrue(AttributeSimilarity.compareBoolean(rnd1,rnd1,1f,1).getSimilarity() == 1f);
        Assert.assertTrue(AttributeSimilarity.compareBoolean(rnd1,rnd2,1f,1).getSimilarity() <= .5f);
    }

    @Test
    void compareList() {
        List<Object> l1 = new ArrayList<>();
        List<Object> l2 = new ArrayList<>();
        for (int i = 1 ; i <=5 ; i++) {
            l1.add(RandomStringUtils.randomAlphabetic(10));
            l2.add(RandomStringUtils.randomAlphabetic(10));
        }
        Assert.assertTrue(AttributeSimilarity.compareList(l1,l1,1f,1).getSimilarity() == 1f);
        Assert.assertTrue(AttributeSimilarity.compareList(l1,l2,1f,1).getSimilarity() <= .5f);
    }

    @Test
    void compare() {
        List<Object> l1 = new ArrayList<>();
        List<Object> l2 = new ArrayList<>();
        for (int i = 1 ; i <=5 ; i++) {
            l1.add(RandomStringUtils.randomAlphabetic(10));
            l2.add(RandomStringUtils.randomAlphabetic(10));
        }
        Assert.assertTrue(AttributeSimilarity.compare(l1,l1,1f,1).getSimilarity() == 1f);
        Assert.assertTrue(AttributeSimilarity.compare(l1,l2,1f,1).getSimilarity() <= .5f);
    }

    @Test
    void isNumber() {
        Assert.assertTrue(AttributeSimilarity.isNumber(String.valueOf(new Random().nextInt())));
        Assert.assertFalse(AttributeSimilarity.isNumber(RandomStringUtils.randomAlphabetic(10)));
    }

    @Test
    void isBoolean() {
        Assert.assertTrue(AttributeSimilarity.isBoolean(new Random().nextBoolean()));
        Assert.assertFalse(AttributeSimilarity.isBoolean(RandomStringUtils.randomAlphabetic(10)));
    }

    @Test
    void isDate() {
        Assert.assertTrue(AttributeSimilarity.isDate("2020-01-01"));
        Assert.assertFalse(AttributeSimilarity.isDate(RandomStringUtils.randomAlphabetic(10)));
    }

    @Test
    void getDate() {
        Assert.assertTrue(AttributeSimilarity.getDate("2020-01-01") instanceof Date);
    }

    @Test
    void getClassOffAttributes() {
        Assert.assertTrue(AttributeSimilarity.getClassOffAttributes(new Random().nextInt(),new Random().nextInt()).equals("int"));
    }
}