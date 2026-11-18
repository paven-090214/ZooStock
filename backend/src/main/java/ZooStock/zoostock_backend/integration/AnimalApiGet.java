package ZooStock.zoostock_backend.integration;

import ZooStock.zoostock_backend.market.AnimalImportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnimalApiGet implements CommandLineRunner {
    private final AnimalImportService animalImportService;

    public AnimalApiGet (
            AnimalImportService animalImportService
    ) {
        this.animalImportService = animalImportService;
    }

    @Override
    public void run(String... args) {

        List<Long> taxonIds = List.of(
                41964L, 41967L, 41963L, 41970L, 41955L,
                42048L, 42069L, 41641L, 41644L, 41659L
        );

        for (Long taxonId : taxonIds) {
            animalImportService.getAnimal(taxonId);
        }
    }
}
