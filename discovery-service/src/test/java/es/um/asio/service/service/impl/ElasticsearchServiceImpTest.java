package es.um.asio.service.service.impl;

import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.repository.elasticsearch.TripleObjectESCustomRepository;
import es.um.asio.service.repository.elasticsearch.TripleObjectESRepository;
import es.um.asio.service.test.TestApplication;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.javatuples.Pair;
import java.util.*;
import org.junit.jupiter.api.Order;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;


@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
class ElasticsearchServiceImpTest {

    @Autowired
    ElasticsearchServiceImp esService;

    static Set<TripleObjectES> tripleObjectsES;

    @BeforeEach
    void setUp() {
        tripleObjectsES = new HashSet<>();

        for (int i = 0 ; i < 10 ; i++) {
            TripleObjectES to = new TripleObjectES(String.valueOf(i),String.format("clase1"), new Date());
            to.setTripleStore(new TripleStore("trellis","um","http://herc-iz-front-desa.atica.um.es/","admin","admin"));
            LinkedTreeMap<String,Object> attrs = new LinkedTreeMap<>();
            for (int j = 1 ; j <= 5 ; j++){
                attrs.put(String.format("key%s",j),String.format("value%s_%s",i,j));
            }
            to.setAttributes(attrs);
            tripleObjectsES.add(to);
        }
    }

    @AfterEach
    void setDown() {
        esService.deleteTripleObjectsES(new ArrayList<>(tripleObjectsES));
    }


    @Test
    @Order(1)
    void saveTripleObjectES() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        TripleObjectES toESResponse = esService.saveTripleObjectES(tripleObjectES);
        Assert.assertNotNull(toESResponse);
        esService.deleteTripleObjectES(toESResponse);
        Assert.assertNull(esService.getTripleObjectESById(toESResponse.getId()));
    }

    @Test
    @Order(2)
    void saveTripleObjectsES() {
        List<TripleObjectES> toESs = new ArrayList<>();
        toESs.addAll(tripleObjectsES);
        List<TripleObjectES> rToESs = (List<TripleObjectES>) esService.saveTripleObjectsES(toESs);
        Assert.assertTrue(toESs.size() == rToESs.size());
        esService.deleteTripleObjectsES(rToESs);
        List<TripleObjectES> r = esService.getTripleObjectsESByClassNameAndAttributes(TripleObjectES.class,"triple-object", "clase1",new ArrayList<Pair<String, Object>>());
        Assert.assertTrue(r.size() == 0);

    }

    @Test
    @Order(3)
    void saveTripleObject() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        TripleObject tripleObject = new TripleObject(tripleObjectES);
        TripleObjectES toESResponse = esService.saveTripleObject(tripleObject);
        Assert.assertNotNull(toESResponse);
        esService.deleteTripleObjectES(toESResponse);
        Assert.assertNull(esService.getTripleObjectESById(toESResponse.getId()));
    }

    @Test
    @Order(4)
    void saveTripleObjects() {
        List<TripleObjectES> toESs = new ArrayList<>();
        toESs.addAll(tripleObjectsES);

        List<TripleObject> tos = new ArrayList<>();
        for (TripleObjectES toES : toESs) {
            tos.add(new TripleObject(toES));
        }

        List<TripleObjectES> rToESs = (List<TripleObjectES>) esService.saveTripleObjects(tos);
        Assert.assertTrue(tos.size() == rToESs.size());
        esService.deleteTripleObjectsES(rToESs);
        List<TripleObjectES> r = esService.getTripleObjectsESByClassNameAndAttributes(TripleObjectES.class,"triple-object", "clase1",new ArrayList<Pair<String, Object>>());
        Assert.assertTrue(r.size() == 0);
    }

    @Test
    @Order(5)
    void deleteTripleObjectES() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        TripleObjectES toESResponse = esService.saveTripleObjectES(tripleObjectES);
        Assert.assertNotNull(toESResponse);
        esService.deleteTripleObjectES(toESResponse);
        Assert.assertNull(esService.getTripleObjectESById(toESResponse.getId()));
    }

    @Test
    @Order(6)
    void deleteTripleObjectsES() {
        List<TripleObjectES> toESs = new ArrayList<>();
        toESs.addAll(tripleObjectsES);
        List<TripleObjectES> rToESs = (List<TripleObjectES>) esService.saveTripleObjectsES(toESs);
        Assert.assertTrue(toESs.size() == rToESs.size());
        esService.deleteTripleObjectsES(rToESs);
        List<TripleObjectES> r = esService.getTripleObjectsESByClassNameAndAttributes(TripleObjectES.class,"triple-object", "clase1",new ArrayList<Pair<String, Object>>());
        Assert.assertTrue(r.size() == 0);
    }

    @Test
    @Order(7)
    void deleteTripleObject() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        TripleObject tripleObject = new TripleObject(tripleObjectES);
        TripleObjectES toESResponse = esService.saveTripleObject(tripleObject);
        Assert.assertNotNull(toESResponse);
        esService.deleteTripleObject(tripleObject);
        Assert.assertNull(esService.getTripleObjectESById(toESResponse.getId()));
    }

    @Test
    void deleteTripleObjects() {
        List<TripleObjectES> toESs = new ArrayList<>();
        toESs.addAll(tripleObjectsES);

        List<TripleObject> tos = new ArrayList<>();
        for (TripleObjectES toES : toESs) {
            tos.add(new TripleObject(toES));
        }

        List<TripleObjectES> rToESs = (List<TripleObjectES>) esService.saveTripleObjects(tos);
        Assert.assertTrue(tos.size() == rToESs.size());
        esService.deleteTripleObjects(tos);
        List<TripleObjectES> r = esService.getTripleObjectsESByClassNameAndAttributes(TripleObjectES.class,"triple-object", "clase1",new ArrayList<Pair<String, Object>>());
        Assert.assertTrue(r.size() == 0);
    }

    @Test
    void getAll() {
        List<TripleObjectES> a = esService.getAll();
        esService.saveTripleObjectsES(new ArrayList<>(tripleObjectsES));
        List<TripleObjectES> res = esService.getAll();
        Assert.assertTrue(res.size() == tripleObjectsES.size());
        esService.deleteTripleObjectsES(new ArrayList<>(tripleObjectsES));
    }

    @Test
    void getAllMappedById() {
        List<TripleObjectES> a = esService.getAll();
        esService.saveTripleObjectsES(new ArrayList<>(tripleObjectsES));
        Assert.assertTrue(esService.getAllMappedById().size() == tripleObjectsES.size());
        esService.deleteTripleObjectsES(new ArrayList<>(tripleObjectsES));
    }

    @Test
    void getTripleObjectESById() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        TripleObjectES toESResponse = esService.saveTripleObjectES(tripleObjectES);
        Assert.assertNotNull(esService.getTripleObjectESById(toESResponse.getId()));
        Assert.assertNotNull(toESResponse);
        esService.deleteTripleObjectES(toESResponse);
        Assert.assertNull(esService.getTripleObjectESById(toESResponse.getId()));
    }

    @Test
    void getTripleObjectById() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        TripleObjectES toESResponse = esService.saveTripleObjectES(tripleObjectES);
        Assert.assertNotNull(esService.getTripleObjectESById(toESResponse.getId()));
        Assert.assertNotNull(toESResponse);
    }

    @Test
    void getTripleObjectsESByClassNameAndAttributes() {
        for (TripleObjectES toES : tripleObjectsES) {
            List<Pair<String, Object>> params = new ArrayList<>();
            for (Map.Entry<String, Object> p : toES.getAttributes().entrySet()) {
                params.add(new Pair<>(p.getKey(),p.getKey()));
            }
            Assert.assertNotNull(esService.getTripleObjectsESByClassNameAndAttributes(TripleObjectES.class,"triple-object", "clase1", params));
        }
    }

    @Test
    void getTripleObjectsByClassNameAndAttributes() {
        for (TripleObjectES toES : tripleObjectsES) {
            List<Pair<String, Object>> params = new ArrayList<>();
            for (Map.Entry<String, Object> p : toES.getAttributes().entrySet()) {
                params.add(new Pair<>(p.getKey(),p.getKey()));
            }
            Assert.assertNotNull(esService.getTripleObjectsByClassNameAndAttributes(TripleObjectES.class,"triple-object", "clase1", params));
        }
    }

}