package es.um.asio.back.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import es.um.asio.back.config.properties.CorsProperties;
import es.um.asio.back.filter.SimpleCORSFilter;

/**
 * Web MVC related configuration.
 */
@EnableConfigurationProperties(CorsProperties.class)
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configures and registers the CORS filter for the application.
     *
     * @param corsProperties
     *            Cors related configuration properties.
     * @return the filter registration bean
     */
    @Bean
    public FilterRegistrationBean<SimpleCORSFilter> simpleCORSFilterRegistrationBean(
            @Autowired final CorsProperties corsProperties) {
        final FilterRegistrationBean<SimpleCORSFilter> registrationBean = new FilterRegistrationBean<>();
        final SimpleCORSFilter corsFilter = new SimpleCORSFilter(corsProperties.isEnabled(),
                corsProperties.getAllowedOrigin(), corsProperties.getAllowedMethods(),
                corsProperties.getAllowedHeaders(), corsProperties.getMaxAge());
        registrationBean.setFilter(corsFilter);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
