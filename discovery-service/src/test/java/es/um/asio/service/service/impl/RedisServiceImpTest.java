package es.um.asio.service.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.repository.StringRedisRepository;
import es.um.asio.service.test.TestApplication;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RedisServiceImpTest {

    @Autowired
    RedisServiceImp redisService;

    @Autowired
    FirebaseStorageStrategy firebaseStorageStrategy;


    @Test
    @Order(2)
    void getTriplesMap() {
        Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap = redisService.getTriplesMap();
        Assert.assertTrue(triplesMap.size()>0);
    }

    @Test
    @Order(1)
    void setTriplesMap() {
        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            String content = firebaseStorageStrategy.readFileFromStorage("jTriplesMap.json");
            Type type = new TypeToken<Map<String, Map<String, Map<String, Map<String, TripleObject>>>>>() {
            }.getType();
            Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap = gson.fromJson(content, type);
            redisService.setTriplesMap(triplesMap);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    @Order(4)
    void getFilteredTriples() {
        Map<String, Map<String, Map<String, TripleObject>>> filtered = redisService.getFilteredTriples();
        Assert.assertTrue(filtered.size()>0);
    }

    @Test
    @Order(3)
    void setFilteredTriples() {
        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            String content = firebaseStorageStrategy.readFileFromStorage("jFiltered.json");

            Type type = new TypeToken<Map<String,Map<String, Map<String, TripleObject>>>>() {
            }.getType();
            Map<String,Map<String, Map<String, TripleObject>>> filtered = gson.fromJson(content, type);
            redisService.setFilteredTriples(filtered);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}