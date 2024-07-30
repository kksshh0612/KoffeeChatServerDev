package teamkiim.koffeechat.domain.email.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthCodeCheckServiceRequest {

    private String email;
    private String code;
}
