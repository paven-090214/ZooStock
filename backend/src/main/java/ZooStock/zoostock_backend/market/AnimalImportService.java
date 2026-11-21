package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.integration.AnimalApiClient;
import ZooStock.zoostock_backend.integration.AnimalApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class AnimalImportService {

    private final AnimalApiClient animalApiClient;
    private final TokenRepository tokenRepository;

    public AnimalImportService (
            AnimalApiClient animalApiClient,
            TokenRepository tokenRepository
    ) {
        this.animalApiClient = animalApiClient;
        this.tokenRepository = tokenRepository;
    }

    public void getAnimal(Long taxonId) {

        if (tokenRepository.findByTaxonId(taxonId).isPresent()) {
            return;
        }

        AnimalApiResponse response =  animalApiClient.getAnimal(taxonId);
        if (response == null ||
                response.getResults() == null ||
                response.getResults().isEmpty() ||
                response.getResults().get(0).getDefaultPhoto() == null ||
                response.getResults().get(0).getDefaultPhoto().getMediumUrl() == null
        ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "동물을 찾을 수 없습니다.");
        }

        AnimalApiResponse.AnimalApiData animal = response.getResults().get(0);

        String tokenName = animal.getPreferredCommonName();
        String tokenImg = animal.getDefaultPhoto().getMediumUrl();

        if (tokenName == null ||tokenName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이름을 찾을 수 없습니다.");
        }

        if (tokenImg == null || tokenImg.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다.");
        }

        Long tokenPrice = ThreadLocalRandom.current().nextLong(3000L, 11001L);

        TokenEntity entity = new TokenEntity(
                taxonId,
                tokenName,
                tokenPrice,
                tokenImg
        );
        tokenRepository.save(entity);
        return;
    }
}
