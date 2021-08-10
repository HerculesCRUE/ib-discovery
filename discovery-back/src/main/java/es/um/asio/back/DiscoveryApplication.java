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
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@Import({ ServiceConfig.class })


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
        @SuppressWarnings("unused")
        org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger("org.hibernate");
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.WARNING); //or whatever level you need
        SpringApplication.run(DiscoveryApplication.class, args);
    }

    @Bean
    InitializingBean populateInitData() {
        return () -> dataHandler.populateData();
    }

}
