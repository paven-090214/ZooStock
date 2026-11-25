package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.market.dto.OverviewResponse;
import ZooStock.zoostock_backend.market.dto.PriceHistoryResponse;
import ZooStock.zoostock_backend.market.dto.TokenDTO;
import ZooStock.zoostock_backend.trade.dto.TradeHistoryResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;

@RestController
public class TokenController {

    private final TokenService tokenService;
    private final PriceHistoryService priceHistoryService;

    public TokenController (
            TokenService tokenService,
            PriceHistoryService priceHistoryService
    ) {
        this.tokenService = tokenService;
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping("/token/search")
    public ResponseEntity<List<TokenDTO>> searchToken(
            @RequestParam("name") String tokenName
    ) {
        List<TokenDTO> response = tokenService.searchToken(tokenName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/token/search/{id}")
    public ResponseEntity<TokenDTO> findToken(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(tokenService.findToken(id));
    }

    @GetMapping("/token/{id}")
    public ResponseEntity<List<PriceHistoryResponse>> getTokenHistory (
            @PathVariable Long id,
            @RequestParam("history") String time
    ) {
        return ResponseEntity.ok(priceHistoryService.getTokenHistory(id, time));
    }

    @GetMapping("/token")
    public ResponseEntity<OverviewResponse> getDailyRankingResponse() {
        OverviewResponse response = new OverviewResponse(
                priceHistoryService.getDailyChangeRanking(),
                priceHistoryService.getRandomTokens()
        );
        return ResponseEntity.ok(response);
    }


    @GetMapping("/token/all")
    public ResponseEntity<Page<TokenDTO>> findAllToken(
            @RequestParam(value = "sort", defaultValue = "nameAsc") String sortValue,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        return ResponseEntity.ok(tokenService.findAllToken(sortValue, page));
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/token/history/{id}")
    public ResponseEntity<List<TradeHistoryResponse>> responsePriceHistory(
            @PathVariable Long id,
            @RequestAttribute(value = "memberUid") Long memberUid,
            @RequestParam(value = "history") String time
            ) {
        return ResponseEntity.ok(priceHistoryService.responsePriceHistory(memberUid, id, time));
    }
}