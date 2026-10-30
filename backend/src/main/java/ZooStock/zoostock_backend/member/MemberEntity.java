package ZooStock.zoostock_backend.member;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "member")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_uid")
    private Long memberUid;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "member_id")
    private String MemberId;

    @Column(name = "member_password")
    private String memberPassword;

    @Column(name = "member_point")
    private Long  memberPoint;

    @Column(name = "member_created_at")
    private Date MemberCreatedAt;

    public Long getMemberPoint() {
        return memberPoint;
    }
}
