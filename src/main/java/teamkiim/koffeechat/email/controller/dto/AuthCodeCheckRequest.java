package teamkiim.koffeechat.email.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.email.dto.request.AuthCodeCheckServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthCodeCheckRequest {

    private String email;
    private String code;

    public AuthCodeCheckServiceRequest toServiceRequest(){
        return AuthCodeCheckServiceRequest.builder()
                .email(this.email)
                .code(this.code)
                .build();
    }
}
