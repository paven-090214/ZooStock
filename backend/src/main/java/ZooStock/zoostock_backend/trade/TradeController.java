package ZooStock.zoostock_backend.trade;

import ZooStock.zoostock_backend.common.ApiResponse;
import ZooStock.zoostock_backend.global.code.BaseSuccessCode;
import ZooStock.zoostock_backend.trade.code.TradeSuccessCode;
import ZooStock.zoostock_backend.trade.dto.TradeResponse;
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
    public ResponseEntity<ApiResponse<TradeResponse>> buyToken(
            @PathVariable Long tokenId,
            @RequestParam Long quantity,
            @RequestAttribute("memberUid") Long memberUid
    ) {
        TradeResponse result = tradeService.buyToken(memberUid, tokenId, quantity);
        BaseSuccessCode code = TradeSuccessCode.BUY_SUCCESS;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }

    @PostMapping("/trade/{tokenId}/sell")
    public ResponseEntity<ApiResponse<TradeResponse>> sellToken(
            @PathVariable Long tokenId,
            @RequestParam Long quantity,
            HttpServletRequest request
    ) {
        Long memberUid = (Long) request.getAttribute("memberUid");
        TradeResponse result = tradeService.sellToken(memberUid, tokenId, quantity);
        BaseSuccessCode code = TradeSuccessCode.SELL_SUCCESS;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }
}