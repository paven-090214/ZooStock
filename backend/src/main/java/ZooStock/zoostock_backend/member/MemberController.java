package ZooStock.zoostock_backend.member;

import ZooStock.zoostock_backend.common.ApiResponse;
import ZooStock.zoostock_backend.global.code.BaseSuccessCode;
import ZooStock.zoostock_backend.global.code.GeneralSuccessCode;
import ZooStock.zoostock_backend.member.code.MemberSuccessCode;
import ZooStock.zoostock_backend.member.dto.MemberDTO;
import ZooStock.zoostock_backend.member.dto.MemberOverviewResponse;
import ZooStock.zoostock_backend.member.dto.MemberResponse;
import ZooStock.zoostock_backend.member.dto.TopMemberResponse;
import ZooStock.zoostock_backend.trade.dto.TokenHoldingResponse;
import ZooStock.zoostock_backend.trade.dto.TradeHistoryResponse;
import ZooStock.zoostock_backend.trade.TradeHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final TradeHistoryService tradeHistoryService;

    public MemberController(
            MemberService memberService,
            TradeHistoryService tradeHistoryService
    ) {
        this.memberService = memberService;
        this.tradeHistoryService = tradeHistoryService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MemberResponse>> signup(
            @RequestBody MemberDTO memberDTO
            ) {
        BaseSuccessCode code = MemberSuccessCode.SIGN_UP_SUCCESS;
        MemberResponse result = memberService.signup(memberDTO);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(
            @RequestBody MemberDTO memberDTO
    ) {
        BaseSuccessCode code = MemberSuccessCode.LOGIN_SUCCESS;
        String jwtToken = memberService.login(memberDTO);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, jwtToken));
    }

    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<List<TokenHoldingResponse>>> getMyPage(
            @RequestAttribute("memberUid") Long memberUid
    ) {
        BaseSuccessCode code = GeneralSuccessCode.REQUEST_SUCCESS;
        List<TokenHoldingResponse> result =
                memberService.getMyToken(memberUid);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }

    @GetMapping("/mypage/history")
    public ResponseEntity<ApiResponse<Page<TradeHistoryResponse>>> getMyHistory(
            @RequestAttribute("memberUid") Long memberUid,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        BaseSuccessCode code = GeneralSuccessCode.REQUEST_SUCCESS;
        Page<TradeHistoryResponse> result = tradeHistoryService.getMemberTokenList(memberUid, page);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }

    @GetMapping("/mypage/overview")
    public ResponseEntity<ApiResponse<MemberOverviewResponse>> overViewMember(
            @RequestAttribute("memberUid") Long memberUid
    ) {
        MemberOverviewResponse result =
                memberService.overViewMember(memberUid);
        BaseSuccessCode code =
                GeneralSuccessCode.REQUEST_SUCCESS;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }

    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<Page<TopMemberResponse>>> topMemberRanking(
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        BaseSuccessCode code =
                GeneralSuccessCode.REQUEST_SUCCESS;
        Page<TopMemberResponse> result = memberService.topMemberRanking(page);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }
}