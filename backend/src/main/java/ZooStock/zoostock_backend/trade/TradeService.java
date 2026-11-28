package ZooStock.zoostock_backend.trade;

import ZooStock.zoostock_backend.member.MemberEntity;
import ZooStock.zoostock_backend.member.MemberRepository;
import ZooStock.zoostock_backend.market.TokenEntity;
import ZooStock.zoostock_backend.market.TokenRepository;
import ZooStock.zoostock_backend.trade.dto.TradeResponse;
import ZooStock.zoostock_backend.trade.exception.TradeErrorCode;
import ZooStock.zoostock_backend.trade.exception.TradeException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Optional;

@Service
public class TradeService {

    private final TradeHistoryRepository tradeHistoryRepository;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final TokenHoldingRepository tokenHoldingRepository;

    public TradeService(
            TradeHistoryRepository tradeHistoryRepository,
            TokenRepository tokenRepository,
            MemberRepository memberRepository,
            TokenHoldingRepository tokenHoldingRepository
    ) {
        this.tradeHistoryRepository = tradeHistoryRepository;
        this.tokenRepository = tokenRepository;
        this.memberRepository = memberRepository;
        this.tokenHoldingRepository = tokenHoldingRepository;
    }

    @Transactional
    public TradeResponse sellToken(
            Long memberUid,
            Long tokenId,
            Long quantity
    ){
        if (quantity <= 0)
            throw new TradeException(TradeErrorCode.INVALID_QUANTITY);

        Optional<TokenEntity> tokenOptional = tokenRepository.findById(tokenId);
        Optional<MemberEntity> memberOptional = memberRepository.findById(memberUid);
        Optional<TokenHoldingEntity> tokenHoldingOptional =
                tokenHoldingRepository.findByMemberUidAndTokenId(memberUid, tokenId);
        TradeHistoryEntity tradeHistoryEntity = new TradeHistoryEntity();

        Date date = new Date();

        if (tokenHoldingOptional.isEmpty())
            throw new TradeException(TradeErrorCode.HOLDING_NOT_FOUND);

        if (memberOptional.isEmpty())
            throw new TradeException(TradeErrorCode.MEMBER_NOT_FOUND);

        TokenHoldingEntity tokenHoldingEntity = tokenHoldingOptional.get();
        MemberEntity memberEntity = memberOptional.get();
        Long holdingQuantity = tokenHoldingEntity.getQuantity();

        if (tokenOptional.isEmpty())
            throw new TradeException(TradeErrorCode.TOKEN_NOT_FOUND);

        if (holdingQuantity < quantity)
            throw new TradeException(TradeErrorCode.NOT_ENOUGH_TOKEN);

        TokenEntity tokenEntity = tokenOptional.get();
        Long tokenPrice = tokenEntity.getTokenPrice();

        memberEntity.setMemberPoint(memberEntity.getMemberPoint() + tokenPrice * quantity);
        holdingQuantity -= quantity;

        tradeHistoryEntity.setQuantity(quantity);
        tradeHistoryEntity.setTradedAt(date);
        tradeHistoryEntity.setTradePrice(tokenPrice);
        tradeHistoryEntity.setTokenId(tokenId);
        tradeHistoryEntity.setMemberUid(memberUid);
        tradeHistoryEntity.setTradeType(TradeType.SELL);
        tradeHistoryRepository.save(tradeHistoryEntity);

        if (holdingQuantity == 0) {
            tokenHoldingRepository.delete(tokenHoldingEntity);
        }
        else {
            tokenHoldingEntity.setQuantity(holdingQuantity);
            tokenHoldingRepository.save(tokenHoldingEntity);
        }

        memberRepository.save(memberEntity);
        return new TradeResponse(
                tokenId,
                tokenEntity.getTokenName(),
                tokenPrice,
                quantity,
                tradeHistoryEntity.getTradedAt()
        );

    }

    @Transactional
    public TradeResponse buyToken(
            Long memberUid,
            Long tokenId,
            Long quantity
    ) {
        if (quantity <= 0)
            throw new TradeException(TradeErrorCode.INVALID_QUANTITY);

        Optional<MemberEntity> memberEntity = memberRepository.findById(memberUid);
        Optional<TokenEntity> tokenEntity = tokenRepository.findById(tokenId);
        Optional<TokenHoldingEntity> optionalTokenHoldingEntity =
                tokenHoldingRepository.findByMemberUidAndTokenId(memberUid, tokenId);
        TradeHistoryEntity tradeHistoryEntity = new TradeHistoryEntity();

        if (tokenEntity.isEmpty())
            throw new TradeException(TradeErrorCode.TOKEN_NOT_FOUND);

        if (memberEntity.isEmpty())
            throw new TradeException(TradeErrorCode.MEMBER_NOT_FOUND);

        TokenEntity token = tokenEntity.get();
        MemberEntity member = memberEntity.get();
        Long total = quantity * token.getTokenPrice();
        BigDecimal avg = BigDecimal.valueOf(total / quantity);

        if (member.getMemberPoint() < total)
            throw new TradeException(TradeErrorCode.NOT_ENOUGH_POINT);

        member.setMemberPoint(member.getMemberPoint() - total);

        TokenHoldingEntity tokenHoldingEntity = null;

        if (optionalTokenHoldingEntity.isEmpty()) {
            tokenHoldingEntity = new TokenHoldingEntity();
            tokenHoldingEntity.setMemberUid(memberUid);
            tokenHoldingEntity.setTokenId(tokenId);
            tokenHoldingEntity.setQuantity(quantity);
            tokenHoldingEntity.setBuyPriceAvg(avg);
        } else {
            tokenHoldingEntity = optionalTokenHoldingEntity.get();
            BigDecimal oldAvg = tokenHoldingEntity.getBuyPriceAvg();
            BigDecimal oldQuantity = BigDecimal.valueOf(tokenHoldingEntity.getQuantity());
            BigDecimal newQuantity = oldQuantity.add(BigDecimal.valueOf(quantity));
            BigDecimal curTotal = BigDecimal.valueOf(total);
            // 기존 보유 원가와 이번 매수 금액의 합계를 새 보유 수량으로 나눔 <- codex
            BigDecimal newAvg = oldAvg.multiply(oldQuantity)
                    .add(curTotal)
                    .divide(newQuantity, 2, RoundingMode.HALF_UP);
            tokenHoldingEntity.setBuyPriceAvg(newAvg);
            tokenHoldingEntity.setQuantity(tokenHoldingEntity.getQuantity() + quantity);
        }

        Date date = new Date();

        tradeHistoryEntity.setMemberUid(memberUid);
        tradeHistoryEntity.setTokenId(tokenId);
        tradeHistoryEntity.setTradePrice(token.getTokenPrice());
        tradeHistoryEntity.setQuantity(quantity);
        tradeHistoryEntity.setTradeType(TradeType.BUY);
        tradeHistoryEntity.setTradedAt(date);

        tradeHistoryRepository.save(tradeHistoryEntity);
        tokenHoldingRepository.save(tokenHoldingEntity);

        return new TradeResponse(
                tokenId,
                token.getTokenName(),
                token.getTokenPrice(),
                quantity,
                tradeHistoryEntity.getTradedAt()
        );
    }
}
