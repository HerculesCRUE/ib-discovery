package es.um.asio.service.config;

import es.um.asio.service.util.Utils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class DataSourcesTest {

    DataSources dsc;

    @BeforeEach
    void setUp() {
        dsc = new DataSources();
        List<DataSources.Node> nodes = new ArrayList<>();
        for (int i = 1 ; i <=5; i++) {
            DataSources.Node n = new DataSources.Node();
            n.setNodeName(String.format("node%s",i));
            List<DataSources.Node.TripleStore> tripleStores = new ArrayList<>();
            for (int j = 1 ; j <=2; j++) {
                DataSources.Node.TripleStore ts = new DataSources.Node.TripleStore(String.format("type_%s",j),String.format("baseURL_%s",j),String.format("name_%s",j),String.format("user_%s",j),String.format("password_%s",j),"");
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
        DataSources.Thresholds t = new DataSources.Thresholds();
        dsc.setThresholds(t);
        Assert.assertEquals(t,dsc.getThresholds());
    }

    @Test
    void setThresholdsManualThreshold() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setManualThreshold(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getManualThreshold());
    }

    @Test
    void getThresholdsManualThreshold() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setManualThreshold(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getManualThreshold());
    }

    @Test
    void setThresholdsAutomaticThreshold() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setAutomaticThreshold(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getAutomaticThreshold());
    }

    @Test
    void getThresholdsAutomaticThreshold() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setAutomaticThreshold(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getAutomaticThreshold());
    }

    @Test
    void setThresholdsElasticSearchAttributesThresholdSimpleThreshold() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesThresholdSimple(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesThresholdSimple());
    }

    @Test
    void getThresholdsElasticSearchAttributesThresholdSimpleThreshold() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesThresholdSimple(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesThresholdSimple());
    }

    @Test
    void setThresholdsElasticSearchAttributesNumberRatioSimpleThreshold() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesNumberRatioSimple(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesNumberRatioSimple());
    }

    @Test
    void getThresholdsElasticSearchAttributesNumberRatioSimpleThreshold() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesNumberRatioSimple(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesNumberRatioSimple());
    }

    @Test
    void setThresholdsElasticSearchAttributesThresholdComplexThreshold() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesThresholdComplex(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesThresholdComplex());
    }

    @Test
    void getThresholdsElasticSearchAttributesThresholdComplexThreshold() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesThresholdComplex(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesThresholdComplex());
    }

    @Test
    void setThresholdsElasticSearchAttributesNumberRatioComplex() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesNumberRatioComplex(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesNumberRatioComplex());
    }

    @Test
    void getThresholdsElasticSearchAttributesNumberRatioComplex() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchAttributesNumberRatioComplex(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchAttributesNumberRatioComplex());
    }

    @Test
    void setThresholdsElasticSearchMaxDesirableNumbersOfResults() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchMaxDesirableNumbersOfResults(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchMaxDesirableNumbersOfResults());
    }

    @Test
    void getThresholdsElasticSearchMaxDesirableNumbersOfResults() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchMaxDesirableNumbersOfResults(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchMaxDesirableNumbersOfResults());
    }

    @Test
    void setThresholdsElasticSearchCutOffAccordPercentile() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchCutOffAccordPercentile(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchCutOffAccordPercentile());
    }

    @Test
    void getThresholdsElasticSearchCutOffAccordPercentile() {
        DataSources.Thresholds t = new DataSources.Thresholds();
        double mt = new Random().nextDouble();
        t.setElasticSearchCutOffAccordPercentile(mt);
        dsc.setThresholds(t);
        Assert.assertTrue(mt == dsc.getThresholds().getElasticSearchCutOffAccordPercentile());
    }

    @Test
    void setNodes() {
        List<DataSources.Node> nodes = dsc.getNodes();
        dsc.setNodes(nodes);
        Assert.assertEquals(nodes,dsc.getNodes());
    }

    @Test
    void setNodeName() {
        for (DataSources.Node n : dsc.getNodes()) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            n.setNodeName(rnd);
            Assert.assertEquals(rnd,n.getNodeName());
        }
    }

    @Test
    void getNodeName() {
        for (DataSources.Node n : dsc.getNodes()) {
            String rnd = RandomStringUtils.randomAlphabetic(10);
            n.setNodeName(rnd);
            Assert.assertEquals(rnd,n.getNodeName());
        }
    }

    @Test
    void getTripleStoreByTypeNode() {
        for (DataSources.Node n : dsc.getNodes()) {
            for (DataSources.Node.TripleStore ts: n.getTripleStores()) {
                String type = ts.getType();
                Assert.assertEquals(ts, n.getTripleStoreByType(type));
            }
        }
    }

    @Test
    void setTripleStoreType() {
        for (DataSources.Node n : dsc.getNodes()) {
            for (DataSources.Node.TripleStore ts: n.getTripleStores()) {
                String rnd = RandomStringUtils.randomAlphabetic(10);
                ts.setType(rnd);
                Assert.assertEquals(rnd, ts.getType());
            }
        }
    }

    @Test
    void getTripleStoreType() {
        for (DataSources.Node n : dsc.getNodes()) {
            for (DataSources.Node.TripleStore ts: n.getTripleStores()) {
                Assert.assertTrue(Utils.isValidString(ts.getType()));
            }
        }
    }

    @Test
    void setTripleStoreBaseURL() {
        for (DataSources.Node n : dsc.getNodes()) {
            for (DataSources.Node.TripleStore ts: n.getTripleStores()) {
                String rnd = RandomStringUtils.randomAlphabetic(10);
                ts.setBaseURL(rnd);
                Assert.assertEquals(rnd, ts.getBaseURL());
            }
        }
    }

    @Test
    void getTripleStoreBaseURL() {
        for (DataSources.Node n : dsc.getNodes()) {
            for (DataSources.Node.TripleStore ts: n.getTripleStores()) {
                Assert.assertTrue(Utils.isValidString(ts.getBaseURL()));
            }
        }
    }

    @Test
    void setTripleStoreUser() {
        for (DataSources.Node n : dsc.getNodes()) {
            for (DataSources.Node.TripleStore ts: n.getTripleStores()) {
                String rnd = RandomStringUtils.randomAlphabetic(10);
                ts.setUser(rnd);
                Assert.assertEquals(rnd, ts.getUser());
            }
        }
    }

    @Test
    void getTripleStoreUser() {
        for (DataSources.Node n : dsc.getNodes()) {
            for (DataSources.Node.TripleStore ts: n.getTripleStores()) {
                Assert.assertTrue(Utils.isValidString(ts.getUser()));
            }
        }
    }

    @Test
    void setTripleStorePassword() {
        for (DataSources.Node n : dsc.getNodes()) {
            for (DataSources.Node.TripleStore ts: n.getTripleStores()) {
                String rnd = RandomStringUtils.randomAlphabetic(10);
                ts.setPassword(rnd);
                Assert.assertEquals(rnd, ts.getPassword());
            }
        }
    }

    @Test
    void getTripleStorePassword() {
        for (DataSources.Node n : dsc.getNodes()) {
            for (DataSources.Node.TripleStore ts: n.getTripleStores()) {
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
        DataSources.Thresholds t = new DataSources.Thresholds();
        dsc.setThresholds(t);
        Assert.assertEquals(t, dsc.getThresholds());
    }

    @Test
    void getNodes() {
        Assert.assertNotNull(dsc.getNodes());
    }
}