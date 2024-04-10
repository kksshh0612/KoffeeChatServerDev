package teamkiim.koffeechat.global.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

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

    /*
    response에 쿠키를 만들어 Add
     */
    public void setCookie(String name, String value, boolean isLogout, HttpServletResponse response){

        long expTime = 0;

        if(!isLogout && name.equals("Authorization")){
            expTime = accessTokenExpTime / 1000;
        }
        else if(!isLogout && name.equals("refresh-token")){
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

    /*
    HTTP 요청의 쿠키에서 refresh 토큰 파싱
     */
    public String getAccessToken(HttpServletRequest request){

        String accessToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("Authorization")){
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

    /*
    HTTP 요청의 쿠키에서 refresh 토큰 파싱
     */
    public String getRefreshToken(HttpServletRequest request){

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("refresh-token")){
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
