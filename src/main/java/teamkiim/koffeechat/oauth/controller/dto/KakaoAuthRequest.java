package teamkiim.koffeechat.oauth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.oauth.dto.request.KakaoAuthServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAuthRequest {

    private String clientId;
    private String redirectUri;
    private String code;
    private String clientSecret;

    public KakaoAuthServiceRequest toServiceRequest(){
        return KakaoAuthServiceRequest.builder()
                .clientId(this.clientId)
                .redirectUri(this.redirectUri)
                .code(this.code)
                .clientSecret(this.clientSecret)
                .build();
    }
}
