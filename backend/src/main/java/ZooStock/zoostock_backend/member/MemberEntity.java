package ZooStock.zoostock_backend.member;

import ZooStock.zoostock_backend.admin.dto.MemberAdminDTO;
import ZooStock.zoostock_backend.member.dto.MemberDTO;
import ZooStock.zoostock_backend.member.dto.MemberOverviewResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@Entity
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_member_id",
                        columnNames = "member_id"
                )
        }
)
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_uid")
    private Long memberUid;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "member_password")
    private String memberPassword;

    @Column(name = "member_point")
    private Long  memberPoint;

    @Column(name = "member_created_at")
    private Date memberCreatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleType role;

    public MemberOverviewResponse toMemberOverviewResponse(
            String memberName,
            Long memberPoint,
            RoleType role
    ) {
        return new MemberOverviewResponse(
                memberName,
                memberPoint,
                role
        );
    }

    public MemberDTO toMemberResponse(
            Long memberUid,
            String memberName,
            String memberId,
            String memberPassword,
            Long memberPoint,
            Date memberCreatedAt,
            RoleType role
    ) {
        return new MemberDTO (
                memberUid,
                memberName,
                memberId,
                memberPassword,
                memberPoint,
                memberCreatedAt,
                role
        );
    }

    public MemberAdminDTO toMemberAdminVer(
            Long memberUid,
            String memberName,
            String memberId,
            Long memberPoint,
            Date memberCreatedAt,
            RoleType role
    ) {
        return new MemberAdminDTO(
                memberUid,
                memberName,
                memberId,
                memberPoint,
                memberCreatedAt,
                role
        );
    }


}
