package ZooStock.zoostock_backend.trade;

import ZooStock.zoostock_backend.holding.TokenHoldingEntity;
import ZooStock.zoostock_backend.holding.TokenHoldingRepository;
import ZooStock.zoostock_backend.member.MemberDTO;
import ZooStock.zoostock_backend.member.MemberEntity;
import ZooStock.zoostock_backend.member.MemberRepository;
import ZooStock.zoostock_backend.token.TokenDTO;
import ZooStock.zoostock_backend.token.TokenEntity;
import ZooStock.zoostock_backend.token.TokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@RestController
public class TradeController {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final TokenHoldingRepository tokenHoldingRepository;

    public TradeController(
            TokenRepository tokenRepository,
            MemberRepository memberRepository,
            TokenHoldingRepository tokenHoldingRepository
    ) {
        this.tokenRepository = tokenRepository;
        this.memberRepository = memberRepository;
        this.tokenHoldingRepository = tokenHoldingRepository;
    }

    @PostMapping("/trade/{tokenId}/buy")
    public ResponseEntity<String> buyToken(
            @PathVariable Long tokenId,
            @RequestParam Long quantity
    ) {
        if (quantity <= 0) {
            return ResponseEntity.badRequest().body("매수 수량은 1주 이상이여야 합니다.");
        }

        // 로그인 기능 구현 후 적용 예정
        Optional<MemberEntity> memberEntity = memberRepository.findById(memberUid);
        Optional<TokenEntity> tokenEntity = tokenRepository.findById(tokenId);
        Optional<TokenHoldingEntity> OptionalTokenHoldingEntity =
                tokenHoldingRepository.findByMemberUidAndTokenId(memberUid, tokenId);

        if (tokenEntity.isEmpty() || memberEntity.isEmpty()) {
            return ResponseEntity.badRequest().body("토큰 혹은 사용자 초기화 오류");
        }
        TokenEntity token = tokenEntity.get();
        MemberEntity member = memberEntity.get();
        Long total = quantity * token.getTokenPrice();
        BigDecimal avg = BigDecimal.valueOf(total/quantity);

        if (member.getMemberPoint() < total) {
            return ResponseEntity.badRequest().body("포인트가 부족합니다.");
        }

        member.setMemberPoint(member.getMemberPoint() - total);

        TokenHoldingEntity tokenHoldingEntity = null;

        if (OptionalTokenHoldingEntity.isEmpty()) {
            tokenHoldingEntity = new TokenHoldingEntity();
            tokenHoldingEntity.setMemberUid(memberUid);
            tokenHoldingEntity.setTokenId(tokenId);
            tokenHoldingEntity.setQuantity(quantity);
            tokenHoldingEntity.setBuyPriceAvg(avg);
        }
        else {
           tokenHoldingEntity = OptionalTokenHoldingEntity.get();
            BigDecimal oldAvg = tokenHoldingEntity.getBuyPriceAvg();
            BigDecimal oldQuantity = BigDecimal.valueOf(tokenHoldingEntity.getQuantity());
            BigDecimal newQuantity = oldQuantity.add(BigDecimal.valueOf(quantity));
            BigDecimal curTotal = BigDecimal.valueOf(total);
            // 기존 보유 원가와 이번 매수 금액의 합계를 새 보유 수량으로 나눔
            BigDecimal newAvg = oldAvg.multiply(oldQuantity)
                    .add(curTotal)
                    .divide(newQuantity, 2, RoundingMode.HALF_UP);
            tokenHoldingEntity.setBuyPriceAvg(newAvg);
            tokenHoldingEntity.setQuantity(tokenHoldingEntity.getQuantity() + quantity);
        }

        tokenHoldingRepository.save(tokenHoldingEntity);
        return ResponseEntity.ok("매수에 성공했습니다.");
    }
}
