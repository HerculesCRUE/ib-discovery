package es.um.asio.service.model.relational;

import com.google.gson.JsonObject;
import data.DataGenerator;
import es.um.asio.service.model.TripleObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
class JobRegistryTest {

    JobRegistry jobRegistry;

    @BeforeEach
    public void setUp() throws Exception {
        DataGenerator dg = new DataGenerator();
        jobRegistry = dg.getJobRegistry();

    }

    @Test
    void getMaxRequestDate() {
        Date maxDate = new Date(0L);
        for(RequestRegistry rr : jobRegistry.getRequestRegistries()) {
            if (rr.getRequestDate().after(maxDate))
                maxDate = rr.getRequestDate();
        }
        Assert.assertTrue(maxDate.equals(jobRegistry.getMaxRequestDate()));
    }

    @Test
    void addRequestRegistry() {
        RequestRegistry rr = new RequestRegistry("usuario1","12345",RequestType.ENTITY_LINK_INSTANCE,new Date());
        jobRegistry.addRequestRegistry(rr);
        Assert.assertTrue(jobRegistry.getRequestRegistries().contains(rr));
    }

    @Test
    void getStarDateStr() {
        jobRegistry.setStartedDate(new Date());
        Assert.assertTrue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(jobRegistry.getStartedDate()).equals(jobRegistry.getStarDateStr()));
    }

    @Test
    void getCompletedDateStr() {
        jobRegistry.setCompletedDate(new Date());
        Assert.assertTrue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(jobRegistry.getCompletedDate()).equals(jobRegistry.getCompletedDateStr()));
    }

    @Test
    void toSimplifiedJson() {
        JsonObject jSimplifiedJson = jobRegistry.toSimplifiedJson();
        Assert.assertNotNull(jSimplifiedJson);
        Assert.assertTrue(jSimplifiedJson.has("node"));
        Assert.assertTrue(jSimplifiedJson.get("node").getAsString().equals(jobRegistry.getNode()));
        Assert.assertTrue(jSimplifiedJson.has("tripleStore"));
        Assert.assertTrue(jSimplifiedJson.get("tripleStore").getAsString().equals(jobRegistry.getTripleStore()));
        Assert.assertTrue(jSimplifiedJson.has("className"));
        Assert.assertTrue(jSimplifiedJson.get("className").getAsString().equals(jobRegistry.getClassName()));
        Assert.assertTrue(jSimplifiedJson.has("startDate"));
        // Assert.assertTrue(jSimplifiedJson.get("startDate").isJsonNull() || jSimplifiedJson.get("startDate").getAsString().equals(jobRegistry.getStartedDate()));
        Assert.assertTrue(jSimplifiedJson.has("endDate"));
        Assert.assertTrue(jSimplifiedJson.get("endDate").isJsonNull() || jSimplifiedJson.get("endDate").getAsString().equals(jobRegistry.getCompletedDate()));
        Assert.assertTrue(jSimplifiedJson.has("status"));
        Assert.assertTrue(jSimplifiedJson.get("status").getAsString().equals(jobRegistry.getStatusResult().toString()));
        Assert.assertTrue(jSimplifiedJson.has("results"));
        Assert.assertTrue(jSimplifiedJson.get("results").getAsJsonArray().size() == jobRegistry.getObjectResults().size());
    }

    @Test
    void getWebHooks() {
        Set<String> webHooks = new HashSet<>();
        for(RequestRegistry rr : jobRegistry.getRequestRegistries()) {
            if (rr.getWebHook()!=null)
                webHooks.add(rr.getWebHook());
        }
        Assert.assertTrue(webHooks.equals(jobRegistry.getWebHooks()));
    }

    @Test
    void isPropagatedInKafka() {
        boolean propagate = false;
        for(RequestRegistry rr : jobRegistry.getRequestRegistries()) {
            propagate = propagate ||rr.isPropagueInKafka();
        }
        Assert.assertTrue(propagate == jobRegistry.isPropagatedInKafka());
    }

    @Test
    void setId() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        jobRegistry.setId(rnd);
        Assert.assertTrue(jobRegistry.getId().equals(rnd));
    }

    @Test
    void setDiscoveryApplication() {
        DiscoveryApplication da = new DiscoveryApplication("app2");
        jobRegistry.setDiscoveryApplication(da);
        Assert.assertTrue(jobRegistry.getDiscoveryApplication().equals(da));
    }

    @Test
    void setNode() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        jobRegistry.setNode(rnd);
        Assert.assertTrue(jobRegistry.getNode().equals(rnd));
    }

    @Test
    void setTripleStore() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        jobRegistry.setTripleStore(rnd);
        Assert.assertTrue(jobRegistry.getTripleStore().equals(rnd));
    }

    @Test
    void setClassName() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        jobRegistry.setClassName(rnd);
        Assert.assertTrue(jobRegistry.getClassName().equals(rnd));
    }

    @Test
    void setRequestRegistries() {
        Set<RequestRegistry> requestRegistries = jobRegistry.getRequestRegistries();
        requestRegistries.remove(requestRegistries.toArray()[0]);
        jobRegistry.setRequestRegistries(requestRegistries);
        Assert.assertTrue(jobRegistry.getRequestRegistries().equals(requestRegistries));
    }

    @Test
    void setCompletedDate() {
        Date d = new Date();
        jobRegistry.setCompletedDate(d);
        Assert.assertTrue(jobRegistry.getCompletedDate().equals(d));
    }

    @Test
    void setStartedDate() {
        Date d = new Date();
        jobRegistry.setStartedDate(d);
        Assert.assertTrue(jobRegistry.getStartedDate().equals(d));
    }

    @Test
    void setStatusResult() {
        StatusResult sr = StatusResult.COMPLETED;
        jobRegistry.setStatusResult(sr);
        Assert.assertTrue(jobRegistry.getStatusResult().equals(sr));
    }

    @Test
    void setCompleted() {
        jobRegistry.setCompleted(true);
        Assert.assertTrue(jobRegistry.isCompleted());
    }

    @Test
    void setStarted() {
        jobRegistry.setStarted(true);
        Assert.assertTrue(jobRegistry.isStarted());
    }

    @Test
    void setDoSync() {
        jobRegistry.setDoSync(true);
        Assert.assertTrue(jobRegistry.isDoSync());
    }

    @Test
    void setSearchLinks() {
        jobRegistry.setSearchLinks(true);
        Assert.assertTrue(jobRegistry.isSearchLinks());
    }

    @Test
    void setSearchFromDelta() {
        Date d = new Date();
        jobRegistry.setSearchFromDelta(d);
        Assert.assertTrue(jobRegistry.getSearchFromDelta().equals(d));
    }

    @Test
    void setBodyRequest() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        jobRegistry.setBodyRequest(rnd);
        Assert.assertTrue(jobRegistry.getBodyRequest().equals(rnd));
    }

    @Test
    void setObjectResults() {
        Set<ObjectResult> objectResults = jobRegistry.getObjectResults();
        objectResults.remove(objectResults.toArray()[0]);
        jobRegistry.setObjectResults(objectResults);
        Assert.assertTrue(jobRegistry.getObjectResults().equals(objectResults));
    }

    @Test
    void setTripleObject() {
        TripleObject to = new TripleObject();
        jobRegistry.setTripleObject(to);
        Assert.assertTrue(jobRegistry.getTripleObject().equals(to));
    }

    @Test
    void getId() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        jobRegistry.setId(rnd);
        Assert.assertNotNull(jobRegistry.getId());
    }

    @Test
    void getDiscoveryApplication() {
        Assert.assertNotNull(jobRegistry.getDiscoveryApplication());
    }

    @Test
    void getNode() {
        Assert.assertNotNull(jobRegistry.getNode());
    }

    @Test
    void getTripleStore() {
        TripleObject to = new TripleObject();
        jobRegistry.setTripleObject(to);
        Assert.assertTrue(jobRegistry.getTripleObject().equals(to));
    }

    @Test
    void getClassName() {
        Assert.assertNotNull(jobRegistry.getClassName());
    }

    @Test
    void getRequestRegistries() {
        Assert.assertNotNull(jobRegistry.getRequestRegistries());
    }

    @Test
    void getCompletedDate() {
        Date d = new Date();
        jobRegistry.setCompletedDate(d);
        Assert.assertTrue(jobRegistry.getCompletedDate().equals(d));
    }

    @Test
    void getStartedDate() {
        Date d = new Date();
        jobRegistry.setStartedDate(d);
        Assert.assertTrue(jobRegistry.getStartedDate().equals(d));
    }

    @Test
    void getStatusResult() {
        StatusResult sr = StatusResult.COMPLETED;
        jobRegistry.setStatusResult(sr);
        Assert.assertTrue(jobRegistry.getStatusResult().equals(sr));
    }

    @Test
    void isCompleted() {
        jobRegistry.setCompleted(true);
        Assert.assertTrue(jobRegistry.isCompleted());
    }

    @Test
    void isStarted() {
        jobRegistry.setStarted(true);
        Assert.assertTrue(jobRegistry.isStarted());
    }

    @Test
    void isDoSync() {
        jobRegistry.setDoSync(true);
        Assert.assertTrue(jobRegistry.isDoSync());
    }

    @Test
    void isSearchLinks() {
        jobRegistry.setSearchLinks(true);
        Assert.assertTrue(jobRegistry.isSearchLinks());
    }

    @Test
    void getSearchFromDelta() {
        Date d = new Date();
        jobRegistry.setSearchFromDelta(d);
        Assert.assertTrue(jobRegistry.getSearchFromDelta().equals(d));
    }

    @Test
    void getBodyRequest() {
        String rnd = RandomStringUtils.randomAlphabetic(10);
        jobRegistry.setBodyRequest(rnd);
        Assert.assertTrue(jobRegistry.getBodyRequest().equals(rnd));
    }

    @Test
    void getObjectResults() {
        Assert.assertNotNull(jobRegistry.getObjectResults());
    }

    @Test
    void getTripleObject() {
        jobRegistry.setTripleObject(new TripleObject());
        Assert.assertNotNull(jobRegistry.getTripleObject());
    }

    @Test
    void testEquals() {
        Assert.assertTrue(jobRegistry.equals(jobRegistry));
    }

    @Test
    void canEqual() {
        Assert.assertTrue(jobRegistry.canEqual(jobRegistry));
    }

    @Test
    void testHashCode() {
        Assert.assertTrue(jobRegistry.hashCode() == jobRegistry.hashCode());
    }
}