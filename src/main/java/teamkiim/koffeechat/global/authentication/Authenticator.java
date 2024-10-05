package teamkiim.koffeechat.global.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import teamkiim.koffeechat.domain.aescipher.AESCipher;
import teamkiim.koffeechat.domain.auth.dto.TokenDto;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.global.cookie.CookieProvider;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.global.jwt.JwtTokenProvider;
import teamkiim.koffeechat.global.redis.util.RedisUtil;

@Component
@RequiredArgsConstructor
@Slf4j
public class Authenticator {

    private final CookieProvider cookieProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final AESCipher aesCipher;

    private static final String accessTokenName = "Authorization";
    private static final String refreshTokenName = "refresh-token";

    @Value("${jwt.refresh.exp}")
    private long refreshTokenExpTime;

    /**
     * HttpServletRequest 객체에서 JWT 토큰을 이용하여 인증을 진행하고 유효한 토큰을 리턴
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return accessToken
     */
    public String verify(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = cookieProvider.getAccessToken(request);

        String validAccessToken = null;

        if (accessToken == null) {
            String refreshToken = cookieProvider.getRefreshToken(request);

            if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            validAccessToken = jwtTokenProvider.createAccessTokenFromRefreshToken(refreshToken);

            cookieProvider.setCookie("Authorization", validAccessToken, false, response);

        } else {
            validAccessToken = jwtTokenProvider.validateAccessToken(accessToken, request);

            if (validAccessToken == null) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
        }

        return validAccessToken;
    }

    /**
     * ServerHttpRequest 객체에서 JWT 토큰을 이용하여 인증을 진행하고 유효한 토큰을 리턴
     *
     * @param request  ServerHttpRequest
     * @param response ServerHttpResponse
     * @return accessToken
     */
    public String verify(ServerHttpRequest request, ServerHttpResponse response) {

        HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();

        String accessToken = cookieProvider.getAccessToken(httpServletRequest);

        String validAccessToken = null;

        if (accessToken == null) {
            String refreshToken = cookieProvider.getRefreshToken(httpServletRequest);

            if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            validAccessToken = jwtTokenProvider.createAccessTokenFromRefreshToken(refreshToken);

            cookieProvider.setCookie("Authorization", validAccessToken, false, response);

        } else {
            validAccessToken = jwtTokenProvider.validateAccessToken(accessToken, httpServletRequest);

            if (validAccessToken == null) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
        }

        return validAccessToken;
    }

    /**
     * 회원 정보로 JWT 토큰을 생성하여 HttpServletResponse 객체에 set
     *
     * @param member Domain Member
     */
    public TokenDto authenticate(Member member) throws Exception {

        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberRole().toString(), aesCipher.encrypt(member.getId()));
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberRole().toString(), aesCipher.encrypt(member.getId()));

        // 레디스 세팅
        redisUtil.setData(refreshToken, "refresh-token", refreshTokenExpTime);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * accessToken, refreshToken을 만료시킴
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    public void invalidate(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = cookieProvider.getAccessToken(request);
        String refreshToken = cookieProvider.getRefreshToken(request);

        if (accessToken != null) {
            jwtTokenProvider.invalidateAccessToken(accessToken);
        }
        if (refreshToken != null) {
            jwtTokenProvider.invalidateRefreshToken(refreshToken);
        }

        cookieProvider.setCookie(accessTokenName, null, true, response);
        cookieProvider.setCookie(refreshTokenName, null, true, response);
    }

    /**
     * 인증이 완료된 유효한 accessToken에서 암호화된 memberId를 추출
     *
     * @param validAccessToken
     * @return memberId(PK)
     */
    public String getMemberIdFromValidAccessToken(String validAccessToken) {

        return jwtTokenProvider.getMemberPK(jwtTokenProvider.getTokenClaims(validAccessToken));
    }

}
