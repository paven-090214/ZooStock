package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.market.dto.DailyRankingResponse;
import ZooStock.zoostock_backend.market.dto.PriceHistoryResponse;
import ZooStock.zoostock_backend.market.dto.TokenChangeProjection;
import ZooStock.zoostock_backend.market.dto.TokenDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
            priceHistoryEntity.setRecordedAt(LocalDateTime.now());
            priceHistoryEntity.setTokenId(tokenEntity.getTokenId());
            tokenRepository.save(tokenEntity);
            priceHistoryRepository.save(priceHistoryEntity);
        }
    }

    public List<PriceHistoryResponse> getTokenHistory (Long tokenId, String time) {

        if (tokenId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "토큰의 검색 결과가 없습니다.");


        if (time == null || time.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정해진 시간을 입력해주세요.");

        boolean exists = tokenRepository.existsByTokenId(tokenId);

        if (!exists)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "토큰을 찾을 수 없습니다.");

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start;

        switch (time) {
            case "1h":
                start = end.minusHours(1);
                break;
            case "5h":
                start = end.minusHours(5);
                break;
            case "1d":
                start = end.minusDays(1);
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "기간 설정이 잘못되었습니다.");
        }

        return priceHistoryRepository.findByTokenIdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
                tokenId,
                start,
                        end
                        )
                .stream()
                .map(
                        priceHistory -> priceHistory.toPriceHistoryResponse(
                                priceHistory.getHistoryId(),
                                priceHistory.getPrice(),
                                priceHistory.getTokenId(),
                                priceHistory.getRecordedAt()
                        )
                )
                .toList();
    }

    public DailyRankingResponse getDailyChangeRanking () {
        List<TokenChangeProjection> gainers = priceHistoryRepository.findDailyChangeRates("UP");
        List<TokenChangeProjection> losers = priceHistoryRepository.findDailyChangeRates("DOWN");
        return new DailyRankingResponse(
                gainers,
                losers
        );
    }

    public List<TokenDTO> getRandomTokens() {
        return priceHistoryRepository.getRandomTokens()
                .stream()
                .map(
                        token -> token.toTokenDTO(
                                token.getTokenId(),
                                token.getTokenName(),
                                token.getTokenPrice(),
                                token.getImgPath()
                        )
                )
                .toList();
    }

}