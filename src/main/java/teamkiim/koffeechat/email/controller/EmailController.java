package teamkiim.koffeechat.email.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.email.dto.request.AuthCodeCheckRequest;
import teamkiim.koffeechat.email.dto.request.EmailAuthRequest;
import teamkiim.koffeechat.email.service.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Tag(name = "이메일 API")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-auth-code")
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

    @PostMapping("/check-auth-code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 전송 완료"),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "인증 코드가 불일치하는 경우",
                            value = "{\"code\": 400, \"message\": \"인증 코드가 일치하지 않습니다.\"}")}
            ))
    })
    public ResponseEntity<?> checkAuthCode(@Valid @RequestBody AuthCodeCheckRequest authCodeCheckRequest){

        return emailService.checkEmailAuthCode(authCodeCheckRequest);
    }
}
