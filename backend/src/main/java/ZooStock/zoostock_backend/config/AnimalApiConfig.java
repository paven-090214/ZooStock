package ZooStock.zoostock_backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AnimalImportProperties.class)
public class AnimalApiConfig {
    @Bean
    public RestClient getAnimal() {
        return RestClient.create("https://api.inaturalist.org");
    }
}