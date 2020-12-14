package es.um.asio.service.config.properties;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

class PersistencePropertiesTest {

    PersistenceProperties pp;

    @BeforeEach
    void setUp() {
        pp = new PersistenceProperties();
        pp.setDatasource(new DatasourceProperties());
        pp.setJpa(new JpaProperties());
    }

    @Test
    void setDatasource() {
        DatasourceProperties dsp = new DatasourceProperties();
        pp.setDatasource(dsp);
        Assert.assertEquals(dsp,pp.getDatasource());
    }

    @Test
    void setJpa() {
        JpaProperties jpa = new JpaProperties();
        pp.setJpa(jpa);
        Assert.assertEquals(jpa,pp.getJpa());
    }

    @Test
    void getDatasource() {
        DatasourceProperties dsp = new DatasourceProperties();
        pp.setDatasource(dsp);
        Assert.assertEquals(dsp,pp.getDatasource());
    }

    @Test
    void getJpa() {
        JpaProperties jpa = new JpaProperties();
        pp.setJpa(jpa);
        Assert.assertEquals(jpa,pp.getJpa());
    }
}