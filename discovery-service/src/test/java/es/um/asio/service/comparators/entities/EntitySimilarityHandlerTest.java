package es.um.asio.service.comparators.entities;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.AttributeStats;
import es.um.asio.service.service.impl.CacheServiceImp;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
class EntitySimilarityHandlerTest {

    List<TripleObject> tos;

    DataGenerator dg;
    CacheServiceImp cache;

    @BeforeEach
    public void init() throws Exception {
        dg = new DataGenerator();
        tos = dg.getTripleObjects();
        cache = dg.getCacheServiceImp();
        cache.initialize();
        for (TripleObject to : tos) {
            cache.addTripleObject(to.getTripleStore().getNode().getNode(), to.getTripleStore().getTripleStore(), to);
        }
        cache.generateEntityStats();
    }

    @Test
    void calculateSimilarityInEntities() {
        for (TripleObject to : tos) {
            Map<String, AttributeStats> attributeStatsMap = cache.getStatsHandler().getAttributesMap(to.getTripleStore().getNode().getNode(),to.getTripleStore().getTripleStore(),to.getClassName()).getAttValues();
            Map<String, List<EntitySimilarityObj>> esos = EntitySimilarityHandler.calculateSimilarityInEntities(
                    cache,
                    to,
                    tos,
                    0.6f,
                    0.9f
            );
            Assert.assertTrue(esos.containsKey("MANUAL"));
            Assert.assertTrue(esos.containsKey("AUTOMATIC"));
            Assert.assertTrue(esos.get("MANUAL").size() <= tos.size());
            Assert.assertTrue(esos.get("AUTOMATIC").size() <= tos.size());
        }
    }
}

