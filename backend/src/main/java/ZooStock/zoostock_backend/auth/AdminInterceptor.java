package ZooStock.zoostock_backend.auth;

import ZooStock.zoostock_backend.admin.exception.AdminErrorCode;
import ZooStock.zoostock_backend.admin.exception.AdminException;
import ZooStock.zoostock_backend.member.MemberEntity;
import ZooStock.zoostock_backend.member.MemberRepository;
import ZooStock.zoostock_backend.member.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        Long memberUid = (Long) request.getAttribute("memberUid");

        MemberEntity member = memberRepository.findById(memberUid).orElseThrow(
                () -> new AdminException(AdminErrorCode.FORBIDDEN)
        );

        if (member.getRole() != RoleType.ADMIN)
            throw new AdminException(AdminErrorCode.FORBIDDEN);
        else
            return true;
    }
}
