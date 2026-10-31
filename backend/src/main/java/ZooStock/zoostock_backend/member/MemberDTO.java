package ZooStock.zoostock_backend.member;

import java.util.Date;

public class MemberDTO {
    private Long memberUid;
    private String memberName;
    private String memberId;
    private String memberPassword;
    private Long memberPoint;
    private Date memberCreatedAt;

    public Long getMemberUid() {
        return memberUid;
    }

    public void setMemberUid(Long memberUid) {
        this.memberUid = memberUid;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberPassword() {
        return memberPassword;
    }

    public void setMemberPassword(String memberPassword) {
        this.memberPassword = memberPassword;
    }

    public Long getMemberPoint() {
        return memberPoint;
    }

    public void setMemberPoint(Long memberPoint) {
        this.memberPoint = memberPoint;
    }

    public Date getMemberCreatedAt() {
        return memberCreatedAt;
    }

    public void setMemberCreatedAt(Date memberCreatedAt) {
        this.memberCreatedAt = memberCreatedAt;
    }
}