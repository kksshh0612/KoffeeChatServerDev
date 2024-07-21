package teamkiim.koffeechat.oauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.oauth.SocialLoginType;
import teamkiim.koffeechat.oauth.controller.dto.GoogleAuthRequest;
import teamkiim.koffeechat.oauth.controller.dto.KakaoAuthRequest;
import teamkiim.koffeechat.oauth.controller.dto.SaveSocialLoginMemberInfoRequest;
import teamkiim.koffeechat.oauth.controller.dto.SocialAccessTokenRequest;
import teamkiim.koffeechat.oauth.dto.response.SocialMemberInfoResponse;
import teamkiim.koffeechat.oauth.service.OAuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
@Tag(name = "소셜 인증 API")
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * 카카오 인증 코드로 카카오 서버에 access, refresh 토큰 요청
     */
    @PostMapping("/get-kakao-code")
    @Operation(summary = "카카오 인증 코드로 엑세스 토큰 발급", description = "카카오 인증 코드로 access, refresh 토큰을 발급받는다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = JSONObject.class))),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "외부 API로 부터 응답으로 받은 JSON 데이터에 문제가 있을 경우",
                            value = "{\"code\": 500, \"message\": \"소셜 로그인 사용자 정보 json 파싱 에러\"}")}
            ))
    })
    public ResponseEntity<?> getKakaoAccessTokenFromCode(@Valid @RequestBody KakaoAuthRequest kakaoAuthRequest){

        return oAuthService.getKakaoJWT(kakaoAuthRequest.toServiceRequest());
    }

    /**
     * 구글 인증 코드로 구글 서버에 access, refresh 토큰 요청
     */
    @PostMapping("/get-google-code")
    @Operation(summary = "구글 인증 코드로 엑세스 토큰 발급", description = "카카오 인증 코드로 access, refresh 토큰을 발급받는다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = JSONObject.class))),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "외부 API로 부터 응답으로 받은 JSON 데이터에 문제가 있을 경우",
                            value = "{\"code\": 500, \"message\": \"소셜 로그인 사용자 정보 json 파싱 에러\"}")}
            ))
    })
    public ResponseEntity<?> getGoogleAccessTokenFromCode(@Valid @RequestBody GoogleAuthRequest googleAuthRequest){

        return oAuthService.getGoogleJWT(googleAuthRequest.toServiceRequest());
    }

    /**
     * 소셜 로그인 인증 서버로부터 발급받은 Access 토큰을 이용하여 소셜 로그인 회원의 회원정보 조회
     */
    @PostMapping("/get-member-info")
    @Operation(summary = "소셜 로그인 회원 정보 조회", description = "소셜 로그인 서버 엑세스 토큰을 이용해 사용자 정보를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SocialMemberInfoResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "외부 API로 부터 응답으로 받은 JSON 데이터에 문제가 있을 경우",
                            value = "{\"code\": 500, \"message\": \"소셜 로그인 사용자 정보 json 파싱 에러\"}")}
            ))
    })
    public ResponseEntity<?> getAccessTokenAndGetMemberInfo(@Valid @RequestBody SocialAccessTokenRequest socialAccessTokenRequest){

        ResponseEntity<?> response;

        switch (socialAccessTokenRequest.getSocialLoginType()){
            case NAVER -> response = oAuthService.getMemberInfoFromNaver(socialAccessTokenRequest.getAccessToken());
            case KAKAO -> response = oAuthService.getMemberInfoFromKakao(socialAccessTokenRequest.getAccessToken());
            case GOOGLE -> response = oAuthService.getMemberInfoFromGoogle(socialAccessTokenRequest.getAccessToken());
            default -> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("시용자 정보를 불러오는 중 에러가 발생했습니다.");
        }

        return response;
    }

    /**
     * 소셜 로그인 회원의 로그인/회원가입
     */
    @PostMapping("/login")
    @Operation(summary = "소셜 로그인 회원 로그인/회원가입", description = "회원의 이메일, 닉네임 정보로 로그인/회원가입을 진행한다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "로그인/회원가입 성공") })
    public ResponseEntity<?> socialLogin(@Valid @RequestBody SaveSocialLoginMemberInfoRequest saveSocialLoginMemberInfoRequest,
                                                    HttpServletResponse response){

        return oAuthService.loginOrSignUpSocialMember(saveSocialLoginMemberInfoRequest.toServiceRequest(), response);
    }
}
