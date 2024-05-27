package teamkiim.koffeechat.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.email.domain.EmailAuth;
import teamkiim.koffeechat.email.domain.repository.EmailAuthRepository;
import teamkiim.koffeechat.email.controller.dto.AuthCodeCheckRequest;
import teamkiim.koffeechat.email.dto.request.AuthCodeCheckServiceRequest;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailAuthRepository emailAuthRepository;

    @Value("${spring.mail.username}")
    private String hostAddress;                           // 메일을 보내는 호스트 이메일 주소

    /**
     * 회원가입 시 인증 코드 이메일 전송
     * @param email 전송할 이메일
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> sendEmailAuthCode(String email){

        EmailAuth emailAuth = new EmailAuth(email);

        emailAuthRepository.save(emailAuth);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(hostAddress);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[KoffeChat] 이메일 인증 서비스");

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<html><body style='font-size: 14px;'>");          // 폰트 크기 설정
            emailBody.append("안녕하세요 <strong>[KoffeChat]</strong> 이메일 인증 서비스 입니다. &#x1F648;<br><br>");
            emailBody.append("홈페이지로 돌아가서 다음 문자열을 입력하세요.<br><br>");
            emailBody.append(emailAuth.getCode());
            emailBody.append("</body></html>");

            mimeMessageHelper.setText(emailBody.toString(), true);          //html형식으로 설정

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e){
            throw new CustomException(ErrorCode.CANNOT_SEND_EMAIL);
        }

        return ResponseEntity.ok("이메일 전송 완료, 이메일을 확인해주세요");
    }

    /**
     * 회원가입 시 이메일 인증
     * @param authCodeCheckServiceRequest 이메일 인증 dto
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> checkEmailAuthCode(AuthCodeCheckServiceRequest authCodeCheckServiceRequest){

        EmailAuth emailAuth = emailAuthRepository.findByEmailAndCode(authCodeCheckServiceRequest.getEmail(), authCodeCheckServiceRequest.getCode())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_CODE_NOT_MATCH));

        emailAuthRepository.delete(emailAuth);

        return ResponseEntity.ok("이메일 인증 완료되었습니다.");
    }
}
