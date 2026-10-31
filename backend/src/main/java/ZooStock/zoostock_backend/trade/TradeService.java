package ZooStock.zoostock_backend.trade;

import ZooStock.zoostock_backend.holding.TokenHoldingEntity;
import ZooStock.zoostock_backend.holding.TokenHoldingRepository;
import ZooStock.zoostock_backend.member.MemberEntity;
import ZooStock.zoostock_backend.member.MemberRepository;
import ZooStock.zoostock_backend.token.TokenEntity;
import ZooStock.zoostock_backend.token.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
    public void sellToken(
            Long memberUid,
            Long tokenId,
            Long quantity
    ){
        if (quantity <= 0) {
            throw new IllegalArgumentException("매도 수량은 1주 이상이여야 합니다.");
        }
        Optional<TokenEntity> tokenOptional = tokenRepository.findById(tokenId);
        Optional<MemberEntity> memberOptional = memberRepository.findByIdForUpdate(memberUid);
        Optional<TokenHoldingEntity> tokenHoldingOptional =
                tokenHoldingRepository.findByMemberUidAndTokenId(memberUid, tokenId);
        TradeHistoryEntity tradeHistoryEntity = new TradeHistoryEntity();

        Date date = new Date();

        if (tokenHoldingOptional.isEmpty() || memberOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자 또는 보유하고 있지 않은 토큰 입니다.");
        }

        TokenHoldingEntity tokenHoldingEntity = tokenHoldingOptional.get();
        MemberEntity memberEntity = memberOptional.get();
        Long holdingQuantity = tokenHoldingEntity.getQuantity();

        if (tokenOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 토큰입니다.");
        }

        if (holdingQuantity < quantity) {
             throw new IllegalArgumentException("코인이 부족합니다.");
         }

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
    }

    @Transactional
    public void buyToken(
            Long memberUid,
            Long tokenId,
            Long quantity
    ) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("매수 수량은 1주 이상이여야 합니다.");
        }

        // 로그인 기능 구현 후 적용 예정
        Optional<MemberEntity> memberEntity = memberRepository.findByIdForUpdate(memberUid);
        Optional<TokenEntity> tokenEntity = tokenRepository.findById(tokenId);
        Optional<TokenHoldingEntity> optionalTokenHoldingEntity =
                tokenHoldingRepository.findByMemberUidAndTokenId(memberUid, tokenId);
        TradeHistoryEntity tradeHistoryEntity = new TradeHistoryEntity();

        if (tokenEntity.isEmpty() || memberEntity.isEmpty()) {
            throw new IllegalArgumentException("토큰 혹은 사용자 초기화 오류");
        }
        TokenEntity token = tokenEntity.get();
        MemberEntity member = memberEntity.get();
        Long total = quantity * token.getTokenPrice();
        BigDecimal avg = BigDecimal.valueOf(total / quantity);

        if (member.getMemberPoint() < total) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

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
    }
}
