package ZooStock.zoostock_backend.trade;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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