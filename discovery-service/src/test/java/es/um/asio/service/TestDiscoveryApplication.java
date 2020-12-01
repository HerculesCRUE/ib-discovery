package es.um.asio.service;

import es.um.asio.service.mapper.MapperConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootApplication
@EnableAutoConfiguration
@Import(MapperConfig.class)
//@WebMvcTest(URISController.class)
@ActiveProfiles("dev")
public class TestDiscoveryApplication {

    public static void main(final String[] args) {
        SpringApplication.run(TestDiscoveryApplication.class, args);
    }

}
