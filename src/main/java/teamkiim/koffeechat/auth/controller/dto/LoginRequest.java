package teamkiim.koffeechat.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.auth.dto.request.LoginServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    private String email;
    private String password;

    public LoginServiceRequest toServiceRequest(){
        return LoginServiceRequest.builder()
                .email(this.email)
                .password(this.password)
                .build();
    }
}
