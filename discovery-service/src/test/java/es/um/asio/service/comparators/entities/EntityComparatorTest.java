package es.um.asio.service.comparators.entities;

import com.google.api.client.json.Json;
import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.relational.CacheRegistry;
import es.um.asio.service.model.stats.StatsHandler;
import es.um.asio.service.service.impl.CacheServiceImp;
import es.um.asio.service.service.impl.RedisServiceImp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
class EntityComparatorTest {

    Map<TripleObject, Map<String,Float>> tos = new HashMap<>();
    DataGenerator dg;
    CacheServiceImp cache;

    @BeforeEach
    public void setUp() throws Exception {
        dg = new DataGenerator();
        cache = dg.getCacheServiceImp();


        for (TripleObject to : dg.getTripleObjects()) {
            cache.addTripleObject(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to);
        }
        cache.setStatsHandler(new StatsHandler());
        cache.generateEntityStats();
        for (TripleObject to : dg.getTripleObjects()) {
            Map<String,Float> stats =  cache.getStatsHandler().generateMoreRelevantAttributesMap(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to.getClassName());
            tos.put(to,stats);
        }
        cache.setStatsHandler(new StatsHandler());
        cache.generateEntityStats();
        System.out.println();
    }

    @Test
    void compare() {
        for (Map.Entry<TripleObject, Map<String, Float>> toEntry : tos.entrySet()) {
            for (Map.Entry<TripleObject, Map<String, Float>> toEntryInner : tos.entrySet()) {
                EntitySimilarityObj eso = EntityComparator.compare(toEntry.getKey(),toEntryInner.getKey(),toEntry.getValue());
                Assert.assertTrue(eso.getSimilarity() == 1f);
            }
        }
    }

}