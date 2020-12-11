package es.um.asio.service.repository;


import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
/*@SpringBootTest(classes={TestApplication.class})*/
class StringRedisRepositoryTest {

    /*@Autowired
    StringRedisRepository stringRedisRepository;

    @Test
    public void addTest(){
        String key = "test_key_1";
        String value = "test_value_1";
        stringRedisRepository.add(key,value);
        String storedValue = stringRedisRepository.getBy(key);
        Assert.assertTrue(storedValue.equals(value));
        stringRedisRepository.delete(key);
    }


    @Test
    void getBy() {
        String key = "test_key_2";
        String value = "test_value_2";
        stringRedisRepository.add(key,value);
        String storedValue = stringRedisRepository.getBy(key);
        Assert.assertTrue(storedValue.equals(value));
        stringRedisRepository.delete(key);
    }

    @Test
    void getKeys() {
        Set<String> keysSet = new HashSet<>();
        for (int i = 0 ; i<10 ; i++) {
            String key = String.format("test_key_%s",i);
            String value = String.format("test_value_%s",i);
            stringRedisRepository.add(key,value);
            keysSet.add(key);
        }
        for (String key : stringRedisRepository.getKeys("test_key_*")) {
            keysSet.remove(key);
            stringRedisRepository.delete(key);
        }
        Assert.assertTrue(keysSet.size()==0);
    }

    @Test
    void getAllValuesBy() {
        Set<String> valuesSet = new HashSet<>();
        for (int i = 0 ; i<10 ; i++) {
            String key = String.format("test_key_%s",i);
            String value = String.format("test_value_%s",i);
            stringRedisRepository.add(key,value);
            valuesSet.add(value);
        }
        for (String key : stringRedisRepository.getAllValuesBy("test_key_*")) {
            valuesSet.remove(key);
            stringRedisRepository.delete(key);
        }
        Assert.assertTrue(valuesSet.size()==0);
    }

    @Test
    void delete() {
        String key = "test_key_1";
        String value = "test_value_1";
        stringRedisRepository.add(key,value);
        Assert.assertNotNull(stringRedisRepository.getBy(key));
        stringRedisRepository.delete(key);
        Assert.assertNull(stringRedisRepository.getBy(key));
    }*/
}