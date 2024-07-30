package teamkiim.koffeechat.domain.oauth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoAuthServiceRequest {

    private String clientId;
    private String redirectUri;
    private String code;
    private String clientSecret;
}
