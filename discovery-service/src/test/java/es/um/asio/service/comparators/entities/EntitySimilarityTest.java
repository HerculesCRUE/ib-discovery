package es.um.asio.service.comparators.entities;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.relational.Attribute;
import es.um.asio.service.model.stats.AttributeStats;
import es.um.asio.service.service.impl.CacheServiceImp;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class EntitySimilarityTest {

    TripleObject to1,to2;
    Map<String,Float> stats;
    Map<String, AttributeStats> attributeStatsMap;


    @Autowired
    CacheServiceImp cache;

    @PostConstruct
    public void init() throws Exception {
        DataGenerator dg = new DataGenerator();
        // TripleObject(String node, String tripleStore, String className, JSONObject jData )
        JSONObject jData1 = new JSONObject("{\"attrs\":{\"k1\":\"v1\",\"k2\":\"v2\",\"k3\":\"v3\"}}");
        JSONObject jData2 = new JSONObject("{\"attrs\":{\"k1\":\"vn1\",\"k2\":\"vn2\",\"k3\":\"vn3\"}}");
        to1 = new TripleObject("n1","ts1","test",jData1);
        to2 = new TripleObject("n1","ts1","test",jData2);
        cache.addTripleObject(to1.getTripleStore().getNode().getNode(), to1.getTripleStore().getTripleStore(), to1);
        cache.addTripleObject(to2.getTripleStore().getNode().getNode(), to2.getTripleStore().getTripleStore(), to2);
        cache.generateEntityStats();
        stats = cache.getStatsHandler().generateMoreRelevantAttributesMap("n1","ts1","test");
        attributeStatsMap = cache.getStatsHandler().getAttributesMap("n1","ts1","test").getAttValues();
    }

    @Test
    void compare() {
        EntitySimilarityObj eso1 = EntitySimilarity.compare(to1,attributeStatsMap,to1.getAttributes(),to1.getAttributes());
        EntitySimilarityObj eso2 = EntitySimilarity.compare(to1,attributeStatsMap,to2.getAttributes(),to2.getAttributes());
        EntitySimilarityObj eso3 = EntitySimilarity.compare(to1,attributeStatsMap,to1.getAttributes(),to2.getAttributes());
        Assert.assertTrue(eso1.getSimilarity() == 1f);
        Assert.assertTrue(eso2.getSimilarity() == 1f);
        Assert.assertFalse(eso3.getSimilarity() == 1f);
    }

    @Test
    void compareAtt() {
        float s1 = EntitySimilarity.compareAtt(to1,attributeStatsMap,"attrs",to1.getAttributes(),to1.getAttributes());
        float s2 = EntitySimilarity.compareAtt(to1,attributeStatsMap,"attrs",to2.getAttributes(),to2.getAttributes());
        float s3 = EntitySimilarity.compareAtt(to1,attributeStatsMap,"attrs",to1.getAttributes(),to2.getAttributes());
        Assert.assertTrue(s1 == 1f);
        Assert.assertTrue(s2 == 1f);
        Assert.assertFalse(s3 == 1f);
    }

    @Test
    void compareNumberAtt() {
        Assert.assertTrue(EntitySimilarity.compareNumberAtt(.5f,1f,1f) == 1f);
        Assert.assertFalse(EntitySimilarity.compareNumberAtt(.5f,.8f,1f) == 1f);
        Assert.assertFalse(EntitySimilarity.compareNumberAtt(1f,.9999f,1f) == 1f);
    }

    @Test
    void testCompareNumberAtt() {
        Assert.assertTrue(EntitySimilarity.compareNumberAtt(true,true) == 1f);
        Assert.assertTrue(EntitySimilarity.compareNumberAtt(false,false) == 1f);
        Assert.assertTrue(EntitySimilarity.compareNumberAtt(true,false) == 0f);
        Assert.assertTrue(EntitySimilarity.compareNumberAtt(false,true) == 0f);
    }

    @Test
    void testCompareNumberAtt1() {
        String rnd1 = RandomStringUtils.randomAlphabetic(10);
        String rnd2;
        do {
            rnd2 = RandomStringUtils.randomAlphabetic(10);
        } while (rnd1.equals(rnd2));
        Assert.assertTrue(EntitySimilarity.compareNumberAtt(rnd1,rnd1) == 1f);
        Assert.assertFalse(EntitySimilarity.compareNumberAtt(rnd1,rnd2) == 1f);
    }

    @Test
    void isNumber() {
        Assert.assertTrue(EntitySimilarity.isNumber(1));
        Assert.assertTrue(EntitySimilarity.isNumber(1l));
        Assert.assertTrue(EntitySimilarity.isNumber(1f));
        Assert.assertTrue(EntitySimilarity.isNumber(1d));
        Assert.assertTrue(EntitySimilarity.isNumber("1"));
        Assert.assertFalse(EntitySimilarity.isNumber("test"));
        Assert.assertFalse(EntitySimilarity.isNumber(true));
        Assert.assertFalse(EntitySimilarity.isNumber(new HashMap<>()));
    }

    @Test
    void isBoolean() {
        Assert.assertTrue(EntitySimilarity.isBoolean(true));
        Assert.assertTrue(EntitySimilarity.isBoolean(false));
        Assert.assertTrue(EntitySimilarity.isBoolean("true"));
        Assert.assertTrue(EntitySimilarity.isBoolean("false"));
        Assert.assertTrue(EntitySimilarity.isBoolean("SI"));
        Assert.assertTrue(EntitySimilarity.isBoolean("NO"));
        Assert.assertFalse(EntitySimilarity.isBoolean(1));
        Assert.assertFalse(EntitySimilarity.isBoolean(1l));
        Assert.assertFalse(EntitySimilarity.isBoolean(1f));
        Assert.assertFalse(EntitySimilarity.isBoolean(1d));
        Assert.assertFalse(EntitySimilarity.isBoolean("1"));
        Assert.assertFalse(EntitySimilarity.isBoolean("test"));
        Assert.assertFalse(EntitySimilarity.isBoolean(new HashMap<>()));
    }

    @Test
    void isObject() {
        Assert.assertTrue(EntitySimilarity.isObject(new HashMap<>()));
        Assert.assertFalse(EntitySimilarity.isObject(true));
        Assert.assertFalse(EntitySimilarity.isObject(false));
        Assert.assertFalse(EntitySimilarity.isObject("true"));
        Assert.assertFalse(EntitySimilarity.isObject("false"));
        Assert.assertFalse(EntitySimilarity.isObject("SI"));
        Assert.assertFalse(EntitySimilarity.isObject("NO"));
        Assert.assertFalse(EntitySimilarity.isObject(1));
        Assert.assertFalse(EntitySimilarity.isObject(1l));
        Assert.assertFalse(EntitySimilarity.isObject(1f));
        Assert.assertFalse(EntitySimilarity.isObject(1d));
        Assert.assertFalse(EntitySimilarity.isObject("1"));
        Assert.assertFalse(EntitySimilarity.isObject("test"));
        Assert.assertFalse(EntitySimilarity.isObject(true));
    }

    @Test
    void isArrayList() {
        Assert.assertTrue(EntitySimilarity.isArrayList(new ArrayList<>()));
        Assert.assertFalse(EntitySimilarity.isArrayList(new HashMap<>()));
        Assert.assertFalse(EntitySimilarity.isArrayList(true));
        Assert.assertFalse(EntitySimilarity.isArrayList(false));
        Assert.assertFalse(EntitySimilarity.isArrayList("true"));
        Assert.assertFalse(EntitySimilarity.isArrayList("false"));
        Assert.assertFalse(EntitySimilarity.isArrayList("SI"));
        Assert.assertFalse(EntitySimilarity.isArrayList("NO"));
        Assert.assertFalse(EntitySimilarity.isArrayList(1));
        Assert.assertFalse(EntitySimilarity.isArrayList(1l));
        Assert.assertFalse(EntitySimilarity.isArrayList(1f));
        Assert.assertFalse(EntitySimilarity.isArrayList(1d));
        Assert.assertFalse(EntitySimilarity.isArrayList("1"));
        Assert.assertFalse(EntitySimilarity.isArrayList("test"));
        Assert.assertFalse(EntitySimilarity.isArrayList(true));
        Assert.assertFalse(EntitySimilarity.isArrayList(new HashMap<>()));
    }

    @Test
    void compareLists() throws Exception {
        JSONObject jData1 = new JSONObject("{\"k1\": [1,2,3,4,5]}");
        JSONObject jData2 = new JSONObject("{\"k1\": [2,3,4,5,6]}");
        TripleObject to1 = new TripleObject("n1","ts1","test2",jData1);
        TripleObject to2 = new TripleObject("n1","ts1","test2",jData2);

        cache.addTripleObject(to1.getTripleStore().getNode().getNode(), to1.getTripleStore().getTripleStore(), to1);
        cache.addTripleObject(to2.getTripleStore().getNode().getNode(), to2.getTripleStore().getTripleStore(), to2);
        cache.generateEntityStats();

        Map<String,Float> stats = cache.getStatsHandler().generateMoreRelevantAttributesMap("n1","ts1","test");
        Map<String, AttributeStats> attributeStatsMap = cache.getStatsHandler().getAttributesMap("n1","ts1","test").getAttValues();

        List<Float> s1 = EntitySimilarity.compareLists(to1,attributeStatsMap,new ArrayList((ArrayList) to1.getAttributes().get("k1")),new ArrayList((ArrayList) to1.getAttributes().get("k1")));
        Assert.assertTrue(s1.size() == new ArrayList((ArrayList) to1.getAttributes().get("k1")).size());
        Assert.assertTrue( (s1.stream().mapToDouble(a->a).sum()) / (new ArrayList((ArrayList) to1.getAttributes().get("k1")).size()) == 1f);
    }
}