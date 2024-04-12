package teamkiim.koffeechat.global.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.global.cookie.CookieProvider;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.global.jwt.JwtTokenProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final CookieProvider cookieProvider;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(!(handler instanceof HandlerMethod)) return true;

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);

        // @Auth가 없는 경우 인증 해당 인터셉터에서 인증 수행하지 않음.
        if(auth == null) return true;

        String accessToken = cookieProvider.getAccessToken(request);
        String validAccessToken = null;

        if(accessToken == null){
            String refreshToken = cookieProvider.getRefreshToken(request);

            if(jwtTokenProvider.validateRefreshToken(refreshToken)){
                validAccessToken = jwtTokenProvider.createAccessTokenFromRefreshToken(refreshToken);
                cookieProvider.setCookie("Authorization", validAccessToken, false, response);
            }
            else{
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
        }
        else{
            validAccessToken = jwtTokenProvider.validateAccessToken(accessToken, request);

            if(validAccessToken == null){
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
        }

        Long memberId = jwtTokenProvider.getMemberPK(jwtTokenProvider.getTokenClaims(validAccessToken));

        request.setAttribute("authenticatedMemberPK", memberId);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
