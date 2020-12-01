package es.um.asio.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.test.TestApplication;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
class FirebaseStorageStrategyTest {

    @Autowired
    FirebaseStorageStrategy firebaseStorageStrategy;

    @Autowired
    RedisServiceImp redisServiceImp;

    boolean writeTriplesMapInFireBase = true;


    @Test
    void readFileFromStorage() {
        String str = firebaseStorageStrategy.readFileFromStorage("test.txt");
        Assert.assertTrue(str.trim().equals("Hola Mundo"));
    }

    @Test
    void writeFile() {
        String content = "Hola Mundo";
        firebaseStorageStrategy.writeFile("test.txt","Hola Mundo");
        Assert.assertTrue(content.equals(firebaseStorageStrategy.readFileFromStorage("test.txt").trim()));
    }

    @Test
    void writeRedisTriplesInFireBase() throws JsonProcessingException {
        if (writeTriplesMapInFireBase) {

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            Map<String, Map<String, Map<String, Map<String, TripleObject>>>> map = redisServiceImp.getTriplesMap();
            String content = new ObjectMapper().writeValueAsString(map);
            Type type = new TypeToken<Map<String, Map<String, Map<String, Map<String, TripleObject>>>>>() {
            }.getType();

            Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap = gson.fromJson(content, type);
            JsonObject jTriplesMap = gson.fromJson(gson.toJson(triplesMap),JsonObject.class);

            CompletableFuture<String[]> rFuture = firebaseStorageStrategy.writeFile("jTriplesMap.json", jTriplesMap.toString());
            rFuture.join();

        }

    }
}