package teamkiim.koffeechat.email.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthCodeCheckRequest {

    private String email;
    private String code;
}
