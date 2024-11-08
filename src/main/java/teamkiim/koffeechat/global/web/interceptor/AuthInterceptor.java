package teamkiim.koffeechat.global.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.global.authentication.Authenticator;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final Authenticator authenticator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // preflight 요청 넘기기
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        log.info("[AuthInterceptor / preHandle] 요청 Method : {}, URI: {}", request.getMethod(), request.getRequestURI());

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);

        // @Auth가 없는 경우 인증 해당 인터셉터에서 인가 작업을 수행하지 않음.
        if (auth == null) {
            return true;
        }

        String validAccessToken = authenticator.verify(request, response);

        // 여기서 권한 확인하기
//        for (MemberRole memberRole : auth.role()) {
//            String requestRole = authenticator.getMemberRoleFromValidAccessToken(validAccessToken);
//            if(memberRole.toString().equals(requestRole))
//        }

        Long memberId = authenticator.getMemberIdFromValidAccessToken(validAccessToken);

        request.setAttribute("authenticatedMemberPK", memberId);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
