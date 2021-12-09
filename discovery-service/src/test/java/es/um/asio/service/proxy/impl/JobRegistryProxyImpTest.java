package es.um.asio.service.proxy.impl;

import es.um.asio.service.ServiceConfig;
import es.um.asio.service.mapper.MapperConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/*
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ServiceConfig.class })
@EnableConfigurationProperties
 */
class JobRegistryProxyImpTest {

    @Autowired
    JobRegistryProxyImp jobRegistryProxyImp;

    @Test
    void save() {
        System.out.printf("");
    }
}

/*
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@Import({ ServiceConfig.class })
 */