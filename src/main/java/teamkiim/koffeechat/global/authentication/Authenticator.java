package teamkiim.koffeechat.global.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import teamkiim.koffeechat.global.cookie.CookieProvider;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.global.jwt.JwtTokenProvider;

@Component
@RequiredArgsConstructor
@Slf4j
public class Authenticator {

    private final CookieProvider cookieProvider;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * HttpServletRequest 객체에서 JWT 토큰을 이용하여 인증을 진행하고 유효한 토큰을 리턴
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return accessToken
     */
    public String authenticate(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = cookieProvider.getAccessToken(request);

        String validAccessToken = null;

        if(accessToken == null){
            String refreshToken = cookieProvider.getRefreshToken(request);

            if(!jwtTokenProvider.validateRefreshToken(refreshToken)) throw new CustomException(ErrorCode.UNAUTHORIZED);

            validAccessToken = jwtTokenProvider.createAccessTokenFromRefreshToken(refreshToken);

            cookieProvider.setCookie("Authorization", validAccessToken, false, response);

        }
        else{
            validAccessToken = jwtTokenProvider.validateAccessToken(accessToken, request);

            if(validAccessToken == null) throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return validAccessToken;
    }

    /**
     * ServerHttpRequest 객체에서 JWT 토큰을 이용하여 인증을 진행하고 유효한 토큰을 리턴
     * @param request ServerHttpRequest
     * @param response ServerHttpResponse
     * @return accessToken
     */
    public String authenticate(ServerHttpRequest request, ServerHttpResponse response) {

        HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();

        String accessToken = cookieProvider.getAccessToken(httpServletRequest);

        String validAccessToken = null;

        if(accessToken == null){
            String refreshToken = cookieProvider.getRefreshToken(httpServletRequest);

            if(!jwtTokenProvider.validateRefreshToken(refreshToken)) throw new CustomException(ErrorCode.UNAUTHORIZED);

            validAccessToken = jwtTokenProvider.createAccessTokenFromRefreshToken(refreshToken);

            cookieProvider.setCookie("Authorization", validAccessToken, false, response);

        }
        else{
            validAccessToken = jwtTokenProvider.validateAccessToken(accessToken, httpServletRequest);

            if(validAccessToken == null) throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return validAccessToken;
    }

    /**
     * 인증이 완료된 유효한 accessToken에서 memberId를 추출
     * @param validAccessToken
     * @return memberId(PK)
     */
    public Long getMemberIdFromValidAccessToken(String validAccessToken){

        return jwtTokenProvider.getMemberPK(jwtTokenProvider.getTokenClaims(validAccessToken));
    }

}
