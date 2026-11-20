package ZooStock.zoostock_backend.auth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(
            JwtProvider jwtProvider
    ) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        //codex
        String path = request.getServletPath();
        String method = request.getMethod();

        // 회원가입·로그인은 JWT 없이 허용
        boolean publicAuth = "POST".equals(method)
                && (
                path.equals("/login")
                        || path.equals("/signup")
        );

        // 공개 조회만 허용
        boolean publicRead = "GET".equals(method)
                && (
                path.equals("/token/search")
                        || path.matches("/token/search/[0-9]+")
                        || path.matches("/board/[0-9]+")
                        || path.matches("/token/[0-9]+")
        );

        // Swagger 화면과 API 문서
        boolean swagger = "GET".equals(method)
                && (
                path.equals("/swagger-ui.html")
                        || path.startsWith("/swagger-ui/")
                        || path.equals("/v3/api-docs")
                        || path.startsWith("/v3/api-docs/")
        );

        // true: JWT 검증 생략 / false: 기존 JWT 검증 실행
        return publicAuth || publicRead || swagger;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader("authorization");

        if (authorization == null ||
                !authorization.startsWith("Bearer ")
        ) {
            response.sendError(401, "JWT가 없습니다.");
            return;
        }

        String memberToken = authorization.substring(7);

        try {
            Long memberUid = jwtProvider.getMemberUid(memberToken);
            request.setAttribute("memberUid", memberUid);
        } catch (JwtException | IllegalArgumentException e) {
            response.sendError(401, "JWT인증에 실패했습니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }

}