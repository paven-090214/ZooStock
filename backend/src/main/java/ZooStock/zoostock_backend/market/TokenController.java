package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.common.ApiResponse;
import ZooStock.zoostock_backend.global.code.BaseSuccessCode;
import ZooStock.zoostock_backend.global.code.GeneralSuccessCode;
import ZooStock.zoostock_backend.market.dto.OverviewResponse;
import ZooStock.zoostock_backend.market.dto.PriceHistoryResponse;
import ZooStock.zoostock_backend.market.dto.TokenDTO;
import ZooStock.zoostock_backend.trade.dto.TradeHistoryResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<List<TokenDTO>>> searchToken(
            @RequestParam("name") String tokenName
    ) {
        BaseSuccessCode code = GeneralSuccessCode.REQUEST_SUCCESS;
        List<TokenDTO> response = tokenService.searchToken(tokenName);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, response));
    }

    @GetMapping("/token/search/{id}")
    public ResponseEntity<ApiResponse<TokenDTO>> findToken(
            @PathVariable Long id
    ) {
        BaseSuccessCode code = GeneralSuccessCode.REQUEST_SUCCESS;
        TokenDTO result = tokenService.findToken(id);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }

    @GetMapping("/token/{id}")
    public ResponseEntity<ApiResponse<List<PriceHistoryResponse>>> getTokenHistory (
            @PathVariable Long id,
            @RequestParam("history") String time
    ) {
        BaseSuccessCode code = GeneralSuccessCode.REQUEST_SUCCESS;
        List<PriceHistoryResponse> result = priceHistoryService.getTokenHistory(id, time);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }

    @GetMapping("/token")
    public ResponseEntity<ApiResponse<OverviewResponse>> getDailyRankingResponse() {
        OverviewResponse response = new OverviewResponse(
                priceHistoryService.getDailyChangeRanking(),
                priceHistoryService.getRandomTokens()
        );
        BaseSuccessCode code = GeneralSuccessCode.REQUEST_SUCCESS;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, response));
    }

    @GetMapping("/token/all")
    public ResponseEntity<ApiResponse<Page<TokenDTO>>> findAllToken(
            @RequestParam(value = "sort", defaultValue = "nameAsc") String sortValue,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        BaseSuccessCode code = GeneralSuccessCode.REQUEST_SUCCESS;
        Page<TokenDTO> result = tokenService.findAllToken(sortValue, page);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/token/history/{id}")
    public ResponseEntity<ApiResponse<List<TradeHistoryResponse>>> responsePriceHistory(
            @PathVariable Long id,
            @RequestAttribute(value = "memberUid") Long memberUid,
            @RequestParam(value = "history") String time
            ) {
        BaseSuccessCode code = GeneralSuccessCode.REQUEST_SUCCESS;
        List<TradeHistoryResponse> result = priceHistoryService.responsePriceHistory(memberUid, id, time);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }
}