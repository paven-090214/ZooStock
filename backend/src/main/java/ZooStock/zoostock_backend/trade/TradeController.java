package ZooStock.zoostock_backend.trade;

import ZooStock.zoostock_backend.member.MemberDTO;
import ZooStock.zoostock_backend.member.MemberEntity;
import ZooStock.zoostock_backend.member.MemberRepository;
import ZooStock.zoostock_backend.token.TokenDTO;
import ZooStock.zoostock_backend.token.TokenEntity;
import ZooStock.zoostock_backend.token.TokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class TradeController {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    public TradeController(
            TokenRepository tokenRepository,
            MemberRepository memberRepository
    ) {
        this.tokenRepository = tokenRepository;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/trade/{tokenId}/buy")
    public void buyToken(
            @PathVariable Long tokenId,
            @RequestParam int quantity
    ) {
        if (quantity <= 0) {
            return ;
        }

        // 로그인 기능 구현 후 적용 예정
        Optional<MemberEntity> memberEntity = memberRepository.findById(memberUid);
        Optional<TokenEntity> tokenEntity = tokenRepository.findById(tokenId);

        if (tokenEntity.isEmpty() || memberEntity.isEmpty()) {
            return ;
        }
        TokenEntity token = tokenEntity.get();
        MemberEntity member = memberEntity.get();

        if (member.getMemberPoint() < quantity * token.getTokenPrice()) {
            return;
        }
    }
}
