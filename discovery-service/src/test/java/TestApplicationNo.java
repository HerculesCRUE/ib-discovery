
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.test.context.junit4.SpringRunner;

/*@SpringBootApplication
@EnableAsync
@EnableAutoConfiguration
@Import(MapperConfig.class)
@ActiveProfiles("dev")*/
/*@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@Import({ ServiceConfig.class })

@ComponentScan(basePackages="es.um.asio.back.controller")
@EnableAsync*/
@RunWith(SpringRunner.class)
public class TestApplicationNo {
    /**@SpringBootApplication
     @EnableAutoConfiguration
     @Import({ ServiceConfig.class })
     @ComponentScan
     * Main method for embedded deployment.
     *
     * @param args
     *            the arguments
     */

/*    @Bean("threadPoolExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("GithubLookup-");
        executor.initialize();
        return executor;
    }*/

    public static void main(final String[] args) {
        SpringApplication.run(TestApplicationNo.class, args);
    }

    /**
     * Creates the password encoder BCrypt.
     *
     * @return The password encoder.
     *//*
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }*/
}
