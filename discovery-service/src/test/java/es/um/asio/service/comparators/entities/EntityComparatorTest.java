package es.um.asio.service.comparators.entities;

import data.DataGenerator;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.StatsHandler;
import es.um.asio.service.service.impl.CacheServiceImp;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

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
            cache.addTripleObject(to.getTripleStore().getNode().getNodeName(),to.getTripleStore().getName(),to);
        }
        cache.setStatsHandler(new StatsHandler());
        cache.generateEntityStats();
        for (TripleObject to : dg.getTripleObjects()) {
            Map<String,Float> stats =  cache.getStatsHandler().generateMoreRelevantAttributesMap(to.getTripleStore().getNode().getNodeName(),to.getTripleStore().getName(),to.getClassName(),null);
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