package ZooStock.zoostock_backend.member.dto;

import ZooStock.zoostock_backend.member.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private Long memberUid;
    private String memberName;
    private String memberId;
    private String memberPassword;
    private Long memberPoint;
    private Date memberCreatedAt;
    private RoleType role;
}