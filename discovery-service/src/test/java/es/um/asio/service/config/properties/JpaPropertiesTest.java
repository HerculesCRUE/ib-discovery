package es.um.asio.service.config.properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class JpaPropertiesTest {

    JpaProperties jpap;

    @BeforeEach
    void setUp() {
        jpap = new JpaProperties();
        jpap.setDialect("org.hibernate.dialect.MariaDB53Dialect");
        jpap.setGenerateDdl(true);
        jpap.setShowSql(true);
        jpap.setFormatSql(true);
        jpap.setUseSqlComments(true);
        Map<String,String> prop = new HashMap<>();
        prop.put("hibernate.temp.use_jdbc_metadata_defaults","true");
        jpap.setProperties(prop);
    }

    @Test
    void setProperties() {
        Map<String,String> prop = new HashMap<>();
        prop.put("hibernate.temp.use_jdbc_metadata_defaults","true");
        jpap.setProperties(prop);
        Assert.assertEquals(prop,jpap.getProperties());
    }

    @Test
    void setGenerateDdl() {
        boolean rnd = new Random().nextBoolean();
        jpap.setGenerateDdl(rnd);
        Assert.assertEquals(rnd,jpap.isGenerateDdl());
    }

    @Test
    void setShowSql() {
        boolean rnd = new Random().nextBoolean();
        jpap.setShowSql(rnd);
        Assert.assertEquals(rnd,jpap.isShowSql());
    }

    @Test
    void setFormatSql() {
        boolean rnd = new Random().nextBoolean();
        jpap.setFormatSql(rnd);
        Assert.assertEquals(rnd,jpap.isFormatSql());
    }

    @Test
    void setUseSqlComments() {
        boolean rnd = new Random().nextBoolean();
        jpap.setUseSqlComments(rnd);
        Assert.assertEquals(rnd,jpap.isUseSqlComments());
    }

    @Test
    void setDialect() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        jpap.setDialect(rnd);
        Assert.assertEquals(rnd,jpap.getDialect());
    }

    @Test
    void getProperties() {
        Map<String,String> prop = new HashMap<>();
        prop.put("hibernate.temp.use_jdbc_metadata_defaults","true");
        jpap.setProperties(prop);
        Assert.assertEquals(prop,jpap.getProperties());
    }

    @Test
    void isGenerateDdl() {
        boolean rnd = new Random().nextBoolean();
        jpap.setGenerateDdl(rnd);
        Assert.assertEquals(rnd,jpap.isGenerateDdl());
    }

    @Test
    void isShowSql() {
        boolean rnd = new Random().nextBoolean();
        jpap.setShowSql(rnd);
        Assert.assertEquals(rnd,jpap.isShowSql());
    }

    @Test
    void isFormatSql() {
        boolean rnd = new Random().nextBoolean();
        jpap.setFormatSql(rnd);
        Assert.assertEquals(rnd,jpap.isFormatSql());
    }

    @Test
    void isUseSqlComments() {
        boolean rnd = new Random().nextBoolean();
        jpap.setUseSqlComments(rnd);
        Assert.assertEquals(rnd,jpap.isUseSqlComments());
    }

    @Test
    void getDialect() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        jpap.setDialect(rnd);
        Assert.assertEquals(rnd,jpap.getDialect());
    }
}