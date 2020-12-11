package es.um.asio.back;

import es.um.asio.service.ServiceConfig;
import es.um.asio.service.service.impl.DataHandlerImp;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@Import({ ServiceConfig.class })

@ComponentScan(basePackages="es.um.asio.back.controller")
@EnableAsync
public class DiscoveryApplication {

    @Autowired
    DataHandlerImp dataHandler;

    /**
     * Main method for embedded deployment.
     *
     * @param args
     *            the arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }

    @Bean
    InitializingBean populateInitData() throws ParseException, IOException, URISyntaxException {
        return () -> {
            dataHandler.populateData();
        };
    }

}
