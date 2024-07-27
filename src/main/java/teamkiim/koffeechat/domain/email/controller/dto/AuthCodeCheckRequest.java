package teamkiim.koffeechat.domain.email.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.email.dto.request.AuthCodeCheckServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthCodeCheckRequest {

    @NotBlank(message = "이메일을 입력해주세요")
    private String email;
    @NotBlank(message = "인증 코드를 입력해주세요")
    private String code;

    public AuthCodeCheckServiceRequest toServiceRequest(){
        return AuthCodeCheckServiceRequest.builder()
                .email(this.email)
                .code(this.code)
                .build();
    }
}
