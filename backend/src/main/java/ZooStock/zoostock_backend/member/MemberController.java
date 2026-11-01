package ZooStock.zoostock_backend.member;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(
            MemberService memberService
    ) {
        this.memberService = memberService;
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
        memberService.login(memberDTO);
        return ResponseEntity.ok("로그인 성공");
    }
}
