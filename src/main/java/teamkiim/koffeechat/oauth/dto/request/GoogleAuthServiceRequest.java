package teamkiim.koffeechat.oauth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleAuthServiceRequest {

    private String clientId;
    private String redirectUri;
    private String code;
    private String clientSecret;
}
