package ZooStock.zoostock_backend.integration;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AnimalApiClient {

    private final RestClient restClient;

    public AnimalApiClient (
            RestClient restClient
    ) {
        this.restClient = restClient;
    }

    public AnimalApiResponse getAnimal(Long taxonId) {
        return restClient.get()
                .uri("/v1/taxa/{id}", taxonId)
                .retrieve()
                .body(AnimalApiResponse.class);
    }
}
