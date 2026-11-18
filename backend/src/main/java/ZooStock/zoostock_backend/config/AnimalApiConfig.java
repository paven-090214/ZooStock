package ZooStock.zoostock_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AnimalApiConfig {
    @Bean
    public RestClient getAnimal() {
        return RestClient.create("https://api.inaturalist.org");
    }
}