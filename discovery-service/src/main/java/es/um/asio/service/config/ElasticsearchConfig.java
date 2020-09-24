package es.um.asio.service.config;

import es.um.asio.service.config.properties.DatasourceProperties;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "es.um.asio.service.repository.elasticsearch")
// @ComponentScan(basePackages = { "com.baeldung.spring.data.es.service" })
public class ElasticsearchConfig {

    @Autowired
    DataProperties dataProperties;

    @Bean
    public RestHighLevelClient client() {
        DataProperties.ElasticSearch es = dataProperties.getElasticSearch();
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(String.format("%s:%d",es.getHost(),es.getPort()))
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}
