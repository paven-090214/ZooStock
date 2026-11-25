package ZooStock.zoostock_backend.admin;

import ZooStock.zoostock_backend.member.dto.MemberDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin/members")
    public ResponseEntity<List<MemberDTO>> adminPage() {
        return ResponseEntity.ok(adminService.adminPage());
    }
}
