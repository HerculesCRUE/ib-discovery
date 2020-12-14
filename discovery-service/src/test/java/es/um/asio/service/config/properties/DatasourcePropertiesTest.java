package es.um.asio.service.config.properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatasourcePropertiesTest {

    DatasourceProperties dsp;

    @BeforeEach
    void setUp() {
        dsp = new DatasourceProperties();
        dsp.setDriverClassName("org.mariadb.jdbc.Driver");
        dsp.setUsername("app");
        dsp.setPassword("sqlpass");
        dsp.setUrl("jdbc:mariadb://127.0.0.1:3307/discovery?ssl=false&createDatabaseIfNotExist=true");

    }

    @Test
    void setDriverClassName() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        dsp.setDriverClassName(rnd);
        Assert.assertEquals(dsp.getDriverClassName(),rnd);
    }

    @Test
    void setUsername() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        dsp.setPassword(rnd);
        Assert.assertEquals(dsp.getPassword(),rnd);
    }

    @Test
    void setPassword() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        dsp.setUsername(rnd);
        Assert.assertEquals(dsp.getUsername(),rnd);
    }

    @Test
    void setUrl() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        dsp.setUrl(rnd);
        Assert.assertEquals(dsp.getUrl(),rnd);
    }

    @Test
    void setJndiName() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        dsp.setJndiName(rnd);
        Assert.assertEquals(dsp.getJndiName(),rnd);
    }

    @Test
    void getDriverClassName() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        dsp.setDriverClassName(rnd);
        Assert.assertEquals(dsp.getDriverClassName(),rnd);
    }

    @Test
    void getUsername() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        dsp.setPassword(rnd);
        Assert.assertEquals(dsp.getPassword(),rnd);
    }

    @Test
    void getPassword() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        dsp.setUsername(rnd);
        Assert.assertEquals(dsp.getUsername(),rnd);
    }

    @Test
    void getUrl() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        dsp.setUrl(rnd);
        Assert.assertEquals(dsp.getUrl(),rnd);
    }

    @Test
    void getJndiName() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        dsp.setJndiName(rnd);
        Assert.assertEquals(dsp.getJndiName(),rnd);
    }
}