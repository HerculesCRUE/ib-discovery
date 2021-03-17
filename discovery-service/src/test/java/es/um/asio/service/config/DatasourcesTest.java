package es.um.asio.service.config;

import es.um.asio.service.util.Utils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class DatasourcesTest {

    Datasources dsc;

    @BeforeEach
    void setUp() {
        dsc = new Datasources();
        List<Datasources.Node> nodes = new ArrayList<>();
        for (int i = 1 ; i <=5; i++) {
            Datasources.Node n = new Datasources.Node();
            n.setNodeName(String.format("node%s",i));
            List<Datasources.Node.TripleStore> tripleStores = new ArrayList<>();
            for (int j = 1 ; j <=2; j++) {
                Datasources.Node.TripleStore ts = new Datasources.Node.TripleStore(String.format("type_%s",j),String.format("baseURL_%s",j),String.format("name_%s",j),String.format("user_%s",j),String.format("password_%s",j),"");
                tripleStores.add(ts);
            }
            n.setTripleStores(tripleStores);
            nodes.add(n);
        }
        dsc.setNodes(nodes);
    }

    @Test
    void getNodeByName() {
        Assert.assertNotNull(dsc.getNodeByName("node1"));
    }

    @Test
    void setUseCachedData() {
        boolean rnd = new Random().nextBoolean();
        dsc.setUseCachedData(rnd);
        Assert.assertEquals(rnd,dsc.isUseCachedData());
    }

    @Test
    void setThresholds() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        dsc.setThresholds(t);
        Assert.assertEquals(t,dsc.getThresholds());
    }

    @Test
    void setThresholdsManualThreshold() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setManualThreshold(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getManualThreshold());
    }

    @Test
    void getThresholdsManualThreshold() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setManualThreshold(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getManualThreshold());
    }

    @Test
    void setThresholdsAutomaticThreshold() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setAutomaticThreshold(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getAutomaticThreshold());
    }

    @Test
    void getThresholdsAutomaticThreshold() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setAutomaticThreshold(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getAutomaticThreshold());
    }

    @Test
    void setThresholdsElasticSearchAttributesThresholdSimpleThreshold() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesThresholdSimple(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesThresholdSimple());
    }

    @Test
    void getThresholdsElasticSearchAttributesThresholdSimpleThreshold() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesThresholdSimple(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesThresholdSimple());
    }

    @Test
    void setThresholdsElasticSearchAttributesNumberRatioSimpleThreshold() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesNumberRatioSimple(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesNumberRatioSimple());
    }

    @Test
    void getThresholdsElasticSearchAttributesNumberRatioSimpleThreshold() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesNumberRatioSimple(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesNumberRatioSimple());
    }

    @Test
    void setThresholdsElasticSearchAttributesThresholdComplexThreshold() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesThresholdComplex(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesThresholdComplex());
    }

    @Test
    void getThresholdsElasticSearchAttributesThresholdComplexThreshold() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesThresholdComplex(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesThresholdComplex());
    }

    @Test
    void setThresholdsElasticSearchAttributesNumberRatioComplex() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesNumberRatioComplex(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesNumberRatioComplex());
    }

    @Test
    void getThresholdsElasticSearchAttributesNumberRatioComplex() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesNumberRatioComplex(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesNumberRatioComplex());
    }

    @Test
    void setThresholdsElasticSearchMaxDesirableNumbersOfResults() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchMaxDesirableNumbersOfResults(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchMaxDesirableNumbersOfResults());
    }

    @Test
    void getThresholdsElasticSearchMaxDesirableNumbersOfResults() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchMaxDesirableNumbersOfResults(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchMaxDesirableNumbersOfResults());
    }

    @Test
    void setThresholdsElasticSearchCutOffAccordPercentile() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchCutOffAccordPercentile(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchCutOffAccordPercentile());
    }

    @Test
    void getThresholdsElasticSearchCutOffAccordPercentile() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchCutOffAccordPercentile(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchCutOffAccordPercentile());
    }

    @Test
    void setNodes() {
        List<Datasources.Node> nodes = dsc.getNodes();
        dsc.setNodes(nodes);
        Assert.assertEquals(nodes,dsc.getNodes());
    }

    @Test
    void setNodeName() {
        for (Datasources.Node n : dsc.getNodes()) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            n.setNodeName(rnd);
            Assert.assertEquals(rnd,n.getNodeName());
        }
    }

    @Test
    void getNodeName() {
        for (Datasources.Node n : dsc.getNodes()) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            n.setNodeName(rnd);
            Assert.assertEquals(rnd,n.getNodeName());
        }
    }

    @Test
    void getTripleStoreByTypeNode() {
        for (Datasources.Node n : dsc.getNodes()) {
            for (Datasources.Node.TripleStore ts: n.getTripleStores()) {
                String type = ts.getType();
                Assert.assertEquals(ts, n.getTripleStoreByType(type));
            }
        }
    }

    @Test
    void setTripleStoreType() {
        for (Datasources.Node n : dsc.getNodes()) {
            for (Datasources.Node.TripleStore ts: n.getTripleStores()) {
                String rnd = RandomStringUtils.randomAlphabetic(10);
                ts.setType(rnd);
                Assert.assertEquals(rnd, ts.getType());
            }
        }
    }

    @Test
    void getTripleStoreType() {
        for (Datasources.Node n : dsc.getNodes()) {
            for (Datasources.Node.TripleStore ts: n.getTripleStores()) {
                Assert.assertTrue(Utils.isValidString(ts.getType()));
            }
        }
    }

    @Test
    void setTripleStoreBaseURL() {
        for (Datasources.Node n : dsc.getNodes()) {
            for (Datasources.Node.TripleStore ts: n.getTripleStores()) {
                String rnd = RandomStringUtils.randomAlphabetic(10);
                ts.setBaseURL(rnd);
                Assert.assertEquals(rnd, ts.getBaseURL());
            }
        }
    }

    @Test
    void getTripleStoreBaseURL() {
        for (Datasources.Node n : dsc.getNodes()) {
            for (Datasources.Node.TripleStore ts: n.getTripleStores()) {
                Assert.assertTrue(Utils.isValidString(ts.getBaseURL()));
            }
        }
    }

    @Test
    void setTripleStoreUser() {
        for (Datasources.Node n : dsc.getNodes()) {
            for (Datasources.Node.TripleStore ts: n.getTripleStores()) {
                String rnd = RandomStringUtils.randomAlphabetic(10);
                ts.setUser(rnd);
                Assert.assertEquals(rnd, ts.getUser());
            }
        }
    }

    @Test
    void getTripleStoreUser() {
        for (Datasources.Node n : dsc.getNodes()) {
            for (Datasources.Node.TripleStore ts: n.getTripleStores()) {
                Assert.assertTrue(Utils.isValidString(ts.getUser()));
            }
        }
    }

    @Test
    void setTripleStorePassword() {
        for (Datasources.Node n : dsc.getNodes()) {
            for (Datasources.Node.TripleStore ts: n.getTripleStores()) {
                String rnd = RandomStringUtils.randomAlphabetic(10);
                ts.setPassword(rnd);
                Assert.assertEquals(rnd, ts.getPassword());
            }
        }
    }

    @Test
    void getTripleStorePassword() {
        for (Datasources.Node n : dsc.getNodes()) {
            for (Datasources.Node.TripleStore ts: n.getTripleStores()) {
                Assert.assertTrue(Utils.isValidString(ts.getPassword()));
            }
        }
    }

    @Test
    void isUseCachedData() {
        boolean rnd = new Random().nextBoolean();
        dsc.setUseCachedData(rnd);
        Assert.assertEquals(rnd,dsc.isUseCachedData());
    }

    @Test
    void getThresholds() {
        Datasources.Thresholds t = new Datasources.Thresholds();
        dsc.setThresholds(t);
        Assert.assertEquals(t, dsc.getThresholds());
    }

    @Test
    void getNodes() {
        Assert.assertNotNull(dsc.getNodes());
    }
}