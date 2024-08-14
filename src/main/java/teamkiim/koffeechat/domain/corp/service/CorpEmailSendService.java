package teamkiim.koffeechat.domain.corp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.email.domain.EmailAuth;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorpEmailSendService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String hostAddress;                  // 메일을 보내는 호스트 이메일 주소

    @Async
    public void sendEmail(Member member, EmailAuth emailAuth) {
        log.info("Starting to send email to {} on thread {}", emailAuth.getEmail(), Thread.currentThread().getName());

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(hostAddress);
            mimeMessageHelper.setTo(emailAuth.getEmail());
            mimeMessageHelper.setSubject("[KoffeChat] 이메일 인증 서비스");

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<!DOCTYPE html>");
            emailBody.append("<html lang='ko'>");
            emailBody.append("<head>");
            emailBody.append("<meta charset='UTF-8'>");
            emailBody.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            emailBody.append("<title>이메일 인증</title>");
            emailBody.append("</head>");
            emailBody.append("<body style='font-family: Arial, sans-serif; background-color: #f3f4f6; margin: 0; padding: 0;'>");
            emailBody.append("<div style='max-width: 600px; margin: 40px auto; background-color: #ffffff; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); border-radius: 10px; overflow: hidden;'>");
            emailBody.append("<div style='background-color: #7c3aed; color: white; padding: 25px; text-align: center;'>");
            emailBody.append("<h1 style='margin: 0; font-size: 26px; font-weight: bold;'>KoffeeChat 이메일 인증</h1>");
            emailBody.append("</div>");
            emailBody.append("<div style='padding: 40px; text-align: center;'>");
            emailBody.append("<h2 style='color: #333; font-size: 22px; margin-top: 0;'>안녕하세요, " + member.getNickname() + " 님</h2>");
            emailBody.append("<p style='color: #555; font-size: 16px; line-height: 1.6; margin: 20px 0;'><strong>[KoffeeChat]</strong> 이메일 인증 서비스 입니다.</p>");
            emailBody.append("<p style='color: #555; font-size: 16px; line-height: 1.6;'>홈페이지로 돌아가서 다음 인증코드를 입력해주세요.</p>");
            emailBody.append("<div style='display: inline-block; margin: 30px 0; padding: 15px 30px; background-color: #e3f2fd; border: 2px solid #7c3aed; border-radius: 5px; font-size: 20px; letter-spacing: 2px; color: #7c3aed; text-align: center;'>" + emailAuth.getCode() + "</div>");
            emailBody.append("</div>");
            emailBody.append("<div style='background-color: #f1f3f4; color: #555; text-align: center; padding: 20px; font-size: 12px; border-top: 1px solid #ddd;'>");
            emailBody.append("<p>&copy; 2024 KoffeChat. All rights reserved.</p>");
            emailBody.append("</div>");
            emailBody.append("</div>");
            emailBody.append("</body>");
            emailBody.append("</html>");

            mimeMessageHelper.setText(emailBody.toString(), true);          //html형식으로 설정

            javaMailSender.send(mimeMessage);
            log.info("Finished sending email to {} on thread {}", emailAuth.getEmail(), Thread.currentThread().getName());

        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
