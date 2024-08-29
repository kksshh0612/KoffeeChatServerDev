package teamkiim.koffeechat.domain.email.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    @EmailApiDocument.SendAuthEmail
    public ResponseEntity<?> sendAuthEmail(@Valid @RequestBody EmailAuthRequest emailAuthRequest) {

        emailService.sendEmailAuthCode(emailAuthRequest.getEmail());

        return ResponseEntity.ok("이메일 전송 완료, 이메일을 확인해주세요");
    }

    /**
     * 이메일 인증 진행
     */
    @PostMapping("/check-auth-code")
    @EmailApiDocument.CheckAuthCode
    public ResponseEntity<?> checkAuthCode(@Valid @RequestBody AuthCodeCheckRequest authCodeCheckRequest) {

        emailService.checkEmailAuthCode(authCodeCheckRequest.toServiceRequest());

        return ResponseEntity.ok("이메일 인증 완료되었습니다.");
    }
}
