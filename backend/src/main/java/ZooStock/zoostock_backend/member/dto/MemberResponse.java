package ZooStock.zoostock_backend.member.dto;

public record MemberResponse (
        Long memberUid,
        String memberId,
        String memberName
){
}
