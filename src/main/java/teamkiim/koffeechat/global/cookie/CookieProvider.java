package teamkiim.koffeechat.global.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

/**
 * 쿠키를 설정하는 클래스
 */
@Component
@Slf4j
public class CookieProvider {

    @Value("${jwt.access.exp}")
    private long accessTokenExpTime;
    @Value("${jwt.refresh.exp}")
    private long refreshTokenExpTime;

    private static final String accessTokenName = "Authorization";
    private static final String refreshTokenName = "refresh-token";

    /**
     * 쿠키를 생성해서 HttpServletResponse 객체에 set
     * @param name 토큰 이름 (Authorization or refresh-token)
     * @param value JWT 토큰 문자열
     * @param isLogout 로그아웃 로직인지 여부
     * @param response HttpServletResponse
     */
    public void setCookie(String name, String value, boolean isLogout, HttpServletResponse response){

        long expTime = 0;

        if(!isLogout && name.equals(accessTokenName)){
            expTime = accessTokenExpTime / 1000;
        }
        else if(!isLogout && name.equals(refreshTokenName)){
            expTime = refreshTokenExpTime / 1000;
        }

        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
//                .domain("")
                .maxAge(expTime)
                .httpOnly(true)
                .secure(false)
//                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 쿠키를 생성해서 ServerHttpResponse 객체에 set
     * @param name 토큰 이름 (Authorization or refresh-token)
     * @param value JWT 토큰 문자열
     * @param isLogout 로그아웃 로직인지 여부
     * @param response ServerHttpResponse
     */
    public void setCookie(String name, String value, boolean isLogout, ServerHttpResponse response){

        long expTime = 0;

        if(!isLogout && name.equals(accessTokenName)){
            expTime = accessTokenExpTime / 1000;
        }
        else if(!isLogout && name.equals(refreshTokenName)){
            expTime = refreshTokenExpTime / 1000;
        }

        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
//                .domain("")
                .maxAge(expTime)
                .httpOnly(true)
                .secure(false)
//                .sameSite("None")
                .build();

        response.getHeaders().add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * HTTP 요청의 쿠키에서 Access Token 파싱
     * @param request HttpServletRequest
     * @return Access Token
     */
    public String getAccessToken(HttpServletRequest request){

        String accessToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(accessTokenName)){
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if(accessToken == null){
            log.debug("CookieProvider.class / getAccessToken : 쿠키에 access-token이 없음");
        }

        return accessToken;
    }

    /**
     * HTTP 요청의 쿠키에서 Refresh Token 파싱
     * @param request HttpServletRequest
     * @return Refresh Token
     */
    public String getRefreshToken(HttpServletRequest request){

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(refreshTokenName)){
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if(refreshToken == null){
            log.debug("CookieProvider.class / getRefreshToken : 쿠키에 refresh-token이 없음");
        }

        return refreshToken;
    }
}
