package es.um.asio.service.service.impl;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
/*@SpringBootTest(classes={TestApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)*/
class RedisServiceImpTest {

    /*@Autowired
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
            redisService.setTriplesMap(triplesMap,true, true);
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
    }*/
}