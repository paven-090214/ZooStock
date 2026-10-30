package ZooStock.zoostock_backend.member;

import java.util.Date;

public class MemberDTO {
    private Long memberUid;
    private String memberName;
    private String MemberId;
    private String MemberPassword;
    private Long MemberPoint;
    private Date MemberCreatedAt;

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
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getMemberPassword() {
        return MemberPassword;
    }

    public void setMemberPassword(String memberPassword) {
        MemberPassword = memberPassword;
    }

    public Long getMemberPoint() {
        return MemberPoint;
    }

    public void setMemberPoint(Long memberPoint) {
        MemberPoint = memberPoint;
    }

    public Date getMemberCreatedAt() {
        return MemberCreatedAt;
    }

    public void setMemberCreatedAt(Date memberCreatedAt) {
        MemberCreatedAt = memberCreatedAt;
    }
}