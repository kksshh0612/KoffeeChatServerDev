package teamkiim.koffeechat.domain.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.domain.auth.controller.dto.LoginRequest;
import teamkiim.koffeechat.domain.auth.controller.dto.SignUpRequest;
import teamkiim.koffeechat.domain.auth.dto.TokenDto;
import teamkiim.koffeechat.domain.auth.service.AuthService;
import teamkiim.koffeechat.global.cookie.CookieProvider;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
@Tag(name = "인증 API")
public class AuthController {

    private final AuthService authService;
    private final CookieProvider cookieProvider;

    private static final String ACCESS_TOKEN_NAME = "Authorization";
    private static final String REFRESH_TOKEN_NAME = "refresh-token";

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    @AuthApiDocument.SignUpApiDoc
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {

        authService.signUp(signUpRequest.toServiceRequest());

        return ResponseEntity.ok("회원가입 성공");
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    @AuthApiDocument.loginApiDoc
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        TokenDto jwtTokenDto = authService.login(loginRequest.toServiceRequest());

        // 쿠키 세팅
        cookieProvider.setCookie(ACCESS_TOKEN_NAME, jwtTokenDto.getAccessToken(), false, response);
        cookieProvider.setCookie(REFRESH_TOKEN_NAME, jwtTokenDto.getRefreshToken(), false, response);

        return ResponseEntity.ok("로그인 성공");
    }

    /**
     * 로그아웃
     */
    @GetMapping("/logout")
    @AuthApiDocument.logoutApiDoc
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        authService.logout(request, response);

        return ResponseEntity.ok("로그아웃 성공");
    }

}
