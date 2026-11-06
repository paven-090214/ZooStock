package ZooStock.zoostock_backend.trade;

import ZooStock.zoostock_backend.holding.TokenHoldingEntity;
import ZooStock.zoostock_backend.holding.TokenHoldingRepository;
import ZooStock.zoostock_backend.member.MemberDTO;
import ZooStock.zoostock_backend.member.MemberEntity;
import ZooStock.zoostock_backend.member.MemberRepository;
import ZooStock.zoostock_backend.token.TokenDTO;
import ZooStock.zoostock_backend.token.TokenEntity;
import ZooStock.zoostock_backend.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/trade/{tokenId}/buy")
    public ResponseEntity<String> buyToken(
            @PathVariable Long tokenId,
            @RequestParam Long quantity,
            @RequestAttribute("memberUid") Long memberUid
    ) {
        tradeService.buyToken(memberUid, tokenId, quantity);
        return ResponseEntity.ok("매수에 성공했습니다.");
    }

    @PostMapping("/trade/{tokenId}/sell")
    public ResponseEntity<String> sellToken(
            @PathVariable Long tokenId,
            @RequestParam Long quantity,
            HttpServletRequest request
    ) {
        Long memberUid = (Long) request.getAttribute("memberUid");
        tradeService.sellToken(memberUid, tokenId, quantity);
        return ResponseEntity.ok("매도에 성공했습니다.");
    }
}
