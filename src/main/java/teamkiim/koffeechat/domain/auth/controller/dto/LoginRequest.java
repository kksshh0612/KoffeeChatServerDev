package teamkiim.koffeechat.domain.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.auth.dto.request.LoginServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "이메일을 입력해주세요")
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    public LoginServiceRequest toServiceRequest(){
        return LoginServiceRequest.builder()
                .email(this.email)
                .password(this.password)
                .build();
    }
}
