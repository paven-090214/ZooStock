package ZooStock.zoostock_backend.member;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Optional;

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

    public Long getMemberUid() {
        return memberUid;
    }

    public Long getMemberPoint() {
        return memberPoint;
    }

    public void setMemberPoint(Long memberPoint) {
        this.memberPoint = memberPoint;
    }

    public String getMemberPassword() {
        return memberPassword;
    }

    public void setMemberPassword(String memberPassword) {
        this.memberPassword = memberPassword;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberCreatedAt(Date memberCreatedAt) {
        this.memberCreatedAt = memberCreatedAt;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
