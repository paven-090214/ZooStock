package ZooStock.zoostock_backend.admin;

import ZooStock.zoostock_backend.admin.dto.MemberAdminDTO;
import ZooStock.zoostock_backend.member.MemberEntity;
import ZooStock.zoostock_backend.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final MemberRepository memberRepository;

    public List<MemberAdminDTO> adminPage() {
        return memberRepository.findAll()
                .stream()
                .map(
                        member -> member.toMemberAdminVer(
                                member.getMemberUid(),
                                member.getMemberName(),
                                member.getMemberId(),
                                member.getMemberPoint(),
                                member.getMemberCreatedAt(),
                                member.getRole()
                        )
                )
                .toList();
    }

    public MemberAdminDTO setMember(Long memberUid, MemberAdminDTO memberAdminDTO) {

        MemberEntity memberEntity = memberRepository.findById(memberUid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.")
        );

        if (memberAdminDTO.memberPoint() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "포인트는 음수가 될 수 없습니다.");

        if (!memberEntity.getMemberName().equals(memberAdminDTO.memberName()) &&
                memberRepository.existsByMemberName(memberAdminDTO.memberName()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이름은 중복될 수 없습니다.");

        memberEntity.setMemberName(memberAdminDTO.memberName());
        memberEntity.setMemberPoint(memberAdminDTO.memberPoint());
        memberEntity.setRole(memberAdminDTO.role());

        memberRepository.save(memberEntity);
        return memberEntity.toMemberAdminVer(
                memberUid,
                memberEntity.getMemberName(),
                memberEntity.getMemberId(),
                memberEntity.getMemberPoint(),
                memberEntity.getMemberCreatedAt(),
                memberEntity.getRole()
        );
    }
}