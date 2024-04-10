package teamkiim.koffeechat.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequest {

    private String email;
    private String password;
    private String nickname;
}
