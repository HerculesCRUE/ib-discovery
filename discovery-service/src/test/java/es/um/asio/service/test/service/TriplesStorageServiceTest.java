package es.um.asio.service.test.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import es.um.asio.service.service.TriplesStorageService;
import es.um.asio.service.service.impl.TrellisStorageServiceImpl;

@RunWith(SpringRunner.class)
public class TriplesStorageServiceTest {
    /**
     * Triples storage service.
     */
    @Autowired
    private TriplesStorageService service;
    
    @TestConfiguration
    static class UserServiceTestConfiguration {
        @Bean
        public TriplesStorageService userService() {
            return new TrellisStorageServiceImpl();
        }
    }
    
    @Test
    public void test_Infraestructure() {
        assertNotNull(service);
    }
}
