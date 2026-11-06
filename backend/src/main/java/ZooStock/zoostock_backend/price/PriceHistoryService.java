package ZooStock.zoostock_backend.price;

import ZooStock.zoostock_backend.token.TokenEntity;
import ZooStock.zoostock_backend.token.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import java.util.List;

@Service
public class PriceHistoryService {

    private final TokenRepository tokenRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    public PriceHistoryService(
            TokenRepository tokenRepository,
            PriceHistoryRepository priceHistoryRepository
    ) {
        this.tokenRepository = tokenRepository;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Transactional
    public void updatePrice() {
        List<TokenEntity> tokenList = tokenRepository.findAll();
        Date date = new Date();

        for (TokenEntity tokenEntity : tokenList) {
            PriceHistoryEntity priceHistoryEntity = new PriceHistoryEntity();
            // -30부터 30까지 난수를 뽑음
            int changePercent = ThreadLocalRandom.current().nextInt(-30, 31);
            // -0.008 ~ 0.008의 변동률을 기존 가격에 적용 <- codex 사용
            BigDecimal changeRate = BigDecimal.valueOf(changePercent)
                    .divide(BigDecimal.valueOf(1000));
            BigDecimal value = BigDecimal.valueOf(tokenEntity.getTokenPrice())
                    .multiply(BigDecimal.ONE.add(changeRate));

            tokenEntity.setTokenPrice(value.setScale(0, RoundingMode.HALF_UP).longValueExact());

            priceHistoryEntity.setPrice(tokenEntity.getTokenPrice());
            priceHistoryEntity.setRecordedAt(date);
            priceHistoryEntity.setTokenId(tokenEntity.getTokenId());
            tokenRepository.save(tokenEntity);
            priceHistoryRepository.save(priceHistoryEntity);
        }
    }
}