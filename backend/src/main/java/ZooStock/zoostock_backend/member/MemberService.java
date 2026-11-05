package ZooStock.zoostock_backend.member;

import ZooStock.zoostock_backend.jwt.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public MemberService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider
            ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public void signup(MemberDTO memberDTO) {
        boolean existsId = memberRepository.existsByMemberId(memberDTO.getMemberId());
        boolean existsName = memberRepository.existsByMemberName(memberDTO.getMemberName());

        if (existsId) {
            throw new IllegalArgumentException("이미 존재하는 아이디 입니다.");
        }

        if (existsName) {
            throw new IllegalArgumentException("이미 존재하는 별명 입니다.");
        }

        if (
                memberDTO.getMemberId() == null ||
                        memberDTO.getMemberId().isBlank() ||
                        memberDTO.getMemberPassword() == null ||
                        memberDTO.getMemberPassword().isBlank() ||
                        memberDTO.getMemberName() == null ||
                        memberDTO.getMemberName().isBlank()
        ) {
            throw new IllegalArgumentException("칸을 비울 수 없습니다.");
        }

        MemberEntity memberEntity = new MemberEntity();
        Date date = new Date();

        memberEntity.setMemberPassword(passwordEncoder.encode(memberDTO.getMemberPassword()));
        memberEntity.setMemberId(memberDTO.getMemberId());
        memberEntity.setMemberCreatedAt(date);
        memberEntity.setMemberName(memberDTO.getMemberName());
        memberEntity.setMemberPoint(10000L);
        memberRepository.save(memberEntity);
    }

    @Transactional
    public String login(MemberDTO memberDTO) {

        Optional<MemberEntity> memberOptional =
                memberRepository.findByMemberId(memberDTO.getMemberId());
        if (memberOptional.isEmpty()) {
            throw new IllegalArgumentException("아이디가 존재하지 않습니다.");
        }

        if (
                memberDTO.getMemberId() == null ||
                        memberDTO.getMemberId().isBlank() ||
                        memberDTO.getMemberPassword() == null ||
                        memberDTO.getMemberPassword().isBlank()
        ) {
            throw new IllegalArgumentException("값이 비어있을 수 없습니다.");
        }

        MemberEntity memberEntity = memberOptional.get();
        boolean existsPassword =
                passwordEncoder.matches(
                        memberDTO.getMemberPassword(),
                        memberEntity.getMemberPassword()
                );


        if (existsPassword) {
            return jwtProvider.createAccessToken(memberEntity.getMemberUid());
        }
        else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}