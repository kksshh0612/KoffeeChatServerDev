package teamkiim.koffeechat.oauth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import teamkiim.koffeechat.global.cookie.CookieProvider;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.global.jwt.JwtTokenProvider;
import teamkiim.koffeechat.global.redis.util.RedisUtil;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.MemberRole;
import teamkiim.koffeechat.member.domain.repository.MemberRepository;
import teamkiim.koffeechat.oauth.dto.request.KakaoAuthRequest;
import teamkiim.koffeechat.oauth.dto.request.SocialMemberInfoSaveRequest;
import teamkiim.koffeechat.oauth.dto.response.SocialMemberInfoResponse;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OAuthService {

    private final MemberRepository memberRepository;
    private final CookieProvider cookieProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final RestTemplate restTemplate;

    private static final String accessTokenName = "Authorization";
    private static final String refreshTokenName = "refresh-token";

    @Value("${jwt.refresh.exp}")
    private long refreshTokenExpTime;

    /**
     * 엑세스 토큰으로 네이버 회원 정보 조회
     */
    public ResponseEntity<?> getMemberInfoFromNaver(String accessToken){

        String requestURL = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<JSONObject> response = restTemplate.exchange(requestURL, HttpMethod.GET, entity, JSONObject.class);

        String email = null, nickname = null;

        try{
            email = response.getBody().get("email").toString();
            nickname = response.getBody().get("nickname").toString();
        } catch (JSONException e){
            throw new CustomException(ErrorCode.JSON_ERROR);
        }

        return ResponseEntity.ok(new SocialMemberInfoResponse(email, nickname));
    }

    /**
     * 엑세스 토큰으로 카카오 회원 정보 조회
     */
    public ResponseEntity<?> getMemberInfoFromKakao(String accessToken){

        String requestURL = "https://kapi.kakao.com/v2/user/me";        //https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("charset", "utf-8");
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<JSONObject> response = restTemplate.exchange(requestURL, HttpMethod.GET, entity, JSONObject.class);

        String email = null, nickname = null;

        try{
            Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
            email = kakaoAccount.get("email").toString();
            Map<String, Object> memberProfile = (Map<String, Object>) kakaoAccount.get("profile");
            nickname = memberProfile.get("nickname").toString();
        } catch (JSONException e){
            throw new CustomException(ErrorCode.JSON_ERROR);
        }

        return ResponseEntity.ok(new SocialMemberInfoResponse(email, nickname));
    }

    /**
     * 엑세스 토큰으로 구글 회원 정보 조회
     */
    public ResponseEntity<?> getMemberInfoFromGoogle(String accessToken){

        String requestURL = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<JSONObject> response = restTemplate.exchange(requestURL, HttpMethod.GET, entity, JSONObject.class);

        String email = null, nickname = null;

        try{
            email = response.getBody().get("email").toString();
            nickname = response.getBody().get("name").toString();
        } catch (JSONException e){
            throw new CustomException(ErrorCode.JSON_ERROR);
        }

        return ResponseEntity.ok(new SocialMemberInfoResponse(email, nickname));
    }

    /**
     * 카카오 인증 코드로 엑세스 토큰 발급
     */
    public ResponseEntity<?> getKakaoAccessToken(KakaoAuthRequest kakaoAuthRequest){

        String requestUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        JSONObject requestBody = new JSONObject();

        try{
            requestBody.put("grant_type", "authorization_code");
            requestBody.put("client_id", kakaoAuthRequest.getClientId());
            requestBody.put("redirect_uri", kakaoAuthRequest.getRedirectUri());
            requestBody.put("code", kakaoAuthRequest.getCode());
            requestBody.put("client_secret", kakaoAuthRequest.getClientSecret());
        } catch (JSONException e){
            throw new CustomException(ErrorCode.JSON_ERROR);
        }

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), httpHeaders);

        ResponseEntity<JSONObject> response = restTemplate.exchange(requestUrl, HttpMethod.POST, entity, JSONObject.class);

        return response;
    }

    /**
     * 소셜 로그인 회원 정보로 회원가입/로그인
     */
    @Transactional
    public ResponseEntity<?> loginOrSignUpSocialMember(SocialMemberInfoSaveRequest memberInfoSaveRequest, HttpServletResponse response){

        // 만약 가입된 이메일이 있다면 로그인 처리
        Optional<Member> member = memberRepository.findByEmail(memberInfoSaveRequest.getEmail());

        if(member.isPresent()){         // 로그인
            loginSocialMember(member.get(), response);
        }
        else{                           // 회원가입
            Member joinMember = signUpSocialMember(memberInfoSaveRequest);
            loginSocialMember(joinMember, response);
        }

        return ResponseEntity.ok("로그인/회원가입 성공");
    }

    private void loginSocialMember(Member member,HttpServletResponse response){

        String accessToken = jwtTokenProvider.createAccessToken(member.getRole().toString(), member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getRole().toString(), member.getId());

        // 레디스 세팅
        redisUtil.setData(refreshToken, "refresh-token", refreshTokenExpTime);

        // 쿠키 세팅
        cookieProvider.setCookie(accessTokenName, accessToken, false, response);
        cookieProvider.setCookie(refreshTokenName, refreshToken, false, response);
    }

    private Member signUpSocialMember(SocialMemberInfoSaveRequest memberInfoSaveRequest){

        Member member = Member.builder()
                .email(memberInfoSaveRequest.getEmail())
                .password(null)
                .nickname(memberInfoSaveRequest.getNickname())
                .role(MemberRole.USER)
                .imageUrl(null)
                .socialLoginId(null)
                .build();

        memberRepository.save(member);

        return member;
    }
}