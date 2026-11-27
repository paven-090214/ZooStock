package ZooStock.zoostock_backend.admin;

import ZooStock.zoostock_backend.admin.dto.MemberAdminDTO;
import ZooStock.zoostock_backend.admin.exception.AdminErrorCode;
import ZooStock.zoostock_backend.admin.exception.AdminException;
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
                () -> new AdminException(AdminErrorCode.MEMBER_NOT_FOUND)
        );

        if (memberAdminDTO.memberPoint() == null ||
            memberAdminDTO.memberName() == null ||
                memberAdminDTO.memberName().isBlank() ||
                memberAdminDTO.role() == null
        ) {
            throw new AdminException(AdminErrorCode.EMPTY_INPUT);
        }

        if (memberAdminDTO.memberPoint() < 0)
            throw new AdminException(AdminErrorCode.NEGATIVE_POINT);

        if (!memberEntity.getMemberName().equals(memberAdminDTO.memberName()) &&
                memberRepository.existsByMemberName(memberAdminDTO.memberName()))
            throw new AdminException(AdminErrorCode.DUPLICATE_NAME);

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