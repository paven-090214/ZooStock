package ZooStock.zoostock_backend.admin;

import ZooStock.zoostock_backend.admin.dto.MemberAdminDTO;
import ZooStock.zoostock_backend.common.ApiResponse;
import ZooStock.zoostock_backend.global.code.BaseSuccessCode;
import ZooStock.zoostock_backend.global.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin/members")
    public ResponseEntity<ApiResponse<Page<MemberAdminDTO>>> adminPage(
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        Page<MemberAdminDTO> result = adminService.adminPage(page);
        BaseSuccessCode code = GeneralSuccessCode.REQUEST_SUCCESS;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, result));
    }

    @PutMapping("/admin/members/{uid}/set")
    public  ResponseEntity<ApiResponse<MemberAdminDTO>> setMember(
            @PathVariable Long uid,
            @RequestBody MemberAdminDTO request
            ) {
        BaseSuccessCode code = GeneralSuccessCode.REQUEST_SUCCESS;
        MemberAdminDTO response = adminService.setMember(uid, request);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onSuccess(code, response));
    }
}
