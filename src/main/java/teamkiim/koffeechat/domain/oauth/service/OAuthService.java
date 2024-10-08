package teamkiim.koffeechat.domain.oauth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.domain.MemberRole;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.oauth.dto.request.GoogleAuthServiceRequest;
import teamkiim.koffeechat.domain.oauth.dto.request.KakaoAuthServiceRequest;
import teamkiim.koffeechat.domain.oauth.dto.request.SaveSocialLoginMemberInfoServiceRequest;
import teamkiim.koffeechat.global.cookie.CookieProvider;
import teamkiim.koffeechat.global.jwt.JwtTokenProvider;
import teamkiim.koffeechat.global.redis.util.RedisUtil;

import java.io.File;
import java.nio.file.Paths;
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

    @Value("${file-path}")
    private String baseFilePath;

    @Value("${basic-profile-image-url}")
    private String basicProfileImageUrl;

    /**
     * Access Token으로 네이버 회원 정보 조회
     * @param accessToken 네이버 인증 서버에서 발급해준 Access Token
     * @return SocialMemberInfoResponse
     */
    public ResponseEntity<?> getMemberInfoFromNaver(String accessToken){

        String requestURL = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(requestURL, HttpMethod.GET, entity, String.class);

        return response;
    }

    /**
     * Access Token으로 카카오 회원 정보 조회
     * @param accessToken 카카오 인증 서버에서 발급해준 Access Token
     * @return SocialMemberInfoResponse
     */
    public ResponseEntity<?> getMemberInfoFromKakao(String accessToken){

        String requestURL = "https://kapi.kakao.com/v2/user/me";        //https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(requestURL, HttpMethod.GET, entity, String.class);

        return response;
    }

    /**
     * Access Token으로 구글 회원 정보 조회
     * @param accessToken 구글 인증 서버에서 발급해준 Access Token
     * @return SocialMemberInfoResponse
     */
    public ResponseEntity<?> getMemberInfoFromGoogle(String accessToken){

        String requestURL = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(requestURL, HttpMethod.GET, entity, String.class);

        return response;
    }

    /**
     * 카카오 인증 코드로 Access / Refresh 토큰 발급
     * @param kakaoAuthServiceRequest 토큰을 발급받기 위해 카카오 인증 서버로 보내야 할 정보들이 담긴 dto
     * @return 구글 인증 서버로 부터 받은 Access, Refresh Token 정보
     */
    public ResponseEntity<?> getKakaoJWT(KakaoAuthServiceRequest kakaoAuthServiceRequest){

        String requestUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", kakaoAuthServiceRequest.getClientId());
        requestBody.add("redirect_uri", kakaoAuthServiceRequest.getRedirectUri());
        requestBody.add("code", kakaoAuthServiceRequest.getCode());
        requestBody.add("client_secret", kakaoAuthServiceRequest.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class);

        return response;
    }

    /**
     * 구글 인증 코드로 Access / Refresh 토큰 발급
     * @param googleAuthServiceRequest 토큰을 발급받기 위해 구글 인증 서버로 보내야 할 정보들이 담긴 dto
     * @return 구글 인증 서버로 부터 받은 Access, Refresh Token 정보
     */
    public ResponseEntity<?> getGoogleJWT(GoogleAuthServiceRequest googleAuthServiceRequest){

        String requestUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", googleAuthServiceRequest.getClientId());
        requestBody.add("redirect_uri", googleAuthServiceRequest.getRedirectUri());
        requestBody.add("code", googleAuthServiceRequest.getCode());
        requestBody.add("client_secret", googleAuthServiceRequest.getClientSecret());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class);

        return response;
    }

    /**
     * 소셜 로그인 회원 정보로 회원가입/로그인
     * @param memberInfoServiceRequest 소셜 로그인 회원의 email, nickname 정보
     * @param response HttpServletResponse
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> loginOrSignUpSocialMember(SaveSocialLoginMemberInfoServiceRequest memberInfoServiceRequest, HttpServletResponse response){

        // 만약 가입된 이메일이 있다면 로그인 처리
        Optional<Member> member = memberRepository.findByEmail(memberInfoServiceRequest.getEmail());

        if(member.isPresent()){         // 로그인
            loginSocialMember(member.get(), response);
        }
        else{                           // 회원가입
            Member joinMember = signUpSocialMember(memberInfoServiceRequest);
            loginSocialMember(joinMember, response);
        }

        return ResponseEntity.ok("로그인/회원가입 성공");
    }

    /*
    소셜 로그인 회원의 로그인
     */
    private void loginSocialMember(Member member,HttpServletResponse response){

        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberRole().toString(), member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberRole().toString(), member.getId());

        // 레디스 세팅
        redisUtil.setData(refreshToken, "refresh-token", refreshTokenExpTime);

        // 쿠키 세팅
        cookieProvider.setCookie(accessTokenName, accessToken, false, response);
        cookieProvider.setCookie(refreshTokenName, refreshToken, false, response);
    }

    /*
    소셜 로그인 회원의 회원가입
     */
    private Member signUpSocialMember(SaveSocialLoginMemberInfoServiceRequest memberInfoSaveRequest){

        Member member = Member.builder()
                .email(memberInfoSaveRequest.getEmail())
                .password(null)
                .nickname(memberInfoSaveRequest.getNickname())
                .memberRole(MemberRole.TEMP)
                .profileImageUrl(baseFilePath + File.separator + basicProfileImageUrl)
                .build();

        memberRepository.save(member);

        return member;
    }
}