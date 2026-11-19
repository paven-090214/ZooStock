package ZooStock.zoostock_backend.member;

import ZooStock.zoostock_backend.member.dto.MemberDTO;
import ZooStock.zoostock_backend.trade.dto.TokenHoldingResponse;
import ZooStock.zoostock_backend.trade.dto.TradeHistoryResponse;
import ZooStock.zoostock_backend.trade.TradeHistoryService;
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
    public ResponseEntity<String> signup(
            @RequestBody MemberDTO memberDTO
            ) {
        memberService.signup(memberDTO);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody MemberDTO memberDTO
    ) {
        String jwtToken = memberService.login(memberDTO);
        return ResponseEntity.ok(jwtToken);
    }

    @GetMapping("/mypage")
    public ResponseEntity<List<TokenHoldingResponse>> getMyPage(
            @RequestAttribute("memberUid") Long memberUid
    ) {
        return ResponseEntity.ok(
                memberService.getMyToken(memberUid)
        );
    }

    @GetMapping("/mypage/history")
    public ResponseEntity<List<TradeHistoryResponse>> getMyHistory(
            @RequestAttribute("memberUid") Long memberUid
    ) {
        return ResponseEntity.ok(
                tradeHistoryService.getMemebrTokenList(memberUid)
        );
    }
}