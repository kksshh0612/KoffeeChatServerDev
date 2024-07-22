package teamkiim.koffeechat.domain.email.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.email.controller.dto.AuthCodeCheckRequest;
import teamkiim.koffeechat.domain.email.dto.request.EmailAuthRequest;
import teamkiim.koffeechat.domain.email.service.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Tag(name = "이메일 API")
public class EmailController {

    private final EmailService emailService;

    /**
     * 회원가입 시 이메일 인증 메세지 전송
     */
    @PostMapping("/send-auth-code")
    @Operation(summary = "회원가입 시 이메일 인증 메세지 전송", description = "회원가입 시 인증 이메일을 전송한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 전송 완료"),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "이메일이 정상적으로 전송되지 않은 경우",
                            value = "{\"code\": 500, \"message\": \"이메일 전송에 실패했습니다. 이메일이 올바른지 확인해주세요.\"}")}
            ))
    })
    public ResponseEntity<?> sendAuthEmail(@Valid @RequestBody EmailAuthRequest emailAuthRequest){

        return emailService.sendEmailAuthCode(emailAuthRequest.getEmail());
    }

    /**
     * 이메일 인증 진행
     */
    @PostMapping("/check-auth-code")
    @Operation(summary = "회원가입 시 이메일로 전송한 코드 확인", description = "회원가입 시 이메일로 전송한 코드를 확인한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 전송 완료"),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "인증 코드가 불일치하는 경우",
                            value = "{\"code\": 400, \"message\": \"인증 코드가 일치하지 않습니다.\"}")}
            ))
    })
    public ResponseEntity<?> checkAuthCode(@Valid @RequestBody AuthCodeCheckRequest authCodeCheckRequest){

        return emailService.checkEmailAuthCode(authCodeCheckRequest.toServiceRequest());
    }
}
