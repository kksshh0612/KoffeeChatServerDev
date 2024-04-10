package teamkiim.koffeechat.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import teamkiim.koffeechat.global.cookie.CookieProvider;
import teamkiim.koffeechat.global.redis.util.RedisUtil;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.MemberRole;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰을 생성/파싱/설정 하는 클래스
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider implements InitializingBean {

    private final RedisUtil redisUtil;
    private final CookieProvider cookieProvider;

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.access.exp}")
    private long accessTokenExpTime;
    @Value("${jwt.refresh.exp}")
    private long refreshTokenExpTime;

    private static final String AUTHORITIES_KEY = "auth";
    private Key key;

    /*
   빈이 생성되고 의존관계 주입까지 완료된 후, Key 변수에 값 할당
    */
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);     //생성자 주입으로 받은 secret 값을 Base64에 디코딩하여 key 변수에 할당
        this.key = Keys.hmacShaKeyFor(keyBytes);              //hmac 알고리즘을 이용하여 Key 인스턴스 생성
    }

    /*
    로그인한 사용자의 정보로 access 토큰 발급
     */
    public String createAccessToken(String role,  Long memberId){

        Date validity = new Date(System.currentTimeMillis() + accessTokenExpTime);    //현재시간 + 토큰 유효 시간 == 만료날짜

        return Jwts.builder()
                .setSubject(memberId.toString())
                .claim(AUTHORITIES_KEY, role)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /*
    로그인한 사용자의 정보로 refresh 토큰 발급
     */
    public String createRefreshToken(String role,  Long memberId){

        Date validity = new Date(System.currentTimeMillis() + refreshTokenExpTime);    //현재시간 + 토큰 유효 시간 == 만료날짜

        return Jwts.builder()
                .setSubject(memberId.toString())
                .claim(AUTHORITIES_KEY, role)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /*
    토큰에서 claim 정보 추출
     */
    public Claims getTokenClaims(String token){

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims;
    }

    /*
    Claim에서 PK 추출
     */
    public Long getMemberPK(Claims claims){

        return Long.parseLong(claims.getSubject());
    }

    /*
   유효한 access 토큰인지 확인 return이 null : 다시 로그인 / access토큰 : 다음 프로세스로 이동
    */
    public String validateAccessToken(String accessToken, HttpServletRequest request) {

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
            return accessToken;

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.debug("TokenProvider.class / validateAccessToken : 잘못된 JWT 서명");
            return null;

        } catch (ExpiredJwtException e) {
            log.debug("TokenProvider.class / validateAccessToken : 만료된 JWT access 토큰");

            String refreshToken = cookieProvider.getRefreshToken(request);

            //리프레쉬 토큰이 유효하면 DB 기존 값 지우고 엑세스 토큰 재발급
            if(refreshToken != null && validateRefreshToken(refreshToken)){

                log.debug("TokenProvider.class / validateAccessToken : JWT access 토큰 재발급");

                String newAccessToken = createAccessTokenFromRefreshToken(refreshToken);

                return newAccessToken;
            }
            else{       //리프레쉬 토큰이 유효하지 않으면 다시 로그인하라는 예외 발생
                log.info("refresh 토큰 만료 / 다시 로그인 해야함");

                return null;
            }
        }
    }

    /*
    유효한 refresh 토큰인지 확인
     */
    public boolean validateRefreshToken(String requestRefreshToken){

        //쿠키에서 받은 리프레시 토큰이 없음
        if(requestRefreshToken == null){
            log.debug("JwtTokenProvider.class / validateRefreshToken : refresh 토큰 없음");
            return false;
        }

        // 레디스에 리프레시 토큰이 있으면
        if(redisUtil.hasKey(requestRefreshToken) && redisUtil.getData(requestRefreshToken).equals("refresh-token")){
            log.debug("JwtTokenProvider.class / validateRefreshToken : refresh 토큰이 유효함");
            return true;
        }

        log.debug("JwtTokenProvider.class / validateRefreshToken : refresh 토큰이 유효하지 않음");
        return false;
    }

    /*
    refresh 토큰으로 새로운 access 토큰 발급
     */
    public String createAccessTokenFromRefreshToken(String refreshToken){

        Claims claims = getTokenClaims(refreshToken);

        Long id = Long.parseLong(claims.getSubject());                  // 멤버 PK
        String memberRole = claims.get(AUTHORITIES_KEY).toString();     // 멤버 권한

        return createAccessToken(memberRole, id);
    }

    /*
    access 토큰 폐기 (redis에 블랙리스트로 등록)
     */
    public void invalidateAccessToken(String accessToken){

        Claims claims = getTokenClaims(accessToken);

        long expTime = claims.getExpiration().getTime();     //토큰의 만료 시각
        long now = new Date().getTime();                     //현재 시각

        // 남은 엑세스토큰 유효 시간
        long remainTime = expTime - now;        // 테스트 사이트 : https://currentmillis.com/

        //레디스에 블랙리스트 등록
        redisUtil.setData(accessToken, "logout", remainTime);
    }

    /*
    refresh 토큰 폐기 (redis에서 삭제)
     */
    public void invalidateRefreshToken(String refreshToken){

        if(refreshToken != null){
            redisUtil.deleteData(refreshToken);
        }
    }

}
