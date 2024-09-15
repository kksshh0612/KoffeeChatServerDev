package teamkiim.koffeechat.domain.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.email.domain.EmailAuth;
import teamkiim.koffeechat.domain.email.dto.request.AuthCodeCheckServiceRequest;
import teamkiim.koffeechat.domain.email.repository.EmailAuthRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailService {

    private final EmailAuthRepository emailAuthRepository;
    private final EmailSendService emailSendService;

    /**
     * 회원가입 시 인증 코드 이메일 전송
     *
     * @param email 전송할 이메일
     */
    @Transactional
    public void sendEmailAuthCode(String email) {

        EmailAuth emailAuth = new EmailAuth(email);

        emailAuthRepository.save(emailAuth);
        emailSendService.sendEmail(emailAuth);

        log.info("Returning response to client on thread {}", Thread.currentThread().getName());
    }

    /**
     * 회원가입 시 이메일 인증
     *
     * @param authCodeCheckServiceRequest 이메일 인증 dto
     */
    @Transactional
    public void checkEmailAuthCode(AuthCodeCheckServiceRequest authCodeCheckServiceRequest) {

        EmailAuth emailAuth = emailAuthRepository.findByEmailAndCode(authCodeCheckServiceRequest.getEmail(), authCodeCheckServiceRequest.getCode())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_CODE_NOT_MATCH));

        emailAuthRepository.delete(emailAuth);
    }
}
