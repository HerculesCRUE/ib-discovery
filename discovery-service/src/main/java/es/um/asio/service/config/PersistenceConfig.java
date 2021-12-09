package es.um.asio.service.config;

import com.google.common.collect.Sets;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import es.um.asio.service.config.properties.DatasourceProperties;
import es.um.asio.service.config.properties.JpaProperties;
import es.um.asio.service.config.properties.PersistenceProperties;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * Persistence configuration.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Configuration
@EnableConfigurationProperties(PersistenceProperties.class)
@EnableJpaRepositories(basePackages = "es.um.asio.service.repository.relational")
@EnableTransactionManagement
@EntityScan(basePackages = "es.um.asio.service.model.relational")
@EnableRedisRepositories(basePackages = "es.um.asio.service.repository.redis")
@EnableElasticsearchRepositories(basePackages = "es.um.asio.service.repository.elasticsearch")
public class PersistenceConfig {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Configuration properties.
     */
    @Autowired
    private PersistenceProperties properties;


    /**
     * Configures de datasource for the application.<br>

     * fields to build it.<br>
     * In either case, HikariCP is used to wrap the datasource.
     *
     * @return an instance of {@link DataSource} to be used as the datasource
     */
    @Bean
    public DataSource dataSource() {
        final HikariConfig config = new HikariConfig();
        final DatasourceProperties datasourceProperties = this.properties.getDatasource();

        if (StringUtils.isNotBlank(datasourceProperties.getJndiName())) {
            // JNDI conection
            final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
            dsLookup.setResourceRef(true);
            final DataSource dataSource = dsLookup.getDataSource(datasourceProperties.getJndiName());
            config.setDataSource(dataSource);
        } else {
            // Parameters connection
            config.setDriverClassName(datasourceProperties.getDriverClassName());
            config.setJdbcUrl(datasourceProperties.getUrl());
            config.setUsername(datasourceProperties.getUsername());
            config.setPassword(datasourceProperties.getPassword());
            config.setConnectionTestQuery("SELECT 1");
            config.setValidationTimeout(60000);
            config.setAutoCommit(true);
            config.setInitializationFailTimeout(0);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(180000);
            config.setMinimumIdle(10);
            config.setMaximumPoolSize(100);
            config.setIdleTimeout(60000);
            // config.setLeakDetectionThreshold(60 * 1000);
            //config.setIdleTimeout(6000000);
            //config.setConnectionTimeout(18000000);
        }

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "500");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("testOnBorrow", "true");
        config.addDataSourceProperty("validationQuery ", "SELECT 1");
        config.addDataSourceProperty("timeBetweenEvictionRunsMillis ", "3600000");
        return new HikariDataSource(config);
    }

    /**
     * Builds the entity manager for the application.
     *
     * @param dataSource
     *            The datasource to be used
     * @return an instance of {@link org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean} for data access
     */
    @Bean
    @Autowired
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
        final JpaProperties jpa = this.properties.getJpa();

        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan(this.getEntityPackages().stream().toArray(String[]::new));
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        final Properties jpaProperties = new Properties();
        if (StringUtils.isNotBlank(jpa.getDialect())) {
            jpaProperties.put(AvailableSettings.DIALECT, jpa.getDialect());
        }

        if (jpa.isGenerateDdl()) {
            jpaProperties.put(AvailableSettings.HBM2DDL_AUTO, "update");
        }
        // jpaProperties.put("hibernate.connection.characterEncoding", "latin1");
        jpaProperties.put(AvailableSettings.SHOW_SQL, jpa.isShowSql());
        jpaProperties.put(AvailableSettings.FORMAT_SQL, jpa.isShowSql());
        jpaProperties.put(AvailableSettings.STATEMENT_BATCH_SIZE, 100);
        jpaProperties.put(AvailableSettings.JDBC_TIME_ZONE, "UTC");
        jpaProperties.put(AvailableSettings.USE_SECOND_LEVEL_CACHE, false);
        jpaProperties.put(AvailableSettings.CACHE_REGION_FACTORY, "org.hibernate.cache.jcache.JCacheRegionFactory");
        jpaProperties.put(AvailableSettings.CACHE_PROVIDER_CONFIG, "org.ehcache.jsr107.EhcacheCachingProvider");
        jpaProperties.putAll(jpa.getProperties());

        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    /**
     * Creates and configures the TransactionManager instance for the platform.
     *
     * @return an instance of {@link PlatformTransactionManager}
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    /**
     * Gets packages containing entities.
     * 
     * @return Set of packages containing entities
     */
    private Set<String> getEntityPackages() {
        final Set<String> packages = Sets.newHashSet();

        final Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(EntityScan.class);

        EntityScan annotation;

        for (final String name : beans.keySet()) {
            annotation = this.applicationContext.findAnnotationOnBean(name, EntityScan.class);

            if(annotation != null) {
                // Get basePackages configuration
                if (annotation.basePackages() != null) {
                    packages.addAll(Arrays.asList(annotation.basePackages()));
                }
    
                // Get basePackageClasses configuration
                if (annotation.basePackageClasses() != null) {
                    for (final Class<?> clazz : annotation.basePackageClasses()) {
                        packages.add(clazz.getPackageName());
                    }
                }
            }
        }

        return packages;
    }
}
