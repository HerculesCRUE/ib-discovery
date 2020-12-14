package es.um.asio.back.config.properties;

import es.um.asio.service.util.Utils;
import jdk.jshell.execution.Util;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CorsPropertiesTest {

    CorsProperties cp;

    @BeforeEach
    void setUp() {
        cp = new CorsProperties();
        cp.setAllowedHeaders(new String[]{"Authorization","X-Requested-With","Content-Type"});
        cp.setEnabled(true);
        cp.setAllowedOrigin("*");
        cp.setAllowedMethods("GET,POST,PUT,DELETE");

    }

    @Test
    void getAllowedHeaders() {
        List<String> allowedHeaders = Arrays.asList(cp.getAllowedHeaders());
        Assert.assertTrue(allowedHeaders.size() == 3);
        Assert.assertTrue(allowedHeaders.contains("Authorization"));
        Assert.assertTrue(allowedHeaders.contains("X-Requested-With"));
        Assert.assertTrue(allowedHeaders.contains("Content-Type"));
    }

    @Test
    void setAllowedHeaders() {
        cp.setAllowedHeaders(new String[]{"1","2","3"});
        List<String> allowedHeaders = Arrays.asList(cp.getAllowedHeaders());
        Assert.assertTrue(allowedHeaders.size() == 3);
        Assert.assertTrue(allowedHeaders.contains("1"));
        Assert.assertTrue(allowedHeaders.contains("2"));
        Assert.assertTrue(allowedHeaders.contains("3"));
    }

    @Test
    void testToString() {
        Assert.assertTrue(Utils.isValidString(cp.toString()));
    }

    @Test
    void setEnabled() {
        boolean rnd = new Random().nextBoolean();
        cp.setEnabled(rnd);
        Assert.assertEquals(cp.isEnabled(),rnd);
    }

    @Test
    void setAllowedOrigin() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        cp.setAllowedOrigin(rnd);
        Assert.assertEquals(cp.getAllowedOrigin(),rnd);
    }

    @Test
    void setAllowedMethods() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        cp.setAllowedMethods(rnd);
        Assert.assertEquals(cp.getAllowedMethods(),rnd);
    }

    @Test
    void setMaxAge() {
        String rnd = String.valueOf(Math.abs(new Random().nextInt()));
        cp.setMaxAge(rnd);
        Assert.assertEquals(cp.getMaxAge(),rnd);
    }

    @Test
    void isEnabled() {
        boolean rnd = new Random().nextBoolean();
        cp.setEnabled(rnd);
        Assert.assertEquals(cp.isEnabled(),rnd);
    }

    @Test
    void getAllowedOrigin() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        cp.setAllowedOrigin(rnd);
        Assert.assertEquals(cp.getAllowedOrigin(),rnd);
    }

    @Test
    void getAllowedMethods() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        cp.setAllowedMethods(rnd);
        Assert.assertEquals(cp.getAllowedMethods(),rnd);
    }

    @Test
    void getMaxAge() {
        String rnd = String.valueOf(Math.abs(new Random().nextInt()));
        cp.setMaxAge(rnd);
        Assert.assertEquals(cp.getMaxAge(),rnd);
    }
}