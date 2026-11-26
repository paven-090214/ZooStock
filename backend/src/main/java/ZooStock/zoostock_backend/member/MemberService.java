package ZooStock.zoostock_backend.member;

import ZooStock.zoostock_backend.auth.JwtProvider;
import ZooStock.zoostock_backend.market.TokenEntity;
import ZooStock.zoostock_backend.market.TokenRepository;
import ZooStock.zoostock_backend.member.dto.MemberDTO;
import ZooStock.zoostock_backend.member.dto.MemberOverviewResponse;
import ZooStock.zoostock_backend.member.dto.TopMemberResponse;
import ZooStock.zoostock_backend.trade.TokenHoldingRepository;
import ZooStock.zoostock_backend.trade.dto.TokenHoldingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenHoldingRepository tokenHoldingRepository;
    private final TokenRepository tokenRepository;

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
        memberEntity.setMemberPoint(50000L);
        memberEntity.setRole(RoleType.USER);
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

    public List<TokenHoldingResponse> getMyToken(Long memberUid) {
        if (memberUid == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정보를 찾을 수 없습니다.");

        return tokenHoldingRepository.findByMemberUid(memberUid)
                .stream()
                .map(
                        token -> {
                            Long currentPrice = tokenRepository.findByTokenId(token.getTokenId())
                                    .map(TokenEntity::getTokenPrice).orElse(null);
                            return token.toTokenHoldingResponse(
                                    token.getHoldingId(),
                                    token.getMemberUid(),
                                    token.getTokenId(),
                                    token.getQuantity(),
                                    token.getBuyPriceAvg(),
                                    token.calculateProfitRate(currentPrice)
                            );
                        }
                )
                .toList();
    }

    public MemberOverviewResponse OverViewMember(Long memberUid) {

        if (memberUid == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "회원 인증이 되지 않았습니다.");

        return memberRepository.findById(memberUid)
                .map(
                        member -> member.toMemberOverviewResponse(
                                member.getMemberName(),
                                member.getMemberPoint(),
                                member.getRole()
                        )
                )
                .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "회원 조회 실패")
        );
    }

    public Page<TopMemberResponse> topMemberRanking(int page) {
        if (page <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 페이지 입니다.");

        Pageable pageable = PageRequest.of(page - 1, 10);
        return memberRepository.findTopMember(pageable);
    }
}