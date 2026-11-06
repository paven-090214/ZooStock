package ZooStock.zoostock_backend.filter;

import ZooStock.zoostock_backend.jwt.JwtProvider;
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
        String path = request.getServletPath();

        return path.equals("/login") ||
                path.equals("/signup") ||
                path.startsWith("/swagger-ui/") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/board/{boardId}");
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