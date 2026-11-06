package ZooStock.zoostock_backend.jwt;

import ZooStock.zoostock_backend.member.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;

    public JwtProvider(
            SecretKey secretKey
    ) {
        this.secretKey = secretKey;
    }

    public String createAccessToken(Long memberUid) {
        Date now = new Date();

        return Jwts.builder() // 토큰 생성
                .subject(memberUid.toString()) // payload에 표시할 식별자
                .issuedAt(now) // 발급 날짜
                .expiration(new Date(now.getTime() + 3600000L)) // 만료 날짜, 1시간 후
                .signWith(secretKey, Jwts.SIG.HS256)
                // secretKey : 사용할 때 사용할 서버의 비밀키
                // Jwts : JJWT 라이브러리 클래스
                // SIG : JWT 내부의 서명 알고리즘 관련 클래스
                // HS256 : HMAC-SHA256 서명 알고리즘을 나타내는 상수
                .compact(); // JWT를 최종적으로 문자열로 생성
    }

    public Long getMemberUid(String memberToken) {
        Jws<Claims> parsedToken = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(memberToken);

        String memberUid =  parsedToken.getPayload().getSubject();
        return Long.parseLong(memberUid);
    }
}