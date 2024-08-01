package teamkiim.koffeechat.domain.email.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이메일 인증 메시지 전송 Request")
public class EmailAuthRequest {

    @Schema(description = "사용자 이메일", example = "koffeechat@naver.com")
    private String email;
}
