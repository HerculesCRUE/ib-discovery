package es.um.asio.service.config;

import es.um.asio.service.util.Utils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

class DataPropertiesTest {

    DataProperties dp;

    @BeforeEach
    void setUp() {
        dp = new DataProperties();
        dp.setReadCacheFromFirebase(true);
    }

    @Test
    void testToString() {
        Assert.assertTrue(Utils.isValidString(dp.toString()));
    }

    @Test
    void setReadCacheFromFirebase() {
        boolean rnd = new Random().nextBoolean();
        dp.setReadCacheFromFirebase(rnd);
        Assert.assertEquals(rnd,dp.isReadCacheFromFirebase());
    }

    @Test
    void setElasticSearch() {
        DataProperties.ElasticSearch es = new DataProperties.ElasticSearch();
        dp.setElasticSearch(es);
        Assert.assertEquals(es,dp.getElasticSearch());
    }


    @Test
    void setElasticSearchHost() {
        DataProperties.ElasticSearch es = new DataProperties.ElasticSearch();
        es.setHost("localhost");
        dp.setElasticSearch(es);
        Assert.assertEquals(es.getHost(),dp.getElasticSearch().getHost());
    }

    @Test
    void setElasticSearchPort() {
        DataProperties.ElasticSearch es = new DataProperties.ElasticSearch();
        es.setPort(9200);
        dp.setElasticSearch(es);
        Assert.assertEquals(es.getPort(),dp.getElasticSearch().getPort());
    }

    @Test
    void setElasticSearchPassword() {
        DataProperties.ElasticSearch es = new DataProperties.ElasticSearch();
        String rnd = RandomStringUtils.randomAlphabetic(10);
        es.setPassword(rnd);
        dp.setElasticSearch(es);
        Assert.assertEquals(es.getPassword(),dp.getElasticSearch().getPassword());
    }

    @Test
    void setRedis() {
        DataProperties.Redis r = new DataProperties.Redis();
        dp.setRedis(r);
        Assert.assertEquals(r,dp.getRedis());
    }

    @Test
    void setRedisHost() {
        DataProperties.Redis r = new DataProperties.Redis();
        r.setHost("localhost");
        dp.setRedis(r);
        Assert.assertEquals(r.getHost(),dp.getRedis().getHost());
    }

    @Test
    void setRedisPort() {
        DataProperties.Redis r = new DataProperties.Redis();
        int rnd = Math.abs(new Random().nextInt());
        r.setPort(rnd );
        dp.setRedis(r);
        Assert.assertEquals(r.getPort(),dp.getRedis().getPort());
    }

    @Test
    void setRedisPassword() {
        DataProperties.Redis r = new DataProperties.Redis();
        String rnd = RandomStringUtils.randomAlphabetic(10);
        r.setPassword(rnd);
        dp.setRedis(r);
        Assert.assertEquals(r.getPassword(),dp.getRedis().getPassword());
    }

    @Test
    void setKafka() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        dp.setKafka(k);
        Assert.assertEquals(k,dp.getKafka());
    }

    @Test
    void setKafkaHost() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        k.setHost("localhost");
        dp.setKafka(k);
        Assert.assertEquals(k.getHost(),dp.getKafka().getHost());
    }

    @Test
    void setKafkaPort() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        int rnd = Math.abs(new Random().nextInt());
        k.setPort(rnd );
        dp.setKafka(k);
        Assert.assertEquals(k.getPort(),dp.getKafka().getPort());
    }

    @Test
    void setKafkaTopicEntityChange() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        DataProperties.Kafka.TopicEntityChange tec = new DataProperties.Kafka.TopicEntityChange();
        k.setTopicEntityChange(tec);
        dp.setKafka(k);
        Assert.assertEquals(tec,dp.getKafka().getTopicEntityChange());
    }

    @Test
    void setKafkaTopicEntityChangeTopic() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        DataProperties.Kafka.TopicEntityChange tec = new DataProperties.Kafka.TopicEntityChange();
        String rnd = RandomStringUtils.randomAlphabetic(10);
        tec.setTopic(rnd);
        k.setTopicEntityChange(tec);
        dp.setKafka(k);
        Assert.assertEquals(rnd,dp.getKafka().getTopicEntityChange().getTopic());
    }

    @Test
    void setKafkaTopicEntityChangeGroupId() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        DataProperties.Kafka.TopicEntityChange tec = new DataProperties.Kafka.TopicEntityChange();
        String rnd = RandomStringUtils.randomAlphabetic(10);
        tec.setGroupId(rnd);
        k.setTopicEntityChange(tec);
        dp.setKafka(k);
        Assert.assertEquals(rnd,dp.getKafka().getTopicEntityChange().getGroupId());
    }

    @Test
    void setKafkaTopicDiscoveryAction() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        DataProperties.Kafka.TopicDiscoveryAction tda = new DataProperties.Kafka.TopicDiscoveryAction();
        k.setTopicDiscoveryAction(tda);
        dp.setKafka(k);
        Assert.assertEquals(tda,dp.getKafka().getTopicDiscoveryAction());
    }

    @Test
    void setKafkaTopicDiscoveryActionTopic() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        DataProperties.Kafka.TopicDiscoveryAction tda = new DataProperties.Kafka.TopicDiscoveryAction();
        String rnd = RandomStringUtils.randomAlphabetic(10);
        tda.setTopic(rnd);
        k.setTopicDiscoveryAction(tda);
        dp.setKafka(k);
        Assert.assertEquals(rnd,dp.getKafka().getTopicDiscoveryAction().getTopic());
    }

    @Test
    void setKafkaTopicDiscoveryActionGroupId() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        DataProperties.Kafka.TopicDiscoveryAction tda = new DataProperties.Kafka.TopicDiscoveryAction();
        String rnd = RandomStringUtils.randomAlphabetic(10);
        tda.setGroupId(rnd);
        k.setTopicDiscoveryAction(tda);
        dp.setKafka(k);
        Assert.assertEquals(rnd,dp.getKafka().getTopicDiscoveryAction().getGroupId());
    }


    @Test
    void isReadCacheFromFirebase() {
        boolean rnd = new Random().nextBoolean();
        dp.setReadCacheFromFirebase(rnd);
        Assert.assertEquals(rnd,dp.isReadCacheFromFirebase());
    }

    @Test
    void getElasticSearch() {
        DataProperties.ElasticSearch es = new DataProperties.ElasticSearch();
        dp.setElasticSearch(es);
        Assert.assertEquals(es,dp.getElasticSearch());
    }

    @Test
    void getRedis() {
        DataProperties.Redis r = new DataProperties.Redis();
        dp.setRedis(r);
        Assert.assertEquals(r,dp.getRedis());
    }

    @Test
    void getRedisHost() {
        DataProperties.Redis r = new DataProperties.Redis();
        r.setHost("localhost");
        dp.setRedis(r);
        Assert.assertEquals(r.getHost(),dp.getRedis().getHost());
    }

    @Test
    void getRedisPort() {
        DataProperties.Redis r = new DataProperties.Redis();
        int rnd = Math.abs(new Random().nextInt());
        r.setPort(rnd );
        dp.setRedis(r);
        Assert.assertEquals(r.getPort(),dp.getRedis().getPort());
    }

    @Test
    void getRedisPassword() {
        DataProperties.Redis r = new DataProperties.Redis();
        String rnd = RandomStringUtils.randomAlphabetic(10);
        r.setPassword(rnd);
        dp.setRedis(r);
        Assert.assertEquals(r.getPassword(),dp.getRedis().getPassword());
    }

    @Test
    void getKafka() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        dp.setKafka(k);
        Assert.assertEquals(k,dp.getKafka());
    }

    @Test
    void getKafkaSearchHost() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        k.setHost("localhost");
        dp.setKafka(k);
        Assert.assertEquals(k.getHost(),dp.getKafka().getHost());
    }

    @Test
    void getKafkaSearchPort() {
        DataProperties.Kafka k = new DataProperties.Kafka();
        int rnd = Math.abs(new Random().nextInt());
        k.setPort(rnd);
        dp.setKafka(k);
        Assert.assertEquals(k.getPort(), dp.getKafka().getPort());
    }

    @Test
    void getElasticSearchHost() {
        DataProperties.ElasticSearch es = new DataProperties.ElasticSearch();
        es.setHost("localhost");
        dp.setElasticSearch(es);
        Assert.assertEquals(es.getHost(),dp.getElasticSearch().getHost());
    }

    @Test
    void getElasticSearchPort() {
        DataProperties.ElasticSearch es = new DataProperties.ElasticSearch();
        es.setPort(9200);
        dp.setElasticSearch(es);
        Assert.assertEquals(es.getPort(),dp.getElasticSearch().getPort());
    }

    @Test
    void getElasticSearchPassword() {
        DataProperties.ElasticSearch es = new DataProperties.ElasticSearch();
        String rnd = RandomStringUtils.randomAlphabetic(10);
        es.setPassword(rnd);
        dp.setElasticSearch(es);
        Assert.assertEquals(es.getPassword(),dp.getElasticSearch().getPassword());
    }
}