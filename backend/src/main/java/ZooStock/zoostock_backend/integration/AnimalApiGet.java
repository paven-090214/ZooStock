package ZooStock.zoostock_backend.integration;

import ZooStock.zoostock_backend.config.AnimalImportProperties;
import ZooStock.zoostock_backend.market.AnimalImportService;
import ZooStock.zoostock_backend.market.exception.MarketException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class AnimalApiGet implements CommandLineRunner {
    private final AnimalImportService animalImportService;
    private final AnimalImportProperties animalImportProperties;

    public AnimalApiGet (
            AnimalImportService animalImportService,
            AnimalImportProperties animalImportProperties
    ) {
        this.animalImportService = animalImportService;
        this.animalImportProperties = animalImportProperties;
    }

    @Override
    public void run(String... args) throws InterruptedException {

        if (!animalImportProperties.enabled()) {
            return;
        }

        for (Long taxonId : animalImportProperties.taxonIds()) {
            try {
                animalImportService.getAnimal(taxonId);
            } catch (MarketException | RestClientException e) {
                System.err.println(
                        "동물 등록 실패: " + taxonId + " / " + e.getMessage()
                );
            }

            Thread.sleep(1100);
        }
    }
}
