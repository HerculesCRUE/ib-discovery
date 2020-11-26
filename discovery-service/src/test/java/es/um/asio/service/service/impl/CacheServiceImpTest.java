package es.um.asio.service.service.impl;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import org.checkerframework.checker.units.qual.C;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={main.TestApplication.class})
class CacheServiceImpTest {

    @Autowired
    CacheServiceImp cacheService;

    @Test
    void addTripleObject() throws Exception {

        JSONObject jData = new JSONObject("{\"att1\":\"val1\",\"att2\":\"val2\",\"att3\":\"val3\"}");
        TripleObject to = new TripleObject("test","trellis","test",jData);
        to.setId("1");
        cacheService.addTripleObject("test","trellis",to);
        Assert.assertTrue(cacheService.getTriplesMap().containsKey("test"));
        Assert.assertTrue(cacheService.getTriplesMap().get("test").containsKey("trellis"));
        Assert.assertTrue(cacheService.getTriplesMap().get("test").get("trellis").containsKey("test"));
        Assert.assertTrue(cacheService.getTriplesMap().get("test").get("trellis").get("test").containsKey("1"));
        cacheService.getTriplesMap().get("test");
        TripleObject cachedTo = cacheService.getTriplesMap().get("test").get("trellis").get("test").get("1");
        Assert.assertTrue(to.equals(cachedTo));
        cacheService.removeTripleObject("test","trellis",to);
    }

    @Test
    void addTripleObjectES() throws Exception {
        JSONObject jData = new JSONObject("{\"att1\":\"val1\",\"att2\":\"val2\",\"att3\":\"val3\"}");
        TripleObject to = new TripleObject("test","trellis","test",jData);
        to.setId("1");
        TripleObjectES toES = new TripleObjectES(to);
        cacheService.addTripleObjectES("test","trellis",toES);
        Assert.assertTrue(cacheService.getTriplesMap().containsKey("test"));
        Assert.assertTrue(cacheService.getTriplesMap().get("test").containsKey("trellis"));
        Assert.assertTrue(cacheService.getTriplesMap().get("test").get("trellis").containsKey("test"));
        Assert.assertTrue(cacheService.getTriplesMap().get("test").get("trellis").get("test").containsKey("1"));
        cacheService.getTriplesMap().get("test");
        TripleObject cachedTo = cacheService.getTriplesMap().get("test").get("trellis").get("test").get("1");
        TripleObjectES cachedToEs = new TripleObjectES(cachedTo);
        Assert.assertTrue(toES.equals(cachedToEs));
        cacheService.removeTripleObject("test","trellis",to);
    }

    @Test
    void removeTripleObject() throws Exception {
        JSONObject jData = new JSONObject("{\"att1\":\"val1\",\"att2\":\"val2\",\"att3\":\"val3\"}");
        TripleObject to = new TripleObject("test","trellis","test",jData);
        to.setId("1");
        cacheService.addTripleObject("test","trellis",to);
        Assert.assertTrue(cacheService.getTriplesMap().containsKey("test"));
        Assert.assertTrue(cacheService.getTriplesMap().get("test").containsKey("trellis"));
        Assert.assertTrue(cacheService.getTriplesMap().get("test").get("trellis").containsKey("test"));
        Assert.assertTrue(cacheService.getTriplesMap().get("test").get("trellis").get("test").containsKey("1"));
        cacheService.getTriplesMap().get("test");
        TripleObject cachedTo = cacheService.getTriplesMap().get("test").get("trellis").get("test").get("1");
        Assert.assertTrue(to.equals(cachedTo));
        cacheService.removeTripleObject("test","trellis",to);
        Assert.assertNull(cacheService.getTriplesMap().get("test").get("trellis").get("test").get("1"));
    }

    @Test
    void saveInCache() {
    }

    @Test
    void saveTriplesMapInCache() {
    }

    @Test
    void testSaveTriplesMapInCache() {
    }

    @Test
    void getTipleMapByNodeAndTripleStoreAndClassName() {
    }

    @Test
    void saveFilterMapInCache() {
    }

    @Test
    void saveEntityStatsInCache() {
    }

    @Test
    void saveElasticSearchTriplesMapInCache() {
    }

    @Test
    void loadTiplesMapFromCache() {
    }

    @Test
    void loadFilteredMapFromCache() {
    }

    @Test
    void loadEntitiesStatsFromCache() {
    }

    @Test
    void loadElasticSearchTiplesMapFromCache() {
    }

    @Test
    void isPopulatedCache() {
    }

    @Test
    void generateEntityStats() {
    }

    @Test
    void getFilteredIterator() {
    }

    @Test
    void getAllTripleObjects() {
    }

    @Test
    void getTripleObjects() {
    }

    @Test
    void getTripleObject() {
    }

    @Test
    void getTriplesMap() {
    }

    @Test
    void setTriplesMap() {
    }

    @Test
    void setEsTriplesMap() {
    }

    @Test
    void getEsTriplesMapAsSet() {
    }

    @Test
    void getFiltered() {
    }

    @Test
    void setFiltered() {
    }

    @Test
    void getStatsHandler() {
    }

    @Test
    void setStatsHandler() {
    }

    @Test
    void updateStats() {
    }
}