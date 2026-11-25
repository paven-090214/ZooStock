package ZooStock.zoostock_backend.admin;

import ZooStock.zoostock_backend.member.MemberRepository;
import ZooStock.zoostock_backend.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final MemberRepository memberRepository;

    public List<MemberDTO> adminPage() {
        return memberRepository.findAll()
                .stream()
                .map(
                        member -> member.toMemberResponse(
                                member.getMemberUid(),
                                member.getMemberName(),
                                member.getMemberId(),
                                member.getMemberPassword(),
                                member.getMemberPoint(),
                                member.getMemberCreatedAt(),
                                member.getRole()
                        )
                )
                .toList();
    }
}
