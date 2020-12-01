package main;

import es.um.asio.service.ServiceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableAutoConfiguration
@Import({ ServiceConfig.class })
public class TestApplicationOld {

    public static void main(String[] args) {
        SpringApplication.run(TestApplicationOld.class);
    }

}
