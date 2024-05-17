package teamkiim.koffeechat.oauth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoAuthRequest {

    private String clientId;
    private String redirectUri;
    private String code;
    private String clientSecret;
}
