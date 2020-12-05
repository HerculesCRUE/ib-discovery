package es.um.asio.service.model.relational;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.util.Utils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
class DiscoveryApplicationTest {

    DiscoveryApplication da;
    JobRegistry jr;

    @BeforeEach
    public void setUp() throws Exception {
        DataGenerator dg = new DataGenerator();
        jr = dg.getJobRegistry();
        da = jr.getDiscoveryApplication();
        da.getJobRegistries().add(jr);
        da.setStartDate(new Date());
        da.setPid(String.valueOf(Math.abs(new Random().nextInt())));
        for (int i = 1 ; i <= 5 ; i++) {
            // (DiscoveryApplication discoveryApplication, String node, String tripleStore, String className)
            CacheRegistry cr = new CacheRegistry(
                    da,
                    String.format("node_%s",i),
                    String.format("tripleStore_%s",i),
                    String.format("clase_%s",i)
            );
            cr.setId(i);
            da.getCacheRegistries().add(cr);
            // public ElasticRegistry(DiscoveryApplication discoveryApplication, String node, String tripleStore, String className,int inserted)
            ElasticRegistry er = new ElasticRegistry(
                    da,
                    String.format("node_%s",i),
                    String.format("tripleStore_%s",i),
                    String.format("clase_%s",i),
                    1
            );
            er.setId(i);
            da.getElasticRegistries().add(er);
        }
    }

    @Test
    void setId() {
        String id = RandomStringUtils.randomAlphabetic(10);
        da.setId(id);
        Assert.assertTrue(da.getId().equals(id));
    }

    @Test
    void setName() {
        String name = RandomStringUtils.randomAlphabetic(10);
        da.setName(name);
        Assert.assertTrue(da.getName().equals(name));
    }

    @Test
    void setStartDate() {
        Date d = new Date();
        da.setStartDate(d);
        Assert.assertTrue(da.getStartDate().equals(d));
    }

    @Test
    void setPid() {
        String pid = RandomStringUtils.randomAlphabetic(10);
        da.setPid(pid);
        Assert.assertTrue(da.getPid().equals(pid));
    }

    @Test
    void setCacheRegistries() {
        Set<CacheRegistry> cr = new HashSet<>(da.getCacheRegistries());
        da.setCacheRegistries(cr);
        Assert.assertTrue(da.getCacheRegistries().equals(cr));
    }

    @Test
    void setElasticRegistries() {
        Set<ElasticRegistry> er = new HashSet<>(da.getElasticRegistries());
        da.setElasticRegistries(er);
        Assert.assertTrue(da.getElasticRegistries().equals(er));
    }

    @Test
    void setJobRegistries() {
        Set<JobRegistry> jobRegistries = new HashSet<>();
        jobRegistries.add(jr);
        da.setJobRegistries(jobRegistries);
        Assert.assertTrue(da.getJobRegistries().equals(jobRegistries));
    }

    @Test
    void getId() {
        Assert.assertTrue(Utils.isValidString(da.getId()));
    }

    @Test
    void getName() {
        Assert.assertTrue(Utils.isValidString(da.getName()));
    }

    @Test
    void getStartDate() {
        Assert.assertNotNull(da.getStartDate());
    }

    @Test
    void getPid() {
        Assert.assertTrue(Utils.isValidString(da.getPid()));
    }

    @Test
    void getCacheRegistries() {
        Assert.assertNotNull(da.getCacheRegistries());
    }

    @Test
    void getElasticRegistries() {
        Assert.assertNotNull(da.getElasticRegistries());
    }

    @Test
    void getJobRegistries() {
        Assert.assertNotNull(da.getJobRegistries());
    }

    @Test
    void testEquals() {
        Assert.assertTrue(da.equals(da));
    }

    @Test
    void canEqual() {
        Assert.assertTrue(da.canEqual(da));
    }

    @Test
    void testHashCode() {
        Assert.assertTrue(da.hashCode() == da.hashCode());
    }
}