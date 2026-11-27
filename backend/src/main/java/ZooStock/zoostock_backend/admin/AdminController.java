package ZooStock.zoostock_backend.admin;

import ZooStock.zoostock_backend.admin.dto.MemberAdminDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin/members")
    public ResponseEntity<List<MemberAdminDTO>> adminPage() {
        return ResponseEntity.ok(adminService.adminPage());
    }

    @PutMapping("/admin/members/{uid}/set")
    public  ResponseEntity<MemberAdminDTO> setMember(
            @PathVariable Long uid,
            @RequestBody MemberAdminDTO request
            ) {
        MemberAdminDTO response = adminService.setMember(uid, request);
        return ResponseEntity.ok(response);
    }
}
