package teamkiim.koffeechat.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.auth.controller.dto.LoginRequest;
import teamkiim.koffeechat.auth.controller.dto.SignUpRequest;
import teamkiim.koffeechat.auth.service.AuthService;
import teamkiim.koffeechat.global.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "인증 API")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "사용자가 회원가입 요청을 한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json",
                            examples = {@ExampleObject(name = "회원가입 요청한 이메일과 동일한 이메일이 이미 존재하는 경우",
                                            value = "{\"code\": 409, \"message\": \"이미 회원가입된 이메일입니다.\"}")}
                    ))
    })
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest){

        return authService.signUp(signUpRequest.toServiceRequest());
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자가 로그인 요청을 한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json",
                            examples = {@ExampleObject(name = "로그인 정보가 일치하지 않는 경우",
                                    value = "{\"code\": 400, \"message\": \"이메일, 비밀번호가 일치하지 않습니다.\"}")}
                    )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                            examples = {@ExampleObject(name = "사용자가 로그인 요청을 보낸 이메일이 존재하지 않는 경우",
                                            value = "{\"code\": 404, \"message\": \"해당 회원이 존재하지 않습니다.\"}")}
                    ))
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response){

        return authService.login(loginRequest.toServiceRequest(), response);
    }

    /**
     * 로그아웃
     */

}
