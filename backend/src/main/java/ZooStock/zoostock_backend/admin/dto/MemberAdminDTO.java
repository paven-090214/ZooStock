package ZooStock.zoostock_backend.admin.dto;

import ZooStock.zoostock_backend.member.RoleType;
import java.util.Date;

public record MemberAdminDTO(
        Long memberUid,
        String memberName,
        String memberId,
        Long memberPoint,
        Date memberCreatedAt,
        RoleType role
) {
}
