package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.market.dto.DailyRankingResponse;
import ZooStock.zoostock_backend.market.dto.PriceHistoryResponse;
import ZooStock.zoostock_backend.market.dto.TokenChangeProjection;
import ZooStock.zoostock_backend.market.dto.TokenDTO;
import ZooStock.zoostock_backend.market.exception.MarketErrorCode;
import ZooStock.zoostock_backend.market.exception.MarketException;
import ZooStock.zoostock_backend.trade.TradeHistoryRepository;
import ZooStock.zoostock_backend.trade.dto.TradeHistoryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import java.util.List;

@Service
public class PriceHistoryService {

    // 가격이 돌아오려는 기준 (시작 가격 3,000 ~ 11,000의 가운데)
    private static final long BASE_PRICE = 7000L;
    // 토큰의 최저 가격
    private static final long MIN_PRICE = 100L;

    private final TokenRepository tokenRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final TradeHistoryRepository tradeHistoryRepository;

    public PriceHistoryService(
            TokenRepository tokenRepository,
            PriceHistoryRepository priceHistoryRepository,
            TradeHistoryRepository tradeHistoryRepository
    ) {
        this.tokenRepository = tokenRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.tradeHistoryRepository = tradeHistoryRepository;
    }

    @Transactional
    public void updatePrice() {
        List<TokenEntity> tokenList = tokenRepository.findAll();

        for (TokenEntity tokenEntity : tokenList) {
            PriceHistoryEntity priceHistoryEntity = new PriceHistoryEntity();
            Long currentPrice = tokenEntity.getTokenPrice();

            // -100부터 100까지 난수를 뽑음 (1 = 0.01%, 100 = 1%)
            int changePercent = ThreadLocalRandom.current().nextInt(-100, 101);

            // 기준 가격보다 싸면 조금 더 오르게, 비싸면 조금 더 내리게 보정
            if (currentPrice < BASE_PRICE) {
                changePercent += 1;
            } else if (currentPrice > BASE_PRICE) {
                changePercent -= 1;
            }

            // 약 -0.01 ~ 0.01의 변동률을 기존 가격에 적용
            BigDecimal changeRate = BigDecimal.valueOf(changePercent)
                    .divide(BigDecimal.valueOf(10000));
            BigDecimal value = BigDecimal.valueOf(currentPrice)
                    .multiply(BigDecimal.ONE.add(changeRate));

            // 소수점을 반올림하고, 최저 가격보다 낮으면 최저 가격을 사용
            long newPrice = value.setScale(0, RoundingMode.HALF_UP).longValueExact();
            tokenEntity.setTokenPrice(Math.max(newPrice, MIN_PRICE));

            priceHistoryEntity.setPrice(tokenEntity.getTokenPrice());
            priceHistoryEntity.setRecordedAt(LocalDateTime.now());
            priceHistoryEntity.setTokenId(tokenEntity.getTokenId());
            tokenRepository.save(tokenEntity);
            priceHistoryRepository.save(priceHistoryEntity);
        }
    }

    public List<PriceHistoryResponse> getTokenHistory (Long tokenId, String time) {

        if (tokenId == null)
            throw new MarketException(MarketErrorCode.TOKEN_NOT_FOUND);


        if (time == null || time.isBlank())
            throw new MarketException(MarketErrorCode.INVALID_PERIOD);

        boolean exists = tokenRepository.existsByTokenId(tokenId);

        if (!exists)
            throw new MarketException(MarketErrorCode.TOKEN_NOT_FOUND);

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
                throw new MarketException(MarketErrorCode.INVALID_PERIOD);
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

    public List<TradeHistoryResponse> responsePriceHistory(Long memberUid, Long tokenId, String time) {

        if (memberUid == null)
            throw new MarketException(MarketErrorCode.MEMBER_NOT_FOUND);

        if (tokenId == null)
            throw new MarketException(MarketErrorCode.TOKEN_NOT_FOUND);

        if (time == null || time.isBlank())
            throw new MarketException(MarketErrorCode.INVALID_PERIOD);

        Date end = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(end);

        switch (time) {
            case "1h":
                cal.add(Calendar.HOUR_OF_DAY, -1);
                break;

            case "5h":
                cal.add(Calendar.HOUR_OF_DAY, -5);
                break;

            case "1d":
                cal.add(Calendar.HOUR_OF_DAY, -24);
                break;

            default:
                throw new MarketException(MarketErrorCode.INVALID_PERIOD);
        }

        Date start = cal.getTime();

        return tradeHistoryRepository.findByMemberUidAndTokenIdAndTradedAtGreaterThanEqualOrderByTradedAtAsc(memberUid, tokenId, start)
                .stream()
                .map(
                    token -> token.toTradeHistoryResponse(
                            token.getTradeId(),
                            token.getMemberUid(),
                            token.getTokenId(),
                            token.getTradePrice(),
                            token.getQuantity(),
                            token.getTradedAt(),
                            token.getTradeType()
                    )
                )
                .toList();
    }

}