package ZooStock.zoostock_backend.member.dto;

import ZooStock.zoostock_backend.member.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MemberOverviewResponse {
    private String memberName;
    private Long point;
    private RoleType roleType;
}
